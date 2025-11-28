/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.neoforge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import dev.corgitaco.dataanchor.network.register.C2SPacketRegister;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoService({C2SPacketBroadcaster.class, C2SPacketRegister.class})
public class C2SNeoForgePacketBroadcaster implements C2SPacketBroadcaster, C2SPacketRegister {

    @Override
    public <MSG extends Packet> void sendToServer(MSG msg) {
        PacketDistributor.sendToServer(msg);
    }

    @Override
    public void registerPackets() {
        // Empty, we use NeoForge's packet registration event instead.
    }
}
