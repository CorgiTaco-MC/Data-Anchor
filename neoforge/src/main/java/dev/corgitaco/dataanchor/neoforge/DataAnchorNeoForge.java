/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.neoforge;

import dev.corgitaco.dataanchor.DataAnchor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(DataAnchor.MOD_ID)
public class DataAnchorNeoForge {
    public DataAnchorNeoForge(IEventBus eventBus) {
        DataAnchor.init();
    }
}
