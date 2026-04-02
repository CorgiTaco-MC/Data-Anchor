/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.test.data;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.blockentity.SyncedBlockEntityTrackedData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TestSyncedBlockEntityTrackedData extends SyncedBlockEntityTrackedData implements TickableTrackedData {

    private int yum = 0;

    public TestSyncedBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
        super(trackedDataKey, blockEntity);
    }

    @Override
    public void tick() {
        if (!blockEntity.getLevel().isClientSide()) {
            setYum(this.yum + 1);

            DataAnchor.LOGGER.info("Server block entity yum: %s".formatted(this.yum));
        } else {
            DataAnchor.LOGGER.info("Client block entity yum: %s".formatted(this.yum));
        }
    }

    public void setYum(int yum) {
        this.yum = yum;
        sync();
        markDirty();
    }
}
