package minicraft.item;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BucketItem extends StackableItem {

	public static class Fill {
		private static final HashMap<String, Fill> registry = new HashMap<>();

		public static void removeRegistry(Fill val) {
			registry.remove(val.key, val);
		}
		public static Fill getRegistry(String key) {
			return registry.get(key);
		}
		public static void addRegistry(Fill val) {
			if (registry.put(val.key, val) != null) {
				System.out.println("[ITEMS] WARN: BucketItem$Fill registry replaced: " + val.key);
			}
		}
		public static Fill register(Fill val) {
			addRegistry(val);
			return val;
		}

		public static Set<Fill> getRegistries() {
			return new HashSet<>(registry.values());
		}

		public static final Fill Empty = register(new Fill("EMPTY", "Empty", Tiles.get("hole"), 2));
		public static final Fill Water = register(new Fill("WATER", "Water", Tiles.get("water"), 0));
		public static final Fill Lava = register(new Fill("LAVA", "Lava", Tiles.get("lava"), 1));

		public final String key;
		public final String name;
		public final Tile contained;
		public final int offset;

		public Fill(String key, String name, Tile contained, int offset) {
			this.key = key.toUpperCase();
			this.name = name;
			this.contained = contained;
			this.offset = offset;
			addRegistry(this);
		}

		/**
		 * Getting whether this filling is collectable from {@code tile}.
		 * @param tile The source tile
		 * @return {@code true} if this filling can be obtained with the given tile;
		 * {@code false} to hide this filling from the list
		 */
		public boolean fromTile(Tile tile) {
			return contained.id == tile.id;
		}
	}

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		items.add(new BucketItem(Fill.Empty));
		items.add(new BucketItem(Fill.Water));
		items.add(new BucketItem(Fill.Lava));

		return items;
	}

	private static Fill getFilling(Tile tile) {
		for (Fill fill: Fill.getRegistries())
			if (fill.fromTile(tile))
				return fill;

		return null;
	}

	private Fill filling;

	public BucketItem(Fill fill) { this(fill, 1); }
	public BucketItem(Fill fill, int count) {
		super(fill.name + " Bucket", new Sprite(fill.offset, 6, 0), count);
		this.filling = fill;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		Fill fill = getFilling(tile);
		if (fill == null) return false;

		if (filling != Fill.Empty) {
			if (fill == Fill.Empty) {
				level.setTile(xt, yt, filling.contained);
				if (!Game.isMode("creative")) player.activeItem = editBucket(player, Fill.Empty);
				return true;
			} else if (fill == Fill.Lava && filling == Fill.Water) {
				level.setTile(xt, yt, Tiles.get("Obsidian"));
				if (!Game.isMode("creative")) player.activeItem = editBucket(player, Fill.Empty);
				return true;
			}
		} else { // This is an empty bucket
			level.setTile(xt, yt, Tiles.get("hole"));
			if (!Game.isMode("creative")) player.activeItem = editBucket(player, fill);
			return true;
		}

		return false;
	}

	/** This method exists due to the fact that buckets are stackable, but only one should be changed at one time. */
	private BucketItem editBucket(Player player, Fill newFill) {
		if (count == 0) return null; // This honestly should never happen...
		if (count == 1) return new BucketItem(newFill);

		// This item object is a stack of buckets.
		count--;
		player.getInventory().add(new BucketItem(newFill));
		return this;
	}

	public boolean equals(Item other) {
		return super.equals(other) && filling == ((BucketItem)other).filling;
	}

	@Override
	public int hashCode() { return super.hashCode() + filling.offset * 31; }

	public BucketItem clone() {
		return new BucketItem(filling, count);
	}
}
