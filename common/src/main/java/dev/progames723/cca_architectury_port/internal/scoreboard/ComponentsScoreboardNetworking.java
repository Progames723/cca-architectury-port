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
package dev.progames723.cca_architectury_port.internal.scoreboard;

import dev.progames723.cca_architectury_port.api.v3.component.ComponentKey;
import dev.progames723.cca_architectury_port.api.v3.component.sync.AutoSyncedComponent;
import dev.progames723.cca_architectury_port.api.v3.scoreboard.ScoreboardSyncCallback;
import dev.progames723.cca_architectury_port.api.v3.scoreboard.TeamAddCallback;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.PlayerTeam;

public final class ComponentsScoreboardNetworking {
    /**
     * {@link ClientboundCustomPayloadPacket} channel for default scoreboard component synchronization.
     *
     * <p> Packets emitted on this channel must begin with the {@link ResourceLocation} for the component's type.
     *
     * <p> Components synchronized through this channel will have {@linkplain AutoSyncedComponent#applySyncPacket(FriendlyByteBuf)}
     * called on the game thread.
     */
    public static final ResourceLocation SCOREBOARD_PACKET_ID = new ResourceLocation("cardinal-components", "scoreboard_sync");
    /**
     * {@link ClientboundCustomPayloadPacket} channel for default team component synchronization.
     *
     * <p> Packets emitted on this channel must begin with, in order, the team's name as a {@link String},
     * and the {@link ResourceLocation} for the component's type.
     *
     * <p> Components synchronized through this channel will have {@linkplain AutoSyncedComponent#applySyncPacket(FriendlyByteBuf)}
     * called on the game thread.
     */
    public static final ResourceLocation TEAM_PACKET_ID = new ResourceLocation("cardinal-components", "team_sync");

    public static void init() {
        ScoreboardSyncCallback.EVENT.register((player, tracked) -> {
            for (ComponentKey<?> key : tracked.asComponentProvider().getComponentContainer().keys()) {
                key.syncWith(player, tracked.asComponentProvider());
            }
            
            for (PlayerTeam team : tracked.getPlayerTeams()) {
                for (ComponentKey<?> key : team.asComponentProvider().getComponentContainer().keys()) {
                    key.syncWith(player, team.asComponentProvider());
                }
            }
        });
        TeamAddCallback.EVENT.register((tracked) -> {
            for (ComponentKey<?> key : tracked.asComponentProvider().getComponentContainer().keys()) {
                tracked.syncComponent(key);
            }
        });
    }
}
