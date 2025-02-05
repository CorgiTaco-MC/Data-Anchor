package com.example.examplemod.data.level;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.*;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TrackedLevelSavedData extends SavedData implements TrackedDataContainer<Level, LevelTrackedData> {
    public static final String DATA_NAME = ExampleMod.MOD_ID + "_saved_tracked_data";

    private final Map<TrackedDataKey<LevelTrackedData>, LevelTrackedData> trackedDataMap = new Reference2ReferenceOpenHashMap<>();
    private final List<TickableTrackedData> tickableData = new ArrayList<>();
    private final ServerLevel serverLevel;


    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (!dirty) {
            if (serverLevel instanceof DirtyMarker dirtyMarker) {
                dirtyMarker.clearDirty();
            }
        }
    }

    private TrackedLevelSavedData(ServerLevel serverLevel, CompoundTag tag) {
        this.serverLevel = serverLevel;
        create();
        for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedDataMap.entrySet()) {
            entry.getValue().load(tag.getCompound(entry.getKey().getId().toString()));
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
        CompoundTag compoundTag1 = new CompoundTag();
        for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedDataMap.entrySet()) {
            compoundTag1.put(entry.getKey().getId().toString(), entry.getValue().save());
        }
        compoundTag.put("TrackedData", compoundTag1);
        return compoundTag1;
    }

    @Override
    public <E extends LevelTrackedData> E get(TrackedDataKey<E> key) {
        return (E) this.trackedDataMap.get(key);
    }

    @Override
    public void create() {
        TrackedDataRegistries.LEVEL.factories().forEach((key, factory) -> {
            LevelTrackedData trackedData = factory.create(key, this.serverLevel);
            if (trackedData == null) {
                throw new IllegalArgumentException("Null LevelTrackedData factories are NOT allowed. Found null level LevelTrackedData for key \"%s\"".formatted(key.getId()));
            }

            if (trackedData instanceof TickableTrackedData tickableData) {
                this.tickableData.add(tickableData);
            }

            trackedDataMap.put(key, trackedData);
        });
    }

    @Override
    public Collection<TrackedDataKey<LevelTrackedData>> getKeys() {
        return this.trackedDataMap.keySet();
    }
}
