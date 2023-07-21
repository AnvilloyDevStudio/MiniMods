package io.github.anvilloystudio.minimods.coremods.mixins;

import minicraft.core.Initializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.swing.JFrame;

@Mixin(Initializer.class)
public interface InitializerMixin {
	@Accessor(value = "frame", remap = false)
	static JFrame getFrame() {
		throw new AssertionError();
	}
	@Accessor(value = "fra", remap = false)
	static int getFra() {
		throw new AssertionError();
	}
	@Accessor(value = "tik", remap = false)
	static int getTik() {
		throw new AssertionError();
	}
}
