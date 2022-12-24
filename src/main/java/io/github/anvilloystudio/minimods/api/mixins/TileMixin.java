package io.github.anvilloystudio.minimods.api.mixins;

import io.github.anvilloystudio.minimods.api.TileConnections;
import io.github.anvilloystudio.minimods.api.interfaces.TileDestroyable;
import io.github.anvilloystudio.minimods.api.interfaces.TileInteractable;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Tile.class)
public abstract class TileMixin implements TileInteractable, TileDestroyable {
	@Shadow
	public abstract boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir);

	@Unique
	public TileConnections connects = new TileConnections();

	@Override
	public boolean interactTile(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		return false;
	}

	@Override
	public boolean destroy(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		return interact(level, xt, yt, player, item, attackDir);
	}
}
