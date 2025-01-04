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
package dev.onyxstudios.cca.internal.scoreboard;

import dev.architectury.networking.NetworkManager;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.internal.base.ComponentsInternals;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.Scoreboard;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class CcaScoreboardClientNw {
    public static void initClient() {
        registerScoreboardSync(ComponentsScoreboardNetworking.TEAM_PACKET_ID, buf -> {
            String teamName = buf.readUtf();
            return (componentType, scoreboard) -> componentType.maybeGet(scoreboard.getPlayerTeam(teamName));
        });
        registerScoreboardSync(ComponentsScoreboardNetworking.SCOREBOARD_PACKET_ID,
            buf -> ComponentKey::maybeGet
        );
    }

    private static void registerScoreboardSync(ResourceLocation packetId, Function<FriendlyByteBuf, BiFunction<ComponentKey<?>, Scoreboard, Optional<? extends Component>>> reader) {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, packetId, ((buffer, packetContext) -> {
            try {
                Minecraft client = Minecraft.getInstance();
                BiFunction<ComponentKey<?>, Scoreboard, Optional<? extends Component>> getter = reader.apply(buffer);
                ResourceLocation componentTypeId = buffer.readResourceLocation();
                ComponentKey<?> componentType = ComponentRegistry.get(componentTypeId);
                
                if (componentType != null) {
                    buffer.retain();
                    client.execute(() -> {
                        try {
                            getter.apply(componentType, client.getConnection().getLevel().getScoreboard())
                                .filter(c -> c instanceof AutoSyncedComponent)
                                .ifPresent(c -> ((AutoSyncedComponent) c).applySyncPacket(buffer));
                        } finally {
                            buffer.release();
                        }
                    });
                }
            } catch (Exception e) {
                ComponentsInternals.LOGGER.error("Error while reading scoreboard components from network", e);
                throw e;
            }
        }));
    }
}
