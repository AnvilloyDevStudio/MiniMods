package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.anvilloystudio.minimods.api.TileConnections;
import minicraft.level.tile.Tile;

@Mixin(Tile.class)
public class TileMixin {
	@Unique
	public TileConnections connects = new TileConnections();
}
