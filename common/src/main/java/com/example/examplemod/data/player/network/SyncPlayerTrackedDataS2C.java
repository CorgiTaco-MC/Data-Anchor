package com.example.examplemod.data.player.network;

import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.SyncedPlayerTrackedData;
import com.example.examplemod.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncPlayerTrackedDataS2C(TrackedDataKey<SyncedPlayerTrackedData> playerTrackedDataTrackedDataKey, CompoundTag tag) implements Packet {

    public SyncPlayerTrackedDataS2C(FriendlyByteBuf buf) {
        this(TrackedDataKey.fromID(buf.readResourceLocation()), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(playerTrackedDataTrackedDataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (player instanceof TrackedDataAccess access) {
            SyncedPlayerTrackedData data = (SyncedPlayerTrackedData) access.get(this.playerTrackedDataTrackedDataKey);
            data.readFromNetwork(tag);
        }
    }
}
