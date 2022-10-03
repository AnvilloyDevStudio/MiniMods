package io.github.anvilloystudio.minimods.api.mixins;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import minicraft.core.io.InputHandler;

@Mixin(InputHandler.class)
public class InputHandlerMixin {
	@Shadow(remap = false)
	private HashMap<String, String> keymap;

	@Inject(method = "initKeyMap()V", at = @At(value = "TAIL", remap = false), remap = false)
	private void injectMoreKeymapsForAPI(CallbackInfo ci) {
		keymap.put("INTERACT", "B");
	}
}
