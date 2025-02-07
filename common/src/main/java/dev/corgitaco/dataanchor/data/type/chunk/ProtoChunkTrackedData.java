package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.ProtoChunk;

public abstract non-sealed class ProtoChunkTrackedData extends ChunkTrackedData implements ServerTrackedData {

    public ProtoChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ProtoChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public ProtoChunk get() {
        return (ProtoChunk) super.get();
    }
}
