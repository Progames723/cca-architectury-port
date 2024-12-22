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
package dev.onyxstudios.cca.api.v3.component;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;

public final class StaticComponentRegistrationEvents {
	public static final Event<Block> REGISTER_BLOCK_ENTITY = EventFactory.createLoop();
	
	public static final Event<Chunk> REGISTER_CHUNK = EventFactory.createLoop();
	
	public static final Event<Entity> REGISTER_ENTITY = EventFactory.createLoop();
	
	public static final Event<Item> REGISTER_ITEM = EventFactory.createLoop();
	
	public static final Event<Level> REGISTER_LEVEL = EventFactory.createLoop();
	
	public static final Event<Scoreboard> REGISTER_SCOREBOARD = EventFactory.createLoop();
	
	public static final Event<World> REGISTER_WORLD = EventFactory.createLoop();
	
	//interfaces
	
	public interface Block {void register(BlockComponentFactoryRegistry factoryRegistry);}
	
	public interface Chunk {void register(ChunkComponentFactoryRegistry factoryRegistry);}
	
	public interface Entity {void register(EntityComponentFactoryRegistry factoryRegistry);}
	
	public interface Item {void register(ItemComponentFactoryRegistry factoryRegistry);}
	
	public interface Level {void register(LevelComponentFactoryRegistry factoryRegistry);}
	
	public interface Scoreboard {void register(ScoreboardComponentFactoryRegistry factoryRegistry);}
	
	public interface World {void register(WorldComponentFactoryRegistry factoryRegistry);}
}
