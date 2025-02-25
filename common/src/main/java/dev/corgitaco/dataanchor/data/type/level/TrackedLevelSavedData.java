package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.DirtyMarker;
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
        for (Map.Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : trackedDataMap.entrySet()) {
            CompoundTag save = entry.getValue().save();
            if (save != null) {
                compoundTag.put(entry.getKey().getId().toString(), save);
            }
        }
        return compoundTag;
    }

    @Override
    public <E extends LevelTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        LevelTrackedData levelTrackedData = this.trackedDataMap.get(key);
        if (levelTrackedData == null) {
            return Optional.empty();
        }
        return Optional.of((E) levelTrackedData);
    }

    @Override
    public void create() {
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
    public Collection<TrackedDataKey<LevelTrackedData>> getKeys() {
        return this.trackedDataMap.keySet();
    }
}
