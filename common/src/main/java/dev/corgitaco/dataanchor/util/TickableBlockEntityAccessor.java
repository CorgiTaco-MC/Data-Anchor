/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.util;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collections;
import java.util.List;

public interface TickableBlockEntityAccessor {
    default List<BlockEntity> dataAnchor$getTickableBlockEntities() {
        return Collections.emptyList();
    }
}