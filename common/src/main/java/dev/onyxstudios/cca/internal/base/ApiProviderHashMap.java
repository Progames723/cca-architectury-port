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

import dev.onyxstudios.cca.api.v3.component.ApiProviderMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class ApiProviderHashMap<K, V> implements ApiProviderMap<K, V> {
	private volatile Map<K, V> lookups = new Reference2ReferenceOpenHashMap<>();
	
	public ApiProviderHashMap() {
	}
	
	public @Nullable V get(K key) {
		Objects.requireNonNull(key, "Key may not be null.");
		return this.lookups.get(key);
	}
	
	public synchronized V putIfAbsent(K key, V provider) {
		Objects.requireNonNull(key, "Key may not be null.");
		Objects.requireNonNull(provider, "Provider may not be null.");
		Map<K, V> lookupsCopy = new Reference2ReferenceOpenHashMap<>(this.lookups);
		V result = lookupsCopy.putIfAbsent(key, provider);
		this.lookups = lookupsCopy;
		return result;
	}
}
