package com.example.examplemod.data.chunk;

import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.world.level.chunk.ProtoChunk;

public abstract class ProtoChunkTrackedData extends ChunkTrackedData {

    public ProtoChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ProtoChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public ProtoChunk get() {
        return (ProtoChunk) super.get();
    }
}
