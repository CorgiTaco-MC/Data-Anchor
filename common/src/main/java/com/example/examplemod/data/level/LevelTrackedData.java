package com.example.examplemod.data.level;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.world.level.Level;

public abstract class LevelTrackedData implements TrackedData<Level> {

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
