/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public sealed abstract class PlayerTrackedData extends EntityTrackedData permits ServerPlayerTrackedData, SyncedPlayerTrackedData {

    protected final TrackedDataKey<? extends PlayerTrackedData> trackedDataKey;
    protected final Player player;
    private final boolean persistent;

    public PlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, Player player) {
        this(trackedDataKey, player, false);
    }

    public PlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, Player player, boolean persistent) {
        super(trackedDataKey, player);
        this.trackedDataKey = trackedDataKey;
        this.player = player;
        this.persistent = persistent;
    }

    public void respawn(ServerPlayer oldPlayer, boolean keepEverything) {
        if (persistent) {
            TrackedDataRegistries.ENTITY.get(this.trackedDataKey, oldPlayer).ifPresent(data -> this.load(data.save()));
        }
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

    @Override
    public Player get() {
        return this.player;
    }
}
