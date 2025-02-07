package com.example.examplemod.data.type.entity;

import com.example.examplemod.data.ServerTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerPlayer;

public non-sealed abstract class ServerPlayerTrackedData extends PlayerTrackedData implements ServerTrackedData {

    public ServerPlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, ServerPlayer player) {
        super(trackedDataKey, player);
    }

    @Override
    public ServerPlayer get() {
        return (ServerPlayer) super.get();
    }
}
