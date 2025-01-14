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
package dev.progames723.cca_architectury_port.mixin.item.common;

import dev.progames723.cca_architectury_port.api.v3.component.Component;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentContainer;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentKey;
import dev.progames723.cca_architectury_port.api.v3.component.ComponentProvider;
import dev.progames723.cca_architectury_port.api.v3.item.ItemTagInvalidationListener;
import dev.progames723.cca_architectury_port.internal.base.AbstractComponentContainer;
import dev.progames723.cca_architectury_port.internal.item.ItemCaller;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStack.class)
public abstract class MixinItemStack implements ComponentProvider {
    @Unique
    private static final ComponentContainer EMPTY_COMPONENTS = ComponentContainer.EMPTY;
    
    @Unique
    private @Nullable ComponentContainer components;

    @Inject(method = "setTag", at = @At("RETURN"))
    private void invalidateCaches(CompoundTag tag, CallbackInfo ci) {
        ComponentContainer components = this.components;

        if (components != null) {
            for (ComponentKey<?> key : components.keys()) {
                Component c = key.getInternal(components);
                if (c instanceof ItemTagInvalidationListener listener) {
                    listener.onTagInvalidated();
                }
            }
        }
    }

    @Shadow
    public abstract Item getItem();

    @Shadow public abstract boolean isEmpty();

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void initComponentsNBT(CompoundTag tag, CallbackInfo ci) {
        // Backwards save compatibility (see ItemComponent#readFromNbt)
        if (tag.contains(AbstractComponentContainer.NBT_KEY)) {
            this.getComponentContainer().fromTag(tag);
        }
    }

    @Override
    public ComponentContainer getComponentContainer() {
        if (this.isEmpty()) return EMPTY_COMPONENTS;
        if (this.components == null) {
            this.components = ((ItemCaller) this.getItem()).cardinal_createComponents((ItemStack) (Object) this);
        }
        return this.components;
    }
}
