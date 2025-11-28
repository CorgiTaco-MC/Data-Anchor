/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network.broadcast;

import dev.corgitaco.dataanchor.util.ServiceUtil;

public interface PacketBroadcaster {
    S2CPacketBroadcaster S2C = ServiceUtil.load(S2CPacketBroadcaster.class);
    C2SPacketBroadcaster C2S = ServiceUtil.load(C2SPacketBroadcaster.class);
    BiDirectionalPacketBroadcaster BI = ServiceUtil.load(BiDirectionalPacketBroadcaster.class);
}
