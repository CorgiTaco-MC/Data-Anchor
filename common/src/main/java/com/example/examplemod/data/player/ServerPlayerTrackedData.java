package com.example.examplemod.data.player;

import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.server.level.ServerPlayer;

public non-sealed abstract class ServerPlayerTrackedData extends PlayerTrackedData {
    public ServerPlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, ServerPlayer player) {
        super(trackedDataKey, player);
    }
}
