package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import minicraft.gfx.Sprite;
import minicraft.level.tile.OreTile;
import minicraft.level.tile.OreTile.OreType;

@Mixin(OreTile.class)
public interface OreTileMixin {
	@Invoker(value = "<init>", remap = false)
	public static OreTile invokeInit(OreType o) {
		throw new AssertionError();
	}

	@Accessor(value = "sprite", remap = false)
	public void setSprite(Sprite sprite);
}
