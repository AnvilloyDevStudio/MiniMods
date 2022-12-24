package io.github.anvilloystudio.minimods.api.interfaces;

import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.item.Item;
import minicraft.level.Level;

public interface TileDestroyable {
	/**
	 * Called when you hit an item on a tile (ex: Pickaxe on rock).
	 * DESTROY ONLY; {@link minicraft.level.tile.Tile#interact(Level, int, int, Player, Item, Direction) Tile#interact} is now only for tile destroying.
	 * @param level The level the player is on.
	 * @param xt X position of the player in tile coordinates (32x per tile).
	 * @param yt Y position of the player in tile coordinates (32px per tile).
	 * @param player The player who called this method.
	 * @param item The item the player is currently holding.
	 * @param attackDir The direction of the player attacking.
	 * @return Was the operation successful?
	 */
	boolean destroy(Level level, int xt, int yt, Player player, Item item, Direction attackDir);
}
