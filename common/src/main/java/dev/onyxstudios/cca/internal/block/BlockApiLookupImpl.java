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
package dev.onyxstudios.cca.internal.block;

import dev.onyxstudios.cca.api.v3.block.BlockApiLookup;
import dev.onyxstudios.cca.api.v3.component.ApiLookupMap;
import dev.onyxstudios.cca.api.v3.component.ApiProviderMap;
import dev.onyxstudios.cca.mixin.block.common.BlockEntityTypeAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class BlockApiLookupImpl<A, C> implements BlockApiLookup<A, C> {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-lookup-api-v1/block");
	private static final ApiLookupMap<BlockApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(BlockApiLookupImpl::new);
	private final ResourceLocation identifier;
	private final Class<A> apiClass;
	private final Class<C> contextClass;
	private final ApiProviderMap<Block, BlockApiProvider<A, C>> providerMap = ApiProviderMap.create();
	private final List<BlockApiLookup.BlockApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();
	
	public static <A, C> BlockApiLookup<A, C> get(ResourceLocation lookupId, Class<A> apiClass, Class<C> contextClass) {
		return (BlockApiLookup<A, C>) LOOKUPS.getLookup(lookupId, apiClass, contextClass);
	}
	
	private BlockApiLookupImpl(ResourceLocation identifier, Class<A> apiClass, Class<C> contextClass) {
		this.identifier = identifier;
		this.apiClass = apiClass;
		this.contextClass = contextClass;
	}
	
	@Nullable
	public A find(Level world, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context) {
		Objects.requireNonNull(world, "World may not be null.");
		Objects.requireNonNull(pos, "BlockPos may not be null.");
		if (blockEntity == null) {
			if (state == null) {
				state = world.getBlockState(pos);
			}
			
			if (state.hasBlockEntity()) {
				blockEntity = world.getBlockEntity(pos);
			}
		} else if (state == null) {
			state = blockEntity.getBlockState();
		}
		
		BlockApiLookup.BlockApiProvider<A, C> provider = this.getProvider(state.getBlock());
		A instance = null;
		if (provider != null) {
			instance = provider.find(world, pos, state, blockEntity, context);
		}
		
		if (instance != null) {
			return instance;
		} else {
			Iterator<BlockApiProvider<A, C>> var8 = this.fallbackProviders.iterator();
			
			do {
				if (!var8.hasNext()) {
					return null;
				}
				
				BlockApiLookup.BlockApiProvider<A, C> fallbackProvider = var8.next();
				instance = fallbackProvider.find(world, pos, state, blockEntity, context);
			} while(instance == null);
			
			return instance;
		}
	}
	
	public void registerSelf(BlockEntityType<?>... blockEntityTypes) {
		int var3 = blockEntityTypes.length;
		
		for(int var4 = 0; var4 < var3; ++var4) {
			BlockEntityType<?> blockEntityType = ((BlockEntityType[]) blockEntityTypes)[var4];
			Block supportBlock = ((BlockEntityTypeAccessor)blockEntityType).getValidBlocks().iterator().next();
			Objects.requireNonNull(supportBlock, "Could not get a support block for block entity type.");
			BlockEntity blockEntity = blockEntityType.create(BlockPos.ZERO, supportBlock.defaultBlockState());
			Objects.requireNonNull(blockEntity, "Instantiated block entity may not be null.");
			if (!this.apiClass.isAssignableFrom(blockEntity.getClass())) {
				String errorMessage = String.format("Failed to register self-implementing block entities. API class %s is not assignable from block entity class %s.", this.apiClass.getCanonicalName(), blockEntity.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		this.registerForBlockEntities((blockEntityx, context) -> (A) blockEntityx, blockEntityTypes);
	}
	
	public void registerForBlocks(BlockApiLookup.BlockApiProvider<A, C> provider, Block... blocks) {
		Objects.requireNonNull(provider, "BlockApiProvider may not be null.");
		if (blocks.length == 0) {
			throw new IllegalArgumentException("Must register at least one Block instance with a BlockApiProvider.");
		} else {
			for (Block block : blocks) {
				Objects.requireNonNull(block, "Encountered null block while registering a block API provider mapping.");
				if (this.providerMap.putIfAbsent(block, provider) != null) {
					LOGGER.warn("Encountered duplicate API provider registration for block: {}", BuiltInRegistries.BLOCK.getKey(block));
				}
			}
			
		}
	}
	
	public void registerForBlockEntities(BlockApiLookup.BlockEntityApiProvider<A, C> provider, BlockEntityType<?>... blockEntityTypes) {
		Objects.requireNonNull(provider, "BlockEntityApiProvider may not be null.");
		if (blockEntityTypes.length == 0) {
			throw new IllegalArgumentException("Must register at least one BlockEntityType instance with a BlockEntityApiProvider.");
		} else {
			BlockApiLookup.BlockApiProvider<A, C> nullCheckedProvider = (world, pos, state, blockEntity, context) -> blockEntity == null ? null : provider.find(blockEntity, context);
			int var5 = blockEntityTypes.length;
			
			for(int var6 = 0; var6 < var5; ++var6) {
				BlockEntityType<?> blockEntityType = ((BlockEntityType[]) blockEntityTypes)[var6];
				Objects.requireNonNull(blockEntityType, "Encountered null block entity type while registering a block entity API provider mapping.");
				Block[] blocks = ((BlockEntityTypeAccessor)blockEntityType).getValidBlocks().toArray(new Block[0]);
				this.registerForBlocks(nullCheckedProvider, blocks);
			}
			
		}
	}
	
	public void registerFallback(BlockApiLookup.BlockApiProvider<A, C> fallbackProvider) {
		Objects.requireNonNull(fallbackProvider, "BlockApiProvider may not be null.");
		this.fallbackProviders.add(fallbackProvider);
	}
	
	public ResourceLocation getId() {
		return this.identifier;
	}
	
	public Class<A> apiClass() {
		return this.apiClass;
	}
	
	public Class<C> contextClass() {
		return this.contextClass;
	}
	
	@Nullable
	public BlockApiLookup.@Nullable BlockApiProvider<A, C> getProvider(Block block) {
		return this.providerMap.get(block);
	}
	
	public List<BlockApiLookup.BlockApiProvider<A, C>> getFallbackProviders() {
		return this.fallbackProviders;
	}
}
