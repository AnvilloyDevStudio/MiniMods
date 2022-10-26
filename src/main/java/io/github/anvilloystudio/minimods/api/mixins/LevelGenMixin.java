package io.github.anvilloystudio.minimods.api.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.anvilloystudio.minimods.api.ModTileGen;
import minicraft.level.LevelGen;

@Mixin(LevelGen.class)
public class LevelGenMixin {
	@Inject(method = "createTopMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void injectCreateTopMap(int w, int h, CallbackInfoReturnable<byte[][]> ci,
			LevelGen mnoise1, LevelGen mnoise2, LevelGen mnoise3, LevelGen noise1, LevelGen noise2,
			byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(0, map, data, w, h);
	}

	@Inject(method = "createDungeon", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void injectCreateDungeon(int w, int h, CallbackInfoReturnable<byte[][]> ci,
			LevelGen noise1, LevelGen noise2,
			byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(-4, map, data, w, h);
	}

	@Inject(method = "createUndergroundMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void injectCreateUndergroundMap(int w, int h, int depth, CallbackInfoReturnable<byte[][]> ci,
			LevelGen mnoise1, LevelGen mnoise2, LevelGen mnoise3, LevelGen nnoise1, LevelGen nnoise2, LevelGen nnoise3,
			LevelGen wnoise1, LevelGen wnoise2, LevelGen wnoise3, LevelGen noise1, LevelGen noise2,
			byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(-depth, map, data, w, h);
	}

	@Inject(method = "createSkyMap", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void injectCreateSkyMap(int w, int h, CallbackInfoReturnable<byte[][]> ci,
			LevelGen noise1, LevelGen noise2,
			byte[] map, byte[] data) {
		ModTileGen.replaceTilesWithMod(1, map, data, w, h);
	}
}
