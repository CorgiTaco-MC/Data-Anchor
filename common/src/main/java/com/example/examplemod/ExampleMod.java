package com.example.examplemod;

import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedDataRegistry;
import com.example.examplemod.data.player.network.SyncPlayerTrackedDataS2C;
import com.example.examplemod.network.Packet;
import com.example.examplemod.client.S2CNetworkContainer;
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


    /**
     * Initializes the mod.
     */
    public static void init() {
        registerPlayerTrackedData();
        registerPacketHandlers();
    }

    public static final TrackedDataKey<TestSyncedPlayerTrackedData> TEST = TrackedDataKey.of(TestSyncedPlayerTrackedData.class, new ResourceLocation(MOD_ID, "test"));

    private static void registerPlayerTrackedData() {
        PlayerTrackedDataRegistry.register(TEST, TestSyncedPlayerTrackedData::new);
    }

    private static void registerPacketHandlers() {
        NETWORK_CONTAINER.registerPacketHandler("player_tracked_data",
                new Packet.Handler<>(
                        SyncPlayerTrackedDataS2C.class,
                        SyncPlayerTrackedDataS2C::write,
                        SyncPlayerTrackedDataS2C::new,
                        SyncPlayerTrackedDataS2C::handle)
        );
    }
}