package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.Level;

public abstract sealed class LevelTrackedData implements TrackedData<Level> permits ServerLevelTrackedData, SyncedLevelTrackedData {

    protected final TrackedDataKey<? extends LevelTrackedData> trackedDataKey;
    protected final Level level;

    public LevelTrackedData(TrackedDataKey<? extends LevelTrackedData> trackedDataKey, Level level) {
        this.trackedDataKey = trackedDataKey;
        this.level = level;
    }

    @Override
    public Level get() {
        return this.level;
    }
}
