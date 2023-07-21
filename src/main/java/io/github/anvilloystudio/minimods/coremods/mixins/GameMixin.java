package io.github.anvilloystudio.minimods.coremods.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.anvilloystudio.minimods.coremods.CommandWindow;
import io.github.anvilloystudio.minimods.coremods.ExternalDebugPanel;
import minicraft.core.Game;

@Mixin(Game.class)
public class GameMixin {
	@Inject(method = "main([Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "minicraft.core.Initializer.run()V", remap = false), remap = false)
	private static void mainInitRun(CallbackInfo ci) {
		CommandWindow.init();
		ExternalDebugPanel.init();
		System.out.println("GameMixin success check.");
	}

	@Inject(method = "lambda$main$0(Ljava/lang/Thread;Ljava/lang/Throwable;)V", at = @At(value = "TAIL", remap = false), remap = false)
	private static void mainErrorHandlerLambdaTail(CallbackInfo ci) {
		Game.quit();
	}
}
