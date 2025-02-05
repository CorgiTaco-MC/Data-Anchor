package com.example.examplemod.data.chunk.network;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.TrackedDataRegistries;
import com.example.examplemod.data.chunk.SyncedLevelChunkTrackedData;
import com.example.examplemod.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public record SyncLevelChunkTrackedDataS2C(TrackedDataKey<SyncedLevelChunkTrackedData> dataKey, ChunkPos pos, CompoundTag tag) implements Packet {


    public SyncLevelChunkTrackedDataS2C(FriendlyByteBuf buf) {
        this((TrackedDataKey) TrackedDataKey.fromID(TrackedDataRegistries.CHUNK, buf.readResourceLocation()), new ChunkPos(buf.readInt(), buf.readInt()), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        if (!chunk.isEmpty()) {
            if (chunk instanceof TrackedDataContainer access) {
                SyncedLevelChunkTrackedData trackedData = (SyncedLevelChunkTrackedData) access.get(this.dataKey);
                trackedData.readFromNetwork(tag);
            }
        }
    }
}
