package com.example.examplemod.data.chunk;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.chunk.network.SyncLevelChunkTrackedDataS2C;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract class SyncedLevelChunkTrackedData extends LevelChunkTrackedData implements SyncedTrackedData {

    public SyncedLevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public void sync() {
        Level level = get().getLevel();
        if (!level.isClientSide) {
            S2CPacketBroadcaster.S2C.trackingChunk(new SyncLevelChunkTrackedDataS2C((TrackedDataKey<SyncedLevelChunkTrackedData>) trackedDataKey, chunk.getPos(), writeToNetwork()), get());
        }
    }
}
