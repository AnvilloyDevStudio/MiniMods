package minicraft.entity.furniture;

import minicraft.gfx.Sprite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Lantern extends Furniture {
	public static class Type {
		private static final HashMap<String, Type> registry = new HashMap<>();

		public static void removeRegistry(Type val) {
			registry.remove(val.key, val);
		}
		public static Type getRegistry(String key) {
			return registry.get(key);
		}
		public static void addRegistry(Type val) {
			if (registry.put(val.key, val) != null) {
				System.out.println("[ENTITIES] WARN: Lantern$Type registry replaced: " + val.key);
			}
		}
		public static Type register(Type val) {
			addRegistry(val);
			return val;
		}

		public static Set<Type> getRegistries() {
			return new HashSet<>(registry.values());
		}

		public static final Type NORM = new Type("NORM", "", 0, "Lantern", 9, 0);
		public static final Type IRON = new Type("IRON", "Iron", 1, "Iron Lantern", 12, 2);
		public static final Type GOLD = new Type("GOLD", "Gold", 2, "Gold Lantern", 15, 4);

		public final String key, name;
		public final int id;
		protected int light, offset;
		protected String title;

		Type(String key, String name, int id, String title, int light, int offset) {
			this.key = key;
			this.name = name;
			this.id = id;
			this.title = title;
			this.offset = offset;
			this.light = light;
		}
	}

	public Type type;

	/**
	 * Creates a lantern of a given type.
	 * @param type Type of lantern.
	 */
	public Lantern(Type type) {
		super(type.title, new Sprite(18 + type.offset, 26, 2, 2, 2), 3, 2);
		this.type = type;
	}

	@Override
	public Furniture clone() {
		return new Lantern(type);
	}

	/**
	 * Gets the size of the radius for light underground (Bigger number, larger light)
	 */
	@Override
	public int getLightRadius() {
		return type.light;
	}
}
