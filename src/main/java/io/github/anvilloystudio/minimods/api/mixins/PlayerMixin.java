package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.anvilloystudio.minimods.api.interfaces.TileInteractable;
import minicraft.core.io.InputHandler;
import minicraft.entity.ClientTickable;
import minicraft.entity.ItemHolder;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.level.tile.Tile;

@Mixin(Player.class)
public class PlayerMixin extends Mob implements ItemHolder, ClientTickable {
	public PlayerMixin(MobSprite[][] sprites, int health) {
		super(sprites, health);
	}

	@Shadow(remap = false)
	private InputHandler input;

	@Shadow(remap = false)
	private Item activeItem;

	@Shadow(remap = false)
	private Point getInteractionTile() {
		throw new AssertionError();
	}

	@Inject(method = "tick()V", at = @At(value = "CONSTANT", args = "stringValue=menu", ordinal = 1, remap = false), remap = false)
	private void injectTileInteractTick(CallbackInfo ci) {
		if (input.getKey("interact").clicked) {
			Point t = getInteractionTile();
			Tile tile = level.getTile(t.x, t.y);
			((TileInteractable) tile).interactTile(level, t.x, t.y, (Player) (Object) this, activeItem, dir);
		}
	}

	@Shadow(remap = false)
	public Inventory getInventory() {
		throw new AssertionError();
	}

	@Shadow(remap = false)
	public void render(Screen screen) {
		throw new AssertionError();
	}
}
