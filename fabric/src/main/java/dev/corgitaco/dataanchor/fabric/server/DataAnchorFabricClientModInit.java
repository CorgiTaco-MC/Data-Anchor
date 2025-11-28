/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.fabric.server;

import dev.corgitaco.dataanchor.network.register.PacketRegister;
import net.fabricmc.api.ClientModInitializer;

public class DataAnchorFabricClientModInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PacketRegister.ALL.forEach(PacketRegister::registerPackets);
    }
}
