/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerPlayer;

public non-sealed abstract class ServerPlayerTrackedData extends PlayerTrackedData implements ServerTrackedData {

    public ServerPlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, ServerPlayer player) {
        this(trackedDataKey, player, false);
    }

    public ServerPlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, ServerPlayer player, boolean persistent) {
        super(trackedDataKey, player, persistent);
    }

    @Override
    public ServerPlayer get() {
        return (ServerPlayer) super.get();
    }
}
