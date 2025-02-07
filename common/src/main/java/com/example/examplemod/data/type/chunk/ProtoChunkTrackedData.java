package com.example.examplemod.data.type.chunk;

import com.example.examplemod.data.ServerTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
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
