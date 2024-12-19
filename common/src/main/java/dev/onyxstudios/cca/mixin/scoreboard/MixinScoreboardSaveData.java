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
package dev.onyxstudios.cca.mixin.scoreboard;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;

@Mixin(ScoreboardSaveData.class)
public abstract class MixinScoreboardSaveData {
    @Final
    @Shadow
    private Scoreboard scoreboard;

    @Inject(method = "save", at = @At("RETURN"))
    private void saveComponents(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        ((ComponentProvider) this.scoreboard).getComponentContainer().toTag(tag);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void loadComponents(CompoundTag tag, CallbackInfoReturnable<ScoreboardSaveData> ci) {
        ((ComponentProvider) this.scoreboard).getComponentContainer().fromTag(tag);
    }

    @Inject(
        method = "loadTeams",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/scores/ScoreboardSaveData;loadTeamPlayers(Lnet/minecraft/world/scores/PlayerTeam;Lnet/minecraft/nbt/ListTag;)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void loadTeamComponents(ListTag teamsData, CallbackInfo ci, int i, CompoundTag teamData, String name, PlayerTeam team) {
        ((ComponentProvider) team).getComponentContainer().fromTag(teamData);
    }

    @Inject(
        method = "saveTeams",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/scores/PlayerTeam;getPlayers()Ljava/util/Collection;"
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void saveTeamComponents(
        CallbackInfoReturnable<ListTag> cir,
        ListTag teamsData,
        Collection<PlayerTeam> teams,
        Iterator<PlayerTeam> it,
        PlayerTeam team,
        CompoundTag teamData
    ) {
        ((ComponentProvider) team).getComponentContainer().toTag(teamData);
    }
}
