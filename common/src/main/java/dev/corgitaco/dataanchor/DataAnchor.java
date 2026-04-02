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
import dev.corgitaco.dataanchor.data.type.entity.SyncedPlayerTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.network.SyncEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import dev.corgitaco.dataanchor.test.data.TestSyncedBlockEntityTrackedData;
import dev.corgitaco.dataanchor.test.data.chunk.TestSyncedLevelChunkTrackedData;
import dev.corgitaco.dataanchor.test.data.level.TestSyncedLevelTrackedData;
import dev.corgitaco.dataanchor.test.data.player.TestSyncedPlayerTrackedData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
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

    public static final TrackedDataKey<TestSyncedPlayerTrackedData> TEST_PLAYER_TRACKED_DATA_KEY = TrackedDataRegistries.ENTITY.register(id("test_player_tracked_data"), TestSyncedPlayerTrackedData.class, (key, entity) -> {
        if (entity instanceof Player player) {
            return new TestSyncedPlayerTrackedData(key, player);
        }

        return null;
    });

    public static final TrackedDataKey<TestSyncedBlockEntityTrackedData> TEST_SYNCED_BLOCK_ENTITY_TRACKED_DATA_KEY = TrackedDataRegistries.BLOCK_ENTITY.register(id("test_block_entity_tracked_data"), TestSyncedBlockEntityTrackedData.class, (key, blockEntity) -> {
        if (blockEntity instanceof EnderChestBlockEntity enderChestBlockEntity) {
            return new TestSyncedBlockEntityTrackedData(key, enderChestBlockEntity);
        }

        return null;
    });

    public static final TrackedDataKey<TestSyncedLevelTrackedData> TEST_LEVEL_TRACKED_DATA_KEY = TrackedDataRegistries.LEVEL.register(id("test_level_tracked_data"), TestSyncedLevelTrackedData.class, TestSyncedLevelTrackedData::new);

    public static final TrackedDataKey<TestSyncedLevelChunkTrackedData> TEST_SYNCED_LEVEL_CHUNK_TRACKED_DATA_KEY = TrackedDataRegistries.CHUNK.register(id("test_synced_level_chunk_tracked_data_key"), TestSyncedLevelChunkTrackedData.class, (key, chunkAccess) -> {
        if (chunkAccess instanceof LevelChunk levelChunk) {
            return new TestSyncedLevelChunkTrackedData(key, levelChunk);
        }
        return null;
    });


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

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}