/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.ClientTrackedData;
import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.network.SyncEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract non-sealed class SyncedPlayerTrackedData extends PlayerTrackedData implements SyncedTrackedData, ServerTrackedData, ClientTrackedData {
    public SyncedPlayerTrackedData(TrackedDataKey<? extends SyncedPlayerTrackedData> trackedDataKey, Player player) {
        super(trackedDataKey, player);
    }

    @Override
    public void sync() {
        if (player instanceof ServerPlayer) {
            S2CPacketBroadcaster.S2C.trackingEntityAndSelf(new SyncEntityTrackedDataS2C(player.getId(), trackedDataKey, writeToNetwork()), player);
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        // SyncPacket is called directly instead.
    }

    @Override
    public Packet syncPacket() {
        return new SyncEntityTrackedDataS2C(player.getId(), trackedDataKey, writeToNetwork());
    }
}
