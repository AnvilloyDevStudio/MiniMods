package io.github.anvilloystudio.minimods.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import minicraft.level.tile.Tile;

public class TileConnections extends HashMap<String, Boolean> {
	public TileConnections() {
		for (String name : Classes.Map.keySet()) put(name, false);
	}

	public ArrayList<Short> getTiles(String name) {
		return Classes.get(name);
	}

	public boolean get(String name) {
		return super.get(name.toUpperCase());
	}
	@Override
	public Boolean get(Object key) {
		if (key instanceof String)
			return get((String) key);
		else
			return super.get(key);
	}

	public void set(String name, boolean value) {
		put(name, value);
	}
	@Override
	public Boolean put(String key, Boolean value) {
		return super.put(key.toUpperCase(), value);
	}

	public static class Classes {
		private static HashMap<String, ArrayList<Short>> Map = new HashMap<>();

		static {
			put("grass", new ArrayList<Short>(Arrays.asList((short)0)));
			put("sand", new ArrayList<Short>(Arrays.asList((short)10)));
			put("fluid", new ArrayList<Short>(Arrays.asList((short)0)));
		}

		public static void put(String k, ArrayList<Short> v) {
			Map.put(k.toUpperCase(), v);
		}
		public static void putOrAdd(String k, short... v) {
			if (Map.containsKey(k.toUpperCase())) add(k, v);
			else {
				ArrayList<Short> m = new ArrayList<>();
				for (short value : v) m.add(value);
				put(k, m);
			}
		}

		public static void putOrAdd(String k, ArrayList<Short> v) {
			if (Map.containsKey(k.toUpperCase())) add(k, v);
			else put(k, v);
		}

		public static ArrayList<Short> get(String k) {
			return Map.get(k.toUpperCase());
		}

		public static void add(String name, short... values) {
			ArrayList<Short> m = get(name);
			for (short v : values) m.add(v);
		}

		public static void add(String name, short v) {
			get(name).add(v);
		}

		public static void add(String name, ArrayList<Short> v) {
			get(name).addAll(v);
		}
	}

	public static boolean putGrass(Tile tile, boolean connect) {
		return tile.connectsToGrass = connect;
	}
	public static boolean putSand(Tile tile, boolean connect) {
		return tile.connectsToSand = connect;
	}
	public static boolean putFluid(Tile tile, boolean connect) {
		return tile.connectsToFluid = connect;
	}

	public static boolean getGrass(Tile tile) {
		return tile.connectsToGrass;
	}
	public static boolean getSand(Tile tile) {
		return tile.connectsToSand;
	}
	public static boolean getFluid(Tile tile) {
		return tile.connectsToFluid;
	}

	/**
	 * Convert all boolean fields for connections in the {@code Tile} object into the {@code TileConnections}.
	 * @param connects The {@link TileConnections} object.
	 * @param tile The {@link Tile} object.
	 * @return The {@link TileConnections} object provided.
	 */
	public static TileConnections convertBoolToObj(TileConnections connects, Tile tile) {
		connects.put("grass", tile.connectsToGrass);
		connects.put("sand", tile.connectsToSand);
		connects.put("fluid", tile.connectsToFluid);
		return connects;
	}
}
