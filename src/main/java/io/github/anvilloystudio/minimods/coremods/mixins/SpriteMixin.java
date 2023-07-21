package io.github.anvilloystudio.minimods.coremods.mixins;

import minicraft.gfx.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Sprite.class)
public interface SpriteMixin {
	@Accessor(value = "spritePixels", remap = false)
	Sprite.Px[][] getSpritePixels();
}
