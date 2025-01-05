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
package dev.progames723.cca_architectury_port.internal.level;

import dev.progames723.cca_architectury_port.api.v3.component.ComponentKey;
import dev.progames723.cca_architectury_port.api.v3.component.sync.AutoSyncedComponent;
import dev.progames723.cca_architectury_port.api.v3.world.WorldSyncCallback;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelData;

public final class ComponentsLevelNetworking {
    /**
     * {@link ClientboundCustomPayloadPacket} channel for default level component synchronization.
     *
     * <p> Packets emitted on this channel must begin with the
     * {@link ComponentKey#getId() component's type} (as an Identifier).
     *
     * <p> Components synchronized through this channel will have {@linkplain AutoSyncedComponent#applySyncPacket(FriendlyByteBuf)}
     * called on the game thread.
     */
    public static final ResourceLocation PACKET_ID = new ResourceLocation("cardinal-components", "level_sync");

    public static void init() {
        WorldSyncCallback.EVENT.register((player, world) -> {
            LevelData props = world.getLevelData();
            
            for (ComponentKey<?> key : props.asComponentProvider().getComponentContainer().keys()) {
                key.syncWith(player, props.asComponentProvider());
            }
        });
    }

}
