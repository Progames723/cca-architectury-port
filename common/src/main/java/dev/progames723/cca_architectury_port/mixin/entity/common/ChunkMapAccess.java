package dev.progames723.cca_architectury_port.mixin.entity.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ChunkMap.class)
public interface ChunkMapAccess {
	@Accessor
	Int2ObjectMap<TrackedEntityAccess> getEntityMap();
	
	@Mixin(targets = "net/minecraft/server/level/ChunkMap$TrackedEntity")
	interface TrackedEntityAccess {
		@Accessor
		Set<ServerPlayerConnection> getSeenBy();
	}
}
