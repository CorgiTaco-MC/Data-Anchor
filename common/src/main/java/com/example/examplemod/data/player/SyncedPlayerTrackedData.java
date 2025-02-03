package com.example.examplemod.data.player;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.network.SyncPlayerTrackedDataS2C;
import com.example.examplemod.network.PacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract non-sealed class SyncedPlayerTrackedData extends PlayerTrackedData implements SyncedTrackedData {
    public SyncedPlayerTrackedData(TrackedDataKey<? extends SyncedPlayerTrackedData> trackedDataKey, Player player) {
        super(trackedDataKey, player);
    }

    @Override
    public void sync() {
        if (player instanceof ServerPlayer) {
            PacketBroadcaster.INSTANCE.trackingEntityAndSelf(new SyncPlayerTrackedDataS2C((TrackedDataKey<SyncedPlayerTrackedData>) trackedDataKey, writeToNetwork()), player);
        }
    }
}
