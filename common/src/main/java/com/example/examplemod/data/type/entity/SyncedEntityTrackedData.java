package com.example.examplemod.data.type.entity;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.type.entity.network.SyncEntityTrackedDataS2C;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public abstract non-sealed class SyncedEntityTrackedData extends EntityTrackedData implements SyncedTrackedData {

    public SyncedEntityTrackedData(TrackedDataKey<? extends SyncedEntityTrackedData> trackedDataKey, Entity entity) {
        super(trackedDataKey, entity);
    }

    @Override
    public void sync() {
        if (entity.level() instanceof ServerLevel) {
            S2CPacketBroadcaster.S2C.trackingEntity(new SyncEntityTrackedDataS2C(entity.getId(), this.trackedDataKey, writeToNetwork()), entity);
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        S2CPacketBroadcaster.S2C.sendToPlayer(new SyncEntityTrackedDataS2C(entity.getId(), this.trackedDataKey, writeToNetwork()), player);
    }
}
