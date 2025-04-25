/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;

public class TrackedLevelSavedData extends SavedData implements TrackedDataContainer<Level, LevelTrackedData> {
    public static final String DATA_NAME = DataAnchor.MOD_ID + "_saved_tracked_data";

    private final Map<TrackedDataKey<LevelTrackedData>, LevelTrackedData> trackedDataMap = new Reference2ReferenceOpenHashMap<>();
    private final List<TickableTrackedData> tickableData = new ArrayList<>();
    private final ServerLevel serverLevel;


    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (!dirty) {
            if (serverLevel instanceof InternalDirtyMarker dirtyMarker) {
                dirtyMarker.dataAnchor$clearDirty();
            }
        }
    }

    private TrackedLevelSavedData(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        dataAnchor$createTrackedData();
    }

    public static TrackedLevelSavedData get(ServerLevel world) {
        DimensionDataStorage data = world.getDataStorage();

        Codec<TrackedLevelSavedData> codec = RecordCodecBuilder.create(trackedLevelSavedDataInstance ->
                trackedLevelSavedDataInstance.group(
                        Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC).fieldOf("map").forGetter(trackedLevelData -> {
                            Map<String, CompoundTag> map = new HashMap<>();
                            for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedLevelData.trackedDataMap.entrySet()) {
                                map.put(entry.getKey().getId().toString(), entry.getValue().save());
                            }
                            return map;
                        })
                ).apply(trackedLevelSavedDataInstance, map -> {
                    TrackedLevelSavedData trackedLevelSavedData = new TrackedLevelSavedData(world);
                    for (Map.Entry<String, CompoundTag> entry : map.entrySet()) {
                        trackedLevelSavedData.trackedDataMap.forEach((key, value) -> {
                            if (key.getId().toString().equals(entry.getKey())) {
                                value.load(entry.getValue());
                            }
                        });
                    }
                    return trackedLevelSavedData;
                })
        );

        return data.computeIfAbsent(new SavedDataType<>(DATA_NAME, () -> new TrackedLevelSavedData(world), codec, DataFixTypes.LEVEL));
    }

    @Override
    public <E extends LevelTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
        LevelTrackedData levelTrackedData = this.trackedDataMap.get(key);
        if (levelTrackedData == null) {
            return Optional.empty();
        }
        return Optional.of((E) levelTrackedData);
    }

    @Override
    public void dataAnchor$createTrackedData() {
        TrackedDataRegistries.LEVEL.factories().forEach((key, factory) -> {
            LevelTrackedData trackedData = factory.create(key, this.serverLevel);
            if (trackedData instanceof ServerTrackedData) {
                if (trackedData instanceof TickableTrackedData tickableData) {
                    this.tickableData.add(tickableData);
                }
                trackedDataMap.put(key, trackedData);
            }
        });
    }

    @Override
    public Collection<TrackedDataKey<LevelTrackedData>> dataAnchor$getTrackedDataKeys() {
        return this.trackedDataMap.keySet();
    }
}
