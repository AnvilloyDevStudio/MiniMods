package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.anvilloystudio.minimods.api.ModsDisplay;
import minicraft.core.Game;
import minicraft.screen.Display;
import minicraft.screen.TitleDisplay;
import minicraft.screen.entry.SelectEntry;

@Mixin(TitleDisplay.class)
public abstract class TitleDisplayMixin extends Display {
	@Inject(method = "<init>", at = @At(value = "TAIL", remap = false), remap = false)
	private void insertMenusInit(CallbackInfo ci) {
		((MenuMixin) menus[0]).getEntries().add(5, new SelectEntry("Mods", () -> Game.setMenu(new ModsDisplay())));
	}
}
