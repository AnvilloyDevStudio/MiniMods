package io.github.anvilloystudio.minimods.coremods.mixins;

import io.github.anvilloystudio.minimods.coremods.CommandWindow;
import minicraft.core.Updater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Updater.class)
public class UpdaterMixin {
	@Inject(method = "tick()V", at = @At(value = "HEAD", remap = false), remap = false)
	private static void measureTickHead(CallbackInfo ci) {
		CommandWindow.tickStart = System.nanoTime();
	}

	@Inject(method = "tick()V", at = @At(value = "TAIL", remap = false), remap = false)
	private static void measureTickTail(CallbackInfo ci) {
		CommandWindow.tickNano = System.nanoTime() - CommandWindow.tickStart;
		if (CommandWindow.measureCallback != null) {
			CommandWindow.measureCallback.act();
			CommandWindow.measureCallback = null;
		}
	}
}
