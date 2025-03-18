/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.level;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
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

    private TrackedLevelSavedData(ServerLevel serverLevel, CompoundTag tag) {
        this.serverLevel = serverLevel;
        dataAnchor$createTrackedData();
        for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedDataMap.entrySet()) {
            String idString = entry.getKey().getId().toString();
            if (tag.contains(idString, 10)) {
                entry.getValue().load(tag.getCompound(idString));
            }
        }
    }

    private TrackedLevelSavedData(ServerLevel serverLevel) {
        this(serverLevel, new CompoundTag());
    }

    public static TrackedLevelSavedData get(ServerLevel world) {
        DimensionDataStorage data = world.getDataStorage();
        return data.computeIfAbsent(tag -> new TrackedLevelSavedData(world, tag), () -> new TrackedLevelSavedData(world), DATA_NAME);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedDataMap.entrySet()) {
            CompoundTag save = entry.getValue().save();
            if (save != null) {
                compoundTag.put(entry.getKey().getId().toString(), save);
            }
        }
        return compoundTag;
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
