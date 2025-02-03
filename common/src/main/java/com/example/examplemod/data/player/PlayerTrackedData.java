package com.example.examplemod.data.player;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public sealed abstract class PlayerTrackedData implements TrackedData permits ServerPlayerTrackedData, SyncedPlayerTrackedData {

    protected final TrackedDataKey<? extends PlayerTrackedData> trackedDataKey;
    protected final Player player;

    public PlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, Player player) {
        this.trackedDataKey = trackedDataKey;
        this.player = player;
    }

    public void onCopy(ServerPlayer oldPlayer) {
        // Do something when the player dies
    }

    public void playerJoin() {
        // Do something when the player joins
    }

    public void playerAddedToWorld() {
        // Do something when the player is added to a world regardless of action
    }

    public void addDuringCommandTeleport() {
        // Do something when the player is added to the world from a command teleport
    }

    public void addDuringPortalTeleport() {
        // Do something when the player is added to the world from a teleport
    }

    public void addRespawnedPlayer() {
        // Do something when the player is respawned
    }
}
