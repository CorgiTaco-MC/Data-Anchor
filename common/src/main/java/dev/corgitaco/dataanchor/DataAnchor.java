package dev.corgitaco.dataanchor;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.blockentity.network.SyncBlockEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.chunk.network.SyncLevelChunkTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.entity.network.SyncEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import dev.corgitaco.dataanchor.test.data.TestSyncedBlockEntityTrackedData;
import dev.corgitaco.dataanchor.test.data.chunk.TestSyncedLevelChunkTrackedData;
import dev.corgitaco.dataanchor.test.data.level.TestSyncedLevelTrackedData;
import dev.corgitaco.dataanchor.test.data.player.TestSyncedPlayerTrackedData;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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

//    public static final TrackedDataKey<TestSyncedLevelTrackedData> TEST_LEVEL_DATA = TrackedDataRegistries.LEVEL.register(DataAnchor.id("test"), TestSyncedLevelTrackedData.class, TestSyncedLevelTrackedData::new);
//    public static final TrackedDataKey<TestSyncedPlayerTrackedData> TEST_PLAYER_DATA = TrackedDataRegistries.ENTITY.register(DataAnchor.id("player"), TestSyncedPlayerTrackedData.class, (key, obj) -> {
//        if (obj instanceof Player player) {
//            return new TestSyncedPlayerTrackedData(key, player);
//        }
//        return null;
//    });
//    public static final TrackedDataKey<TestSyncedLevelChunkTrackedData> TEST_CHUNK_DATA = TrackedDataRegistries.CHUNK.register(DataAnchor.id("test"), TestSyncedLevelChunkTrackedData.class, (key, obj) -> {
//        if (obj instanceof LevelChunk chunk) {
//            return new TestSyncedLevelChunkTrackedData(key, chunk);
//        }
//        return null;
//    });

    public static final TrackedDataKey<TestSyncedBlockEntityTrackedData> TEST_BLOCK_ENTITY_DATA = TrackedDataRegistries.BLOCK_ENTITY.register(DataAnchor.id("test"), TestSyncedBlockEntityTrackedData.class, TestSyncedBlockEntityTrackedData::new);


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