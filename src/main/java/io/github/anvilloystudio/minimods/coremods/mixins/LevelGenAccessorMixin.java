package io.github.anvilloystudio.minimods.coremods.mixins;

import minicraft.level.LevelGen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;

@Mixin(LevelGen.class)
public interface LevelGenAccessorMixin {
	@Accessor(value = "random", remap = false)
	static Random getRandom() {
		throw new AssertionError();
	};
}
