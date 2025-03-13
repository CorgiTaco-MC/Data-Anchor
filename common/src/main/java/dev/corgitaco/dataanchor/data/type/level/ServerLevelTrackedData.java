package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
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
    public void dataAnchor$markDirty() {
        dirty = true;
    }

    @Override
    public void dataAnchor$clearDirty() {
        dirty = false;
    }
}
