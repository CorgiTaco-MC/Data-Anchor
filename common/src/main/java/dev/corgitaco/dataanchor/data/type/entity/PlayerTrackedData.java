package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public sealed abstract class PlayerTrackedData extends EntityTrackedData permits ServerPlayerTrackedData, SyncedPlayerTrackedData {

    protected final TrackedDataKey<? extends PlayerTrackedData> trackedDataKey;
    protected final Player player;

    public PlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, Player player) {
        super(trackedDataKey, player);
        this.trackedDataKey = trackedDataKey;
        this.player = player;
    }

    public void copy(ServerPlayer oldPlayer, boolean keepEverything) {
        // Do something when the player dies
    }

    public void playerJoin() {
        // Do something when the player joins
    }

    public void playerAddedToWorld() {
        // Do something when the player is added to a world regardless of action
    }

    public void addRespawnedPlayer() {
        // Do something when the player is respawned
    }

    @Override
    public Player get() {
        return this.player;
    }
}
