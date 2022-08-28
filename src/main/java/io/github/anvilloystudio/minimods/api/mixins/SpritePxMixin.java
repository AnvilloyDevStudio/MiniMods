package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import minicraft.gfx.Sprite;

@Mixin(Sprite.Px.class)
public interface SpritePxMixin {
	@Accessor(value = "sheetPos", remap = false)
	public int getSpritePos();
	@Accessor(value = "mirror", remap = false)
	public int getMirror();
}
