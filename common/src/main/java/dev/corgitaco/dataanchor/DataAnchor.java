/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor;

import com.mojang.logging.LogUtils;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.blockentity.network.SyncBlockEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.chunk.network.SyncLevelChunkTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.entity.network.SyncEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import dev.corgitaco.dataanchor.test.data.player.TestSyncedPlayerTrackedData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class DataAnchor {

    /**
     * The mod id for Data Anchor.
     */
    public static final String MOD_ID = "dataanchor";

    /**
     * The logger for Data Anchor.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final S2CNetworkContainer NETWORK_CONTAINER = S2CNetworkContainer.of(MOD_ID);

    /**
     * Initializes the mod.
     */
    public static void init() {
        registerPacketHandlers();
    }

    private static void registerPacketHandlers() {
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncEntityTrackedDataS2C.class,
                        SyncEntityTrackedDataS2C.TYPE,
                        SyncEntityTrackedDataS2C.STREAM_CODEC,
                        SyncEntityTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncLevelChunkTrackedDataS2C.class,
                        SyncLevelChunkTrackedDataS2C.TYPE,
                        SyncLevelChunkTrackedDataS2C.STREAM_CODEC,
                        SyncLevelChunkTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncLevelTrackedDataS2C.class,
                        SyncLevelTrackedDataS2C.TYPE,
                        SyncLevelTrackedDataS2C.STREAM_CODEC,
                        SyncLevelTrackedDataS2C::handle
                )
        );
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncBlockEntityTrackedDataS2C.class,
                        SyncBlockEntityTrackedDataS2C.TYPE,
                        SyncBlockEntityTrackedDataS2C.STREAM_CODEC,
                        SyncBlockEntityTrackedDataS2C::handle
                )
        );

    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}