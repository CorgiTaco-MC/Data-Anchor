package com.example.examplemod;

import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.chunk.network.SyncLevelChunkTrackedDataS2C;
import com.example.examplemod.data.type.entity.network.SyncEntityTrackedDataS2C;
import com.example.examplemod.data.type.level.network.SyncLevelTrackedDataS2C;
import com.example.examplemod.network.Packet;
import com.example.examplemod.network.S2CNetworkContainer;
import com.example.examplemod.test.data.chunk.TestSyncedLevelChunkTrackedData;
import com.example.examplemod.test.data.level.TestSyncedLevelTrackedData;
import com.example.examplemod.test.data.player.TestSyncedPlayerTrackedData;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
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

    public static final TrackedDataKey<TestSyncedLevelTrackedData> TEST_LEVEL_DATA = TrackedDataRegistries.LEVEL.register(ExampleMod.id("test"), TestSyncedLevelTrackedData.class, TestSyncedLevelTrackedData::new);
    public static final TrackedDataKey<TestSyncedPlayerTrackedData> TEST_PLAYER_DATA = TrackedDataRegistries.ENTITY.register(ExampleMod.id("player"), TestSyncedPlayerTrackedData.class, (key, obj) -> {
        if (obj instanceof Player player) {
            return new TestSyncedPlayerTrackedData(key, player);
        }
        return null;
    });
    public static final TrackedDataKey<TestSyncedLevelChunkTrackedData> TEST_CHUNK_DATA = TrackedDataRegistries.CHUNK.register(ExampleMod.id("test"), TestSyncedLevelChunkTrackedData.class, (key, obj) -> {
        if (obj instanceof LevelChunk chunk) {
            return new TestSyncedLevelChunkTrackedData(key, chunk);
        }
        return null;
    });

    /**
     * Initializes the mod.
     */
    public static void init() {
        registerPacketHandlers();
    }


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
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}