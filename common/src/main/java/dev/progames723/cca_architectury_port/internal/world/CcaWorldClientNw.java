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
package dev.progames723.cca_architectury_port.internal.world;

import dev.architectury.networking.NetworkManager;
import dev.progames723.cca_architectury_port.api.v3.component.Component;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentKey;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentRegistry;
import dev.progames723.cca_architectury_port.api.v3.component.sync.AutoSyncedComponent;
import dev.progames723.cca_architectury_port.internal.base.ComponentsInternals;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public final class CcaWorldClientNw {
    public static void initClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ComponentsWorldNetworking.PACKET_ID, ((buf, packetContext) -> {
            try {
                Minecraft client = Minecraft.getInstance();
                ResourceLocation componentTypeId = buf.readResourceLocation();
                ComponentKey<?> componentType = ComponentRegistry.get(componentTypeId);
                
                if (componentType == null) {
                    return;
                }
                
                buf.retain();
                
                client.execute(() -> {
                    try {
                        assert client.level != null;
                        Component c = componentType.get(client.level);
                        if (c instanceof AutoSyncedComponent) {
                            ((AutoSyncedComponent) c).applySyncPacket(buf);
                        }
                    } finally {
                        buf.release();
                    }
                });
            } catch (Exception e) {
                ComponentsInternals.LOGGER.error("Error while reading world components from network", e);
                throw e;
            }
        }));
    }
}
