/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.network.SyncEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
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
