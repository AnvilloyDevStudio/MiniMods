package io.github.anvilloystudio.minimods.api.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.anvilloystudio.minimods.api.ModProcedure;
import minicraft.core.Game;
import minicraft.core.Updater;

@Mixin(Updater.class)
public class UpdaterMixin {
    @Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lminicraft/core/Updater;paused:Z", opcode = Opcodes.PUTSTATIC, ordinal = 1,
		remap = false, shift = At.Shift.AFTER), remap = false)
	private static void tickNotPaused(CallbackInfo ci) {
		ModProcedure.tickables0.forEach(t -> t.tick(Game.input));
	}

	@Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lminicraft/core/Updater;paused:Z", opcode = Opcodes.PUTSTATIC, ordinal = 0,
		remap = false, shift = At.Shift.AFTER), remap = false)
	private static void tickPaused(CallbackInfo ci) {
		ModProcedure.tickables1.forEach(t -> t.tick(Game.input));
	}

	@Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lminicraft/core/Updater;input:Lminicraft/core/io/InputHandler;", opcode = Opcodes.GETSTATIC, ordinal = 4,
		remap = false, shift = At.Shift.AFTER), remap = false)
	private static void tickFocused(CallbackInfo ci) {
		ModProcedure.tickables2.forEach(t -> t.tick(Game.input));
	}
}
