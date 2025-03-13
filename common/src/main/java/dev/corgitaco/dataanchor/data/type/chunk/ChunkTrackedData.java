package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract sealed class ChunkTrackedData implements TrackedData<ChunkAccess>, DirtyMarker permits LevelChunkTrackedData, ProtoChunkTrackedData {

    protected transient final TrackedDataKey<? extends ChunkTrackedData> trackedDataKey;
    protected transient final ChunkAccess chunk;

    public ChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ChunkAccess chunk) {
        this.trackedDataKey = trackedDataKey;
        this.chunk = chunk;
    }

    @Override
    public ChunkAccess get() {
        return this.chunk;
    }

    @Override
    public void dataAnchor$markDirty() {
        chunk.setUnsaved(true);
    }

    @Override
    public void dataAnchor$clearDirty() {

    }
}
