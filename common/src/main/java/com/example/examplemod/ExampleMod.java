package com.example.examplemod;

import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.TrackedDataRegistries;
import com.example.examplemod.data.chunk.network.SyncLevelChunkTrackedDataS2C;
import com.example.examplemod.data.level.network.SyncLevelTrackedDataS2C;
import com.example.examplemod.data.player.network.SyncPlayerTrackedDataS2C;
import com.example.examplemod.network.Packet;
import com.example.examplemod.network.S2CNetworkContainer;
import com.example.examplemod.test.data.level.TestSyncedLevelTrackedData;
import com.example.examplemod.test.data.player.TestSyncedPlayerTrackedData;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ExampleMod {

    /**
     * The mod id for  examplemod.
     */
    public static final String MOD_ID = "examplemod";

    /**
     * The logger for examplemod.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final S2CNetworkContainer NETWORK_CONTAINER = S2CNetworkContainer.of(MOD_ID);

    public static final TrackedDataKey<TestSyncedPlayerTrackedData> TEST_PLAYER_DATA = TrackedDataRegistries.PLAYER.register(ExampleMod.id("test"), TestSyncedPlayerTrackedData.class, TestSyncedPlayerTrackedData::new);
    public static final TrackedDataKey<TestSyncedLevelTrackedData> TEST_LEVEL_DATA = TrackedDataRegistries.LEVEL.register(ExampleMod.id("test"), TestSyncedLevelTrackedData.class, TestSyncedLevelTrackedData::new);

    /**
     * Initializes the mod.
     */
    public static void init() {
        registerPacketHandlers();
    }


    private static void registerPacketHandlers() {
        NETWORK_CONTAINER.registerPacketHandler("player_tracked_data",
                new Packet.Handler<>(
                        SyncPlayerTrackedDataS2C.class,
                        SyncPlayerTrackedDataS2C::write,
                        SyncPlayerTrackedDataS2C::new,
                        SyncPlayerTrackedDataS2C::handle)
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
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}