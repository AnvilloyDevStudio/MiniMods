package minicraft.entity.furniture;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.item.Recipe;
import minicraft.item.Recipes;
import minicraft.screen.CraftingDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Crafter extends Furniture {

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
				System.out.println("[ENTITIES] WARN: Crafter$Type registry replaced: " + val.key);
			}
		}
		public static Type register(Type val) {
			addRegistry(val);
			return val;
		}

		public static Set<Type> getRegistries() {
			return new HashSet<>(registry.values());
		}

		public static final Type Workbench = register(new Type("WORKBENCH", "Workbench", new Sprite(16, 26, 2, 2, 2), 3, 2, Recipes.workbenchRecipes));
		public static final Type Oven = register(new Type("OVEN", "Oven", new Sprite(12, 26, 2, 2, 2), 3, 2, Recipes.ovenRecipes));
		public static final Type Furnace = register(new Type("FURNACE", "Furnace", new Sprite(14, 26, 2, 2, 2), 3, 2, Recipes.furnaceRecipes));
		public static final Type Anvil = register(new Type("ANVIL", "Anvil", new Sprite(8, 26, 2, 2, 2), 3, 2, Recipes.anvilRecipes));
		public static final Type Enchanter = register(new Type("ENCHANTER", "Enchanter", new Sprite(24, 26, 2, 2, 2), 7, 2, Recipes.enchantRecipes));
		public static final Type Loom = register(new Type("LOOM", "Loom", new Sprite(26, 26, 2, 2, 2), 7, 2, Recipes.loomRecipes));

		public final String key, name;
		public ArrayList<Recipe> recipes;
		protected Sprite sprite;
		protected int xr, yr;


		Type(String key, String name, Sprite sprite, int xr, int yr, ArrayList<Recipe> list) {
			this.key = key;
			this.name = name;
			this.sprite = sprite;
			this.xr = xr;
			this.yr = yr;
			recipes = list;
			Crafter.names.add(name);
		}
	}
	public static ArrayList<String> names = new ArrayList<>();

	public Type type;

	/**
	 * Creates a crafter of a given type.
	 * @param type What type of crafter this is.
	 */
	public Crafter(Type type) {
		super(type.name, type.sprite, type.xr, type.yr);
		this.type = type;
	}

	public boolean use(Player player) {
		Game.setMenu(new CraftingDisplay(type.recipes, type.name, player));
		return true;
	}

	@Override
	public Furniture clone() {
		return new Crafter(type);
	}

	@Override
	public String toString() { return type.name+getDataPrints(); }
}
