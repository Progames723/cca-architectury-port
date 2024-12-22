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
package dev.onyxstudios.cca.internal.base;

import dev.onyxstudios.cca.api.v3.component.ApiLookupMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ApiLookupMapImpl<L> implements ApiLookupMap<L> {
	private final Map<ResourceLocation, StoredLookup<L>> lookups = new HashMap<>();
	private final ApiLookupMap.LookupConstructor<L> lookupConstructor;
	
	public ApiLookupMapImpl(ApiLookupMap.LookupConstructor<L> lookupConstructor) {
		this.lookupConstructor = lookupConstructor;
	}
	
	public synchronized L getLookup(ResourceLocation lookupId, Class<?> apiClass, Class<?> contextClass) {
		Objects.requireNonNull(lookupId, "Lookup Identifier may not be null.");
		Objects.requireNonNull(apiClass, "API class may not be null.");
		Objects.requireNonNull(contextClass, "Context class may not be null.");
		StoredLookup<L> storedLookup = this.lookups.computeIfAbsent(lookupId, (id) -> new StoredLookup<>(this.lookupConstructor.get(id, apiClass, contextClass), apiClass, contextClass));
		if (storedLookup.apiClass == apiClass && storedLookup.contextClass == contextClass) {
			return storedLookup.lookup;
		} else {
			String errorMessage = String.format("Lookup with id %s is already registered with api class %s and context class %s. It can't be registered with api class %s and context class %s.", lookupId, storedLookup.apiClass.getCanonicalName(), storedLookup.contextClass.getCanonicalName(), apiClass.getCanonicalName(), contextClass.getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public synchronized @NotNull Iterator<L> iterator() {
		return this.lookups.values().stream().map((storedLookup) -> storedLookup.lookup).iterator();
	}
	
	private static final class StoredLookup<L> {
		final L lookup;
		final Class<?> apiClass;
		final Class<?> contextClass;
		
		StoredLookup(L lookup, Class<?> apiClass, Class<?> contextClass) {
			this.lookup = lookup;
			this.apiClass = apiClass;
			this.contextClass = contextClass;
		}
	}
}
