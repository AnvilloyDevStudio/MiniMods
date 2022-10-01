package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.anvilloystudio.minimods.api.TileConnections;
import io.github.anvilloystudio.minimods.api.interfaces.TileInteractable;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

@Mixin(Tile.class)
public class TileMixin implements TileInteractable {
	@Unique
	public TileConnections connects = new TileConnections();

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		return false;
	}
}
