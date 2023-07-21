package io.github.anvilloystudio.minimods.coremods.mixins;

import minicraft.gfx.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Color.class)
public interface ColorMixin {
	@Invoker(value = "upgrade", remap = false)
	static int upgrade(int rgbMinicraft) {
		throw new AssertionError();
	}
}
