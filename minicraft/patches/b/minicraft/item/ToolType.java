package minicraft.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ToolType {
	private static final HashMap<String, ToolType> registry = new HashMap<>();

	public static void removeRegistry(ToolType val) {
		registry.remove(val.key, val);
	}
	public static ToolType getRegistry(String key) {
		return registry.get(key);
	}
	public static void addRegistry(ToolType val) {
		if (registry.put(val.key, val) != null) {
			System.out.println("[ITEMS] WARN: ToolType registry replaced: " + val.key);
		}
	}
	public static ToolType register(ToolType val) {
		addRegistry(val);
		return val;
	}

	public static Set<ToolType> getRegistries() {
		return new HashSet<>(registry.values());
	}

	public static final ToolType Shovel = register(new ToolType("SHOVEL", "Shovel", 0, 24)); // If there's a second number, it specifies durability.
	public static final ToolType Hoe = register(new ToolType("HOE", "Hoe", 1, 20));
	public static final ToolType Sword = register(new ToolType("SWORD", "Sword", 2, 42));
	public static final ToolType Pickaxe = register(new ToolType("PICKAXE", "Pickaxe", 3, 28));
	public static final ToolType Axe = register(new ToolType("AXE", "Axe", 4, 24));
	public static final ToolType Bow = register(new ToolType("BOW", "Bow", 5, 20));
	public static final ToolType Claymore = register(new ToolType("CLAYMORE", "Claymore", 6, 34));
	public static final ToolType Shear = register(new ToolType("SHEAR", "Shear", 0, 42, true));

	public final String key;
	public final String name;
	public final int xPos; // X Position of origin
	public final int yPos; // Y position of origin
	public final int durability;
	public final boolean noLevel;

	/**
	 * Create a tool with four levels: wood, stone, iron, gold, and gem.
	 * All these levels are added automatically but sprites have to be added manually.
	 * Uses line 14 in the item spritesheet.
	 * @param xPos X position of the starting sprite in the spritesheet.
	 * @param dur Durabiltity of the tool.
	 */
	public ToolType(String key, String name, int xPos, int dur) {
		this.key = key;
		this.name = name;
		this.xPos = xPos;
		yPos = 13;
		durability = dur;
		noLevel = false;
	}

	/**
	 * Create a tool without a specified level.
	 * Uses line 13 in the items spritesheet.
	 * @param xPos X position of the sprite in the spritesheet.
	 * @param dur Durabiltity of the tool.
	 * @param noLevel If the tool has only one level.
	 */
	public ToolType(String key, String name, int xPos, int dur, boolean noLevel) {
		this.key = key;
		this.name = name;
		yPos = 12;
		this.xPos = xPos;
		durability = dur;
		this.noLevel = noLevel;
	}
}
