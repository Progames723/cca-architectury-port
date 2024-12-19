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
package dev.onyxstudios.cca.api.v3.level;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter;
import dev.onyxstudios.cca.api.v3.component.sync.PlayerSyncPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelData;

import java.util.NoSuchElementException;

/**
 * Static helper methods for components attached to {@link LevelData}
 */
public final class LevelComponents {
    /**
     * Attempts to synchronize the component attached to the main {@link LevelData} of the given {@link MinecraftServer}.
     *
     * <p>This method has no visible effect if the component associated with the key
     * does not implement an adequate synchronization interface.
     *
     * @throws NoSuchElementException if the provider does not provide this type of component
     */
    public static void sync(ComponentKey<?> key, MinecraftServer server) {
        LevelData props = server.getWorldData().overworldData();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            key.syncWith(player, props.asComponentProvider());
        }
    }

    /**
     * Attempts to synchronize the component attached to the main {@link LevelData} of the given {@link MinecraftServer}.
     *
     * <p>This method has no visible effect if the component associated with the key
     * does not implement an adequate synchronization interface.
     *
     * @param packetWriter a writer for the sync packet
     * @throws NoSuchElementException if the provider does not provide this type of component
     */
    public static void sync(ComponentKey<?> key, MinecraftServer server, ComponentPacketWriter packetWriter) {
        LevelData props = server.getWorldData().overworldData();
        Component c = key.get(props);
        if (c instanceof AutoSyncedComponent sc) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                key.syncWith(player, props.asComponentProvider(), packetWriter, sc);
            }
        }
    }

    /**
     * Attempts to synchronize the component attached to the main {@link LevelData} of the given {@link MinecraftServer}.
     *
     * <p>This method has no visible effect if the component associated with the key
     * does not implement an adequate synchronization interface.
     *
     * @param packetWriter a writer for the sync packet
     * @param predicate    a predicate for which players should receive the packet
     * @throws NoSuchElementException if the provider does not provide this type of component
     */
    public static void sync(ComponentKey<?> key, MinecraftServer server, ComponentPacketWriter packetWriter, PlayerSyncPredicate predicate) {
        LevelData props = server.getWorldData().overworldData();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            key.syncWith(player, props.asComponentProvider(), packetWriter, predicate);
        }
    }
}
