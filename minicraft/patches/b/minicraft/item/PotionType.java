package minicraft.item;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PotionType {
	private static final HashMap<String, PotionType> registry = new HashMap<>();

	public static void removeRegistry(PotionType val) {
		registry.remove(val.key, val);
	}
	public static PotionType getRegistry(String key) {
		return registry.get(key);
	}
	public static void addRegistry(PotionType val) {
		if (registry.put(val.key, val) != null) {
			System.out.println("[ITEMS] WARN: PotionType registry replaced: " + val.key);
		}
	}
	public static PotionType register(PotionType val) {
		addRegistry(val);
		return val;
	}

	public static Set<PotionType> getRegistries() {
		return new HashSet<>(registry.values());
	}

	public static final PotionType None = register(new PotionType("NONE", "", Color.get(1, 22, 22, 137), 0));

	public static final PotionType Speed = register(new PotionType("SPEED", "Speed", Color.get(1, 23, 46, 23), 4200) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			player.moveSpeed += (double)( addEffect ? 1 : (player.moveSpeed > 1 ? -1 : 0) );
			return true;
		}
	});

	public static final PotionType Light = register(new PotionType("LIGHT", "Light", Color.get(1, 183, 183, 91), 6000));
	public static final PotionType Swim = register(new PotionType("SWIM", "Swim", Color.get(1, 17, 17, 85), 4800));
	public static final PotionType Energy = register(new PotionType("ENERGY", "Energy", Color.get(1, 172, 80, 57), 8400));
	public static final PotionType Regen = register(new PotionType("REGEN", "Regen", Color.get(1, 168, 54, 146), 1800));

	public static final PotionType Health = register(new PotionType("HEALTH", "Health", Color.get(1, 161, 46, 69), 0) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			if(addEffect) player.heal(5);
			return true;
		}
	});

	public static final PotionType Time = register(new PotionType("TIME", "Time", Color.get(1, 102), 1800));
	public static final PotionType Lava = register(new PotionType("LAVA", "Lava", Color.get(1, 129, 37, 37), 7200));
	public static final PotionType Shield = register(new PotionType("SHIELD", "Shield", Color.get(1, 65, 65, 157), 5400));
	public static final PotionType Haste = register(new PotionType("HASTE", "Haste", Color.get(1, 106, 37, 106), 4800));

	public static final PotionType Escape = register(new PotionType("ESCAPE", "Escape", Color.get(1, 85, 62, 62), 0) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			if (addEffect) {
				int playerDepth = player.getLevel().depth;

				if (playerDepth == 0) {
					if (!Game.isValidServer()) {
						// player is in overworld
						String note = "You can't escape from here!";
						Game.notifications.add(note);
					}
					return false;
				}

				int depthDiff = playerDepth > 0 ? -1 : 1;

				World.scheduleLevelChange(depthDiff, () -> {
					Level plevel = World.levels[World.lvlIdx(playerDepth + depthDiff)];
					if (plevel != null && !plevel.getTile(player.x >> 4, player.y >> 4).mayPass(plevel, player.x >> 4, player.y >> 4, player))
						player.findStartPos(plevel, false);
				});
			}
			return true;
		}
	});

	public final String key;
	public final int dispColor, duration;
	public final String name;

	public PotionType(String key, String name, int col, int dur) {
		this.key = key;
		dispColor = col;
		duration = dur;
		if(this.toString().equals("None")) this.name = "Potion";
		else this.name = name + " Potion";
	}

	public boolean toggleEffect(Player player, boolean addEffect) {
		return duration > 0; // If you have no duration and do nothing, then you can't be used.
	}

	public boolean transmitEffect() {
		return true; // Any effect which could be duplicated and result poorly should not be sent to the server.
		// For the case of the Health potion, the player health is not transmitted separately until after the potion effect finishes, so having it send just gets the change there earlier.
	}

	public static final Collection<PotionType> values = Collections.unmodifiableCollection(registry.values());
}
