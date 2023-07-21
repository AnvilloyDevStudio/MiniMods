package io.github.anvilloystudio.minimods.coremods;

import io.github.anvilloystudio.minimods.coremods.mixins.LevelGenAccessorMixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ModTileGen {
	public static HashMap<Integer, ArrayList<ModTileGen>> modGens = new HashMap<>();

	public static void replaceTilesWithMod(int layer, byte[] map, byte[] data, int w, int h) {
		if (modGens.get(layer) != null) {
			for (ModTileGen genObj : modGens.get(layer)) {
				genObj.generation.generate(map, data, layer, w, h, LevelGenAccessorMixin.getRandom());
			}
		}
	}

	@FunctionalInterface
	public interface TileGeneration {
		/**
		 * @param layer 1 ~ -4
		 */
		void generate(byte[] map, byte[] data, int layer, int w, int h, Random random);
	}

	public final TileGeneration generation;

	public ModTileGen(int layer, TileGeneration gen) {
		generation = gen;
		modGens.computeIfAbsent(layer, k -> new ArrayList<>()).add(this);
	}
}
