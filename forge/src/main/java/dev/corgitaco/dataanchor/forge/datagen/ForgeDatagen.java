package dev.corgitaco.dataanchor.forge.datagen;

import dev.corgitaco.dataanchor.DataAnchor;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DataAnchor.MOD_ID)
class ForgeDatagen {

	@SubscribeEvent
	protected static void gatherData(final GatherDataEvent event) {

	}
}
