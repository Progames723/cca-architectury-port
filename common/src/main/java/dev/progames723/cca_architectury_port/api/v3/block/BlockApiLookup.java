/*
 * Cardinal-Components-API
 * Copyright (C) 2019-2023 OnyxStudios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dev.progames723.cca_architectury_port.api.v3.block;

import dev.progames723.cca_architectury_port.internal.block.BlockApiLookupImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

@ApiStatus.NonExtendable
public interface BlockApiLookup<A, C> {
	static <A, C> BlockApiLookup<A, C> get(ResourceLocation lookupId, Class<A> apiClass, Class<C> contextClass) {
		return BlockApiLookupImpl.get(lookupId, apiClass, contextClass);
	}
	
	default @Nullable A find(Level world, BlockPos pos, C context) {
		return this.find(world, pos, null, null, context);
	}
	
	@Nullable A find(Level var1, BlockPos var2, @Nullable BlockState var3, @Nullable BlockEntity var4, C var5);
	
	void registerSelf(BlockEntityType<?>... var1);
	
	void registerForBlocks(BlockApiProvider<A, C> var1, Block... var2);
	
	default <T extends BlockEntity> void registerForBlockEntity(BiFunction<BlockEntity, C, @Nullable A> provider, BlockEntityType<T> blockEntityType) {
		this.registerForBlockEntities(provider::apply, blockEntityType);
	}
	
	void registerForBlockEntities(BlockEntityApiProvider<A, C> var1, BlockEntityType<?>... var2);
	
	void registerFallback(BlockApiProvider<A, C> var1);
	
	ResourceLocation getId();
	
	Class<A> apiClass();
	
	Class<C> contextClass();
	
	@Nullable BlockApiProvider<A, C> getProvider(Block var1);
	
	@FunctionalInterface
	interface BlockEntityApiProvider<A, C> {
		@Nullable A find(BlockEntity var1, C var2);
	}
	
	@FunctionalInterface
	interface BlockApiProvider<A, C> {
		@Nullable A find(Level var1, BlockPos var2, BlockState var3, @Nullable BlockEntity var4, C var5);
	}
}
