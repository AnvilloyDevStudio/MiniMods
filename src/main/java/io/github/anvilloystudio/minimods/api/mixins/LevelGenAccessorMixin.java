package io.github.anvilloystudio.minimods.api.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import minicraft.level.LevelGen;

@Mixin(LevelGen.class)
public interface LevelGenAccessorMixin {
	@Accessor(value = "random", remap = false)
	public static Random getRandom() {
		throw new AssertionError();
	};
}
