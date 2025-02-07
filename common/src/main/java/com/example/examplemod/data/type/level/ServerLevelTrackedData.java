package com.example.examplemod.data.type.level;

import com.example.examplemod.data.DirtyMarker;
import com.example.examplemod.data.ServerTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerLevel;

public abstract non-sealed class ServerLevelTrackedData extends LevelTrackedData implements DirtyMarker, ServerTrackedData {
    private boolean dirty = false;

    public ServerLevelTrackedData(TrackedDataKey<ServerLevelTrackedData> trackedDataKey, ServerLevel chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public ServerLevel get() {
        return (ServerLevel) super.get();
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public void clearDirty() {
        dirty = false;
    }
}
