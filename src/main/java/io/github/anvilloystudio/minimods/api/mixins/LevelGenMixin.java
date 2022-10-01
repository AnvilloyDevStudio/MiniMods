package io.github.anvilloystudio.minimods.api.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.anvilloystudio.minimods.api.ModTileGen;
import minicraft.level.LevelGen;

@Mixin(LevelGen.class)
public class LevelGenMixin {
	@Accessor(value = "random", remap = false)
	public static Random getRandom() {
		throw new AssertionError();
	};

	@Inject(method = "createTopMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void injectCreateTopMap(int w, int h, CallbackInfo ci, byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(0, map, data, w, h);
	}

	@Inject(method = "createDungeonMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void injectCreateDungeonMap(int w, int h, CallbackInfo ci, byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(-4, map, data, w, h);
	}

	@Inject(method = "createUndergroundMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void injectCreateUndergroundMap(int w, int h, int depth, CallbackInfo ci, byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(-depth, map, data, w, h);
	}

	@Inject(method = "createSkyMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void injectCreateSkyMap(int w, int h, CallbackInfo ci, byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(1, map, data, w, h);
	}
}
