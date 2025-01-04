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
package dev.onyxstudios.cca.mixin.block.common;

import dev.onyxstudios.cca.api.v3.component.ComponentContainer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter;
import dev.onyxstudios.cca.internal.block.CardinalComponentsBlock;
import dev.onyxstudios.cca.internal.block.CardinalBlockInternals;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements ComponentProvider {
    @Shadow
    @Nullable
    public abstract Level getLevel();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public abstract BlockEntityType<?> getType();
    
    @Shadow @Nullable protected Level level;
    @Unique
    private ComponentContainer components;

    @Inject(method = "loadStatic", at = @At("RETURN"))
    private static void readComponentData(BlockPos pos, BlockState state, CompoundTag nbt, CallbackInfoReturnable<BlockEntity> cir) {
        if (cir.getReturnValue() != null) {
            cir.getReturnValue().asComponentProvider().getComponentContainer().fromTag(nbt);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        // Promise, this is a BlockEntity
        //noinspection ConstantConditions
        this.components = CardinalBlockInternals.createComponents((BlockEntity) (Object) this);
    }

    @Inject(method = "saveWithId", at = @At("RETURN"))
    private void writeNbt(CallbackInfoReturnable<CompoundTag> cir) {
        this.components.toTag(cir.getReturnValue());
    }

    /**
     * Yay redundancy!
     *
     * <p>This method may be overridden without calling super(), so we need safety nets.
     * On the other hand, we still need this inject because mods can also call {@link BlockEntity#load(CompoundTag)} directly.
     * We mostly do not care about what happens on the client though, since we have our own packets.
     *
     * @see #readComponentData(BlockPos, BlockState, CompoundTag, CallbackInfoReturnable)
     * @see MixinBlockDataAccessor#readComponentData(CompoundTag, CallbackInfo)
     */
    @Inject(method = "load", at = @At(value = "RETURN"))
    private void readNbt(CompoundTag tag, CallbackInfo ci) {
        this.components.fromTag(tag);
    }

    @Nonnull
    @Override
    public ComponentContainer getComponentContainer() {
        return this.components;
    }

    @Override
    public Iterable<ServerPlayer> getRecipientsForComponentSync() {
        Level world = this.getLevel();

        if (world != null && !world.isClientSide) {//very hacky
            return ((ServerLevel)this.level).getChunkSource().chunkMap.getPlayers(new ChunkPos(this.getBlockPos()), false);
        }
        return List.of();
    }

    @Nullable
    @Override
    public <C extends AutoSyncedComponent> ClientboundCustomPayloadPacket toComponentPacket(ComponentKey<? super C> key, ComponentPacketWriter writer, ServerPlayer recipient) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(BlockEntityType.getKey(this.getType()));
        buf.writeBlockPos(this.getBlockPos());
        buf.writeResourceLocation(key.getId());
        writer.writeSyncPacket(buf, recipient);
        return new ClientboundCustomPayloadPacket(CardinalComponentsBlock.PACKET_ID, buf);
    }

}
