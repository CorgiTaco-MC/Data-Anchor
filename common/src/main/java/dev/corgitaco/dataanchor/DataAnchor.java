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
    
    public static final TrackedDataKey<TestSyncedPlayerTrackedData> DATA = TrackedDataRegistries.ENTITY.register(id("meow"), TestSyncedPlayerTrackedData.class, (key, obj) -> {
        if (obj instanceof Player player) {
            return new TestSyncedPlayerTrackedData(key, player);
        }

        return null;
    });

    private static void registerPacketHandlers() {
        NETWORK_CONTAINER.registerPacketHandler("entity_tracked_data",
                new Packet.Handler<>(
                        SyncEntityTrackedDataS2C.class,
                        SyncEntityTrackedDataS2C::write,
                        SyncEntityTrackedDataS2C::new,
                        SyncEntityTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler("chunk_tracked_data",
                new Packet.Handler<>(
                        SyncLevelChunkTrackedDataS2C.class,
                        SyncLevelChunkTrackedDataS2C::write,
                        SyncLevelChunkTrackedDataS2C::new,
                        SyncLevelChunkTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler("level_tracked_data",
                new Packet.Handler<>(
                        SyncLevelTrackedDataS2C.class,
                        SyncLevelTrackedDataS2C::write,
                        SyncLevelTrackedDataS2C::new,
                        SyncLevelTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler("block_entity_tracked_data",
                new Packet.Handler<>(
                        SyncBlockEntityTrackedDataS2C.class,
                        SyncBlockEntityTrackedDataS2C::write,
                        SyncBlockEntityTrackedDataS2C::new,
                        SyncBlockEntityTrackedDataS2C::handle)
        );

    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}