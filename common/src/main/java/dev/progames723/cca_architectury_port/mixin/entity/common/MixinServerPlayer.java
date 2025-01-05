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

import dev.progames723.cca_architectury_port.api.v3.entity.PlayerCopyCallback;
import dev.progames723.cca_architectury_port.internal.entity.SwitchablePlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer implements SwitchablePlayerEntity {
    private transient boolean switchingCharacter = false;

    @Override
    public void cca$markAsSwitchingCharacter() {
        this.switchingCharacter = true;
    }

    @Override
    public boolean cca$isSwitchingCharacter() {
        return this.switchingCharacter;
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void copyDataFrom(ServerPlayer original, boolean lossless, CallbackInfo ci) {
        PlayerCopyCallback.EVENT.invoker().copyData(original, (ServerPlayer) (Object) this, lossless);
    }
}
