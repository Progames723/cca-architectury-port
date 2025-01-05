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
package dev.progames723.cca_architectury_port.api.v3.item;

import net.minecraft.nbt.*;

@SuppressWarnings("unused")
public final class CcaNbtType<T extends Tag> {
    public static final CcaNbtType<ByteTag> BYTE = new CcaNbtType<>(1);
    public static final CcaNbtType<ShortTag> SHORT = new CcaNbtType<>(2);
    public static final CcaNbtType<IntTag> INT = new CcaNbtType<>(3);
    public static final CcaNbtType<LongTag> LONG = new CcaNbtType<>(4);
    public static final CcaNbtType<FloatTag> FLOAT = new CcaNbtType<>(5);
    public static final CcaNbtType<DoubleTag> DOUBLE = new CcaNbtType<>(6);
    public static final CcaNbtType<ByteArrayTag> BYTE_ARRAY = new CcaNbtType<>(7);
    public static final CcaNbtType<StringTag> STRING = new CcaNbtType<>(8);
    public static final CcaNbtType<ListTag> LIST = new CcaNbtType<>(9);
    public static final CcaNbtType<CompoundTag> COMPOUND = new CcaNbtType<>(10);
    public static final CcaNbtType<IntArrayTag> INT_ARRAY = new CcaNbtType<>(11);
    public static final CcaNbtType<LongArrayTag> LONG_ARRAY = new CcaNbtType<>(12);

    public static CcaNbtType<?> byId(int id) {
        return switch (id) {
            case 1 -> BYTE;
            case 2 -> SHORT;
            case 3 -> INT;
            case 4 -> LONG;
            case 5 -> FLOAT;
            case 6 -> DOUBLE;
            case 7 -> BYTE_ARRAY;
            case 8 -> STRING;
            case 9 -> LIST;
            case 10 -> COMPOUND;
            case 11 -> INT_ARRAY;
            case 12 -> LONG_ARRAY;
            default -> throw new IllegalArgumentException("Unsupported NBT Type " + id);
        };
    }

    private final int type;

    private CcaNbtType(int type) {
        this.type = type;
    }

    public int getId() {
        return this.type;
    }
}
