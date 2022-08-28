package io.github.anvilloystudio.minimods.coremods.mixins;

import javax.swing.JFrame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import minicraft.core.Initializer;

@Mixin(Initializer.class)
public interface InitializerMixin {
	@Accessor(value = "frame", remap = false)
	public static JFrame getFrame() {
		throw new AssertionError();
	}
	@Accessor(value = "fra", remap = false)
	public static int getFra() {
		throw new AssertionError();
	}
	@Accessor(value = "tik", remap = false)
	public static int getTik() {
		throw new AssertionError();
	}
}
