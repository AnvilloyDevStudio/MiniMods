package io.github.anvilloystudio.minimods.api.mixins;

import minicraft.gfx.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Sprite.class)
public interface SpriteMixin {
	@Accessor(value = "spritePixels", remap = false)
	public Sprite.Px[][] getSpritePixels();
}
