package dev.corgitaco.dataanchor.neoforge;

import dev.corgitaco.dataanchor.DataAnchor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(DataAnchor.MOD_ID)
public class DataAnchorNeoForge {
    public DataAnchorNeoForge(IEventBus eventBus) {
        DataAnchor.init();
    }
}
