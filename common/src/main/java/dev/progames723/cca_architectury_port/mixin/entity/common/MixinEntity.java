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
package dev.progames723.cca_architectury_port.mixin.entity.common;

import dev.progames723.cca_architectury_port.api.v3.component.ComponentContainer;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentKey;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentProvider;
import dev.progames723.cca_architectury_port.api.v3.component.sync.AutoSyncedComponent;
import dev.progames723.cca_architectury_port.api.v3.component.sync.ComponentPacketWriter;
import dev.progames723.cca_architectury_port.internal.entity.CardinalComponentsEntity;
import dev.progames723.cca_architectury_port.internal.entity.CardinalEntityInternals;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Mixin(Entity.class)
public abstract class MixinEntity implements ComponentProvider {
    @Unique
    private ComponentContainer components;

    @Shadow
    private Level level;

    @Shadow public abstract int getId();

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void initDataTracker(CallbackInfo ci) {
        this.components = CardinalEntityInternals.createEntityComponentContainer((Entity) (Object) this);
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void toTag(CompoundTag inputTag, CallbackInfoReturnable<CompoundTag> cir) {
        this.components.toTag(cir.getReturnValue());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void fromTag(CompoundTag tag, CallbackInfo ci) {
        this.components.fromTag(tag);
    }

    @Nonnull
    @Override
    public ComponentContainer getComponentContainer() {
        return this.components;
    }

    @Override
    public Iterable<ServerPlayer> getRecipientsForComponentSync() {//this is not important for you rn
        Entity holder = (Entity) (Object) this;
        if (!this.level.isClientSide) {
            Collection<ServerPlayer> tracked;
            ChunkMap map = ((ServerChunkCache)this.level.getChunkSource()).chunkMap;
            ChunkMapAccess.TrackedEntityAccess trackedEntityAccess = ((ChunkMapAccess) map).getEntityMap().get(this.getId());
            if (trackedEntityAccess != null) {
                tracked = trackedEntityAccess.getSeenBy().stream().map(ServerPlayerConnection::getPlayer).distinct().toList();
            } else {
                tracked = new ArrayList<>();
            }
            Deque<ServerPlayer> watchers = new ArrayDeque<>(tracked);
            if (holder instanceof ServerPlayer player && player.connection != null) {
                watchers.addFirst(player);
            }
            return watchers;
        }
        return List.of();
    }

    @Nullable
    @Override
    public <C extends AutoSyncedComponent> ClientboundCustomPayloadPacket toComponentPacket(ComponentKey<? super C> key, ComponentPacketWriter writer, ServerPlayer recipient) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(this.getId());
        buf.writeResourceLocation(key.getId());
        writer.writeSyncPacket(buf, recipient);
        return new ClientboundCustomPayloadPacket(CardinalComponentsEntity.PACKET_ID, buf);
    }

}
