package dev.onyxstudios.cca.fabric;

import dev.onyxstudios.cca.internal.base.ComponentsInternals;
import net.fabricmc.api.ModInitializer;

public class Init implements ModInitializer {
	@Override
	public void onInitialize() {
		ComponentsInternals.LOGGER.info("Initializing Cardinal Components API(Architectury port)");
		ComponentsInternals.init();
	}
}
