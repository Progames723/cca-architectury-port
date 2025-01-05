package dev.progames723.cca_architectury_port.fabric;

import dev.progames723.cca_architectury_port.internal.base.ComponentsInternals;
import net.fabricmc.api.ModInitializer;

public class Init implements ModInitializer {
	@Override
	public void onInitialize() {
		ComponentsInternals.LOGGER.info("Initializing Cardinal Components API(Architectury port)");
		ComponentsInternals.init();
	}
}
