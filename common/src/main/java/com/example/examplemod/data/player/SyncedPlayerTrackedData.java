package com.example.examplemod.data.player;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.network.SyncPlayerTrackedDataS2C;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract non-sealed class SyncedPlayerTrackedData extends PlayerTrackedData implements SyncedTrackedData {
    public SyncedPlayerTrackedData(TrackedDataKey<? extends SyncedPlayerTrackedData> trackedDataKey, Player player) {
        super(trackedDataKey, player);
    }

    @Override
    public void sync() {
        if (player instanceof ServerPlayer) {
            S2CPacketBroadcaster.S2C.trackingEntityAndSelf(new SyncPlayerTrackedDataS2C((TrackedDataKey<SyncedPlayerTrackedData>) trackedDataKey, writeToNetwork()), player);
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        S2CPacketBroadcaster.S2C.sendToPlayer(new SyncPlayerTrackedDataS2C((TrackedDataKey<SyncedPlayerTrackedData>) trackedDataKey, writeToNetwork()), player);
    }
}
