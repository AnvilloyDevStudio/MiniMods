package io.github.anvilloystudio.minimods.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import io.github.anvilloystudio.minimods.api.mixins.LevelGenMixin;

public class ModTileGen {
	public static HashMap<Integer, ArrayList<ModTileGen>> modGens = new HashMap<>();

	public static void replaceTilesWithMod(int layer, byte[] map, byte[] data, int w, int h) {
		if (modGens.containsKey(layer)) {
			for (ModTileGen genObj : modGens.get(layer)) {
				genObj.generation.generate(map, data, layer, w, h, LevelGenMixin.getRandom());
			}
		}
	}

	@FunctionalInterface
	public static interface TileGeneration {
		/**
		 * @param layer 1 ~ -4
		 */
		void generate(byte[] map, byte[] data, int layer, int w, int h, Random random);
	}

	public TileGeneration generation;

	public ModTileGen(int layer, TileGeneration gen) {
		generation = gen;
		if (modGens.containsKey(layer)) modGens.get(layer).add(this);
		else modGens.put(layer, new ArrayList<>(Arrays.asList(this)));
	}
}
