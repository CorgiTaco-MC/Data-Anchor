/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.test.data.entity;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.SyncedEntityTrackedData;
import net.minecraft.world.entity.Entity;

public class TestSyncedEntityTrackedData extends SyncedEntityTrackedData implements TickableTrackedData {

    private int yum = 0;

    public TestSyncedEntityTrackedData(TrackedDataKey<? extends SyncedEntityTrackedData> trackedDataKey, Entity entity) {
        super(trackedDataKey, entity);
    }

    @Override
    public void tick() {
        if (!entity.level().isClientSide()) {
            setYum(this.yum + 1);

            DataAnchor.LOGGER.info("Server entity yum: %s".formatted(this.yum));
        } else {
            DataAnchor.LOGGER.info("Client entity yum: %s".formatted(this.yum));
        }
    }

    public void setYum(int yum) {
        this.yum = yum;
        sync();
    }
}
