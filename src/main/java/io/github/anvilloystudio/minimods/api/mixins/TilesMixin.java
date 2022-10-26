package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

@Mixin(Tiles.class)
public interface TilesMixin {
	@Invoker(value = "add", remap = false)
	public static void invokeAdd(int id, Tile tile) {
		throw new AssertionError();
	}
}
