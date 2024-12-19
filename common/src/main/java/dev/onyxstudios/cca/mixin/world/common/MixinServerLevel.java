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
package dev.onyxstudios.cca.mixin.world.common;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter;
import dev.onyxstudios.cca.internal.world.ComponentPersistentState;
import dev.onyxstudios.cca.internal.world.ComponentsWorldNetworking;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends MixinLevel {
    @Shadow public abstract DimensionDataStorage getDataStorage();
    
    @Shadow public abstract List<ServerPlayer> players();
    
    @Unique
    private static final String PERSISTENT_STATE_KEY = "cardinal_world_components";

    @Inject(at = @At("RETURN"), method = "<init>*")
    private void constructor(CallbackInfo ci) {
        this.getDataStorage().computeIfAbsent(
            tag -> ComponentPersistentState.fromNbt(this.components, tag),
            () -> new ComponentPersistentState(this.components),
            PERSISTENT_STATE_KEY
        );
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.getComponentContainer().tickServerComponents();
    }

    @Override
    public Iterable<ServerPlayer> getRecipientsForComponentSync() {
        return this.players();
    }

    @Nullable
    @Override
    public <C extends AutoSyncedComponent> ClientboundCustomPayloadPacket toComponentPacket(ComponentKey<? super C> key, ComponentPacketWriter writer, ServerPlayer recipient) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(key.getId());
        writer.writeSyncPacket(buf, recipient);
        return new ClientboundCustomPayloadPacket(ComponentsWorldNetworking.PACKET_ID, buf);
    }
}
