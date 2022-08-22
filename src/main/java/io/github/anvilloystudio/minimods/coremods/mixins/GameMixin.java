package io.github.anvilloystudio.minimods.coremods.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.anvilloystudio.minimods.coremods.CommandWindow;
import minicraft.core.Game;

@Mixin(Game.class)
public class GameMixin {
	@Inject(method = "main([Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "minicraft.core.Initializer.run()V", remap = false), remap = false)
	private static void mainInitRun(CallbackInfo ci) {
		CommandWindow.init();;
	}
}
