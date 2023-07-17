package io.github.anvilloystudio.minimods.api.mixins;

import minicraft.gfx.SpriteSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import minicraft.gfx.Sprite;

@Mixin(Sprite.Px.class)
public interface SpritePxMixin {
	@Accessor(value = "sheetPos", remap = false)
	int getSpritePos();
	@Accessor(value = "mirror", remap = false)
	int getMirror();
	@Accessor(value = "spriteSheetNum", remap = false)
	int getSpriteSheetNum();
	@Accessor(value = "spriteSheet", remap = false)
	SpriteSheet getSpriteSheet();
}
