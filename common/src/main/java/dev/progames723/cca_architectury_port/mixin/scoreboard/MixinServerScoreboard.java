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
package dev.progames723.cca_architectury_port.mixin.scoreboard;

import dev.progames723.cca_architectury_port.api.v3.component.ComponentKey;
import dev.progames723.cca_architectury_port.api.v3.component.sync.AutoSyncedComponent;
import dev.progames723.cca_architectury_port.api.v3.component.sync.ComponentPacketWriter;
import dev.progames723.cca_architectury_port.api.v3.scoreboard.TeamAddCallback;
import dev.progames723.cca_architectury_port.internal.scoreboard.ComponentsScoreboardNetworking;
import dev.progames723.cca_architectury_port.internal.scoreboard.ScoreboardComponentContainerFactory;
import dev.progames723.cca_architectury_port.internal.scoreboard.StaticScoreboardComponentPlugin;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ServerScoreboard.class)
public abstract class MixinServerScoreboard extends MixinScoreboard {
    @Unique
    private static final Supplier<ScoreboardComponentContainerFactory> componentsContainerFactory = StaticScoreboardComponentPlugin.scoreboardComponentsContainerFactory;

    @Shadow
    @Final
    private MinecraftServer server;

    @Override
    public Iterable<ServerPlayer> getRecipientsForComponentSync() {
        MinecraftServer server = this.server;

        if (server.getPlayerList() != null) {
            return server.getPlayerList().getPlayers();
        }
        return List.of();
    }

    @Nullable
    @Override
    public <C extends AutoSyncedComponent> ClientboundCustomPayloadPacket toComponentPacket(ComponentKey<? super C> key, ComponentPacketWriter writer, ServerPlayer recipient) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(key.getId());
        writer.writeSyncPacket(buf, recipient);
        return new ClientboundCustomPayloadPacket(ComponentsScoreboardNetworking.SCOREBOARD_PACKET_ID, buf);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initComponents(CallbackInfo ci) {
        this.components = componentsContainerFactory.get().create((Scoreboard) (Object) this, this.server);
    }

    @Inject(method = "onTeamAdded", at = @At("RETURN"))
    private void syncTeamComponents(PlayerTeam team, CallbackInfo ci) {
        TeamAddCallback.EVENT.invoker().onTeamAdded(team);
    }
}
