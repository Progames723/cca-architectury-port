package dev.progames723.cca_architectury_port.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.progames723.cca_architectury_port.internal.base.ComponentsInternals;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ComponentsInternals.MOD_ID)
public class Init {
	public Init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		EventBuses.registerModEventBus("cca_architectury_port", bus);
		ComponentsInternals.LOGGER.info("Initializing Cardinal Components API(Architectury port)");
		ComponentsInternals.init();
	}
}
