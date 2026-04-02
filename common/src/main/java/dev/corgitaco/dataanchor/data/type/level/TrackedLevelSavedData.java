/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.*;

public class TrackedLevelSavedData extends SavedData implements TrackedDataContainer<Level, LevelTrackedData> {
    public static final String DATA_NAME = "saved_tracked_data";
    private CompoundTag tag;


    public static Codec<TrackedLevelSavedData> makeCodec() {
        return CompoundTag.CODEC.flatXmap(tag -> {
            TrackedLevelSavedData data = new TrackedLevelSavedData(tag);
            return DataResult.success(data);
        }, data -> DataResult.success(data.save(data.serverLevel.registryAccess())));
    }

    public static final Codec<TrackedLevelSavedData> CODEC = makeCodec();

    public static final SavedDataType<TrackedLevelSavedData> TYPE = new SavedDataType<TrackedLevelSavedData>(
            DataAnchor.id(DATA_NAME),
            TrackedLevelSavedData::new,
            CODEC, DataFixTypes.LEVEL);
    private final Map<TrackedDataKey<LevelTrackedData>, LevelTrackedData> trackedDataMap = new Reference2ReferenceOpenHashMap<>();
    private final List<TickableTrackedData> tickableData = new ArrayList<>();
    private ServerLevel serverLevel;


    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (!dirty) {
            if (serverLevel instanceof InternalDirtyMarker dirtyMarker) {
                dirtyMarker.dataAnchor$clearDirty();
            }
        }
    }

    public TrackedLevelSavedData() {}

    public TrackedLevelSavedData(CompoundTag tag) {
        this.tag = tag;
    }

    public TrackedLevelSavedData init(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        dataAnchor$createTrackedData();
        for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedDataMap.entrySet()) {
            String idString = entry.getKey().getId().toString();
            if (tag.contains(idString)) {
                entry.getValue().load(tag.getCompound(idString).orElseThrow());
            }
        }
        this.tag = null;
        return this;
    }


    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag compoundTag = new CompoundTag();
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
