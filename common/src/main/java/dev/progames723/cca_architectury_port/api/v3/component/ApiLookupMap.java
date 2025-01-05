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
package dev.progames723.cca_architectury_port.api.v3.component;

import dev.progames723.cca_architectury_port.internal.base.ApiLookupMapImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@ApiStatus.NonExtendable
public interface ApiLookupMap<L> extends Iterable<L> {
	static <L> ApiLookupMap<L> create(LookupConstructor<L> lookupConstructor) {
		Objects.requireNonNull(lookupConstructor, "Lookup factory may not be null.");
		return new ApiLookupMapImpl<>(lookupConstructor);
	}
	
	L getLookup(ResourceLocation var1, Class<?> var2, Class<?> var3);
	
	static <L> ApiLookupMap<L> create(LookupFactory<L> lookupFactory) {
		return create((id, apiClass, contextClass) -> lookupFactory.get(apiClass, contextClass));
	}
	
	@FunctionalInterface
	interface LookupConstructor<L> {
		L get(ResourceLocation var1, Class<?> var2, Class<?> var3);
	}
	
	interface LookupFactory<L> {
		L get(Class<?> var1, Class<?> var2);
	}
}