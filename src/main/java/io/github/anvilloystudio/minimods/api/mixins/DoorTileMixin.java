package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.github.anvilloystudio.minimods.api.interfaces.TileInteractable;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.DoorTile;

@Mixin(DoorTile.class)
public class DoorTileMixin implements TileInteractable {
	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		boolean closed = level.getData(xt, yt) == 0;
		level.setData(xt, yt, closed ? 1 : 0);
		return false;
	}
}
