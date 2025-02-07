package dev.corgitaco.dataanchor.forge;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(DataAnchor.MOD_ID)
public class DataAnchorForge {
    public DataAnchorForge() {
        DataAnchor.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent fmlCommonSetupEvent) {
        PacketBroadcaster.ALL.forEach(PacketBroadcaster::registerPackets);
    }
}
