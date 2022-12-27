package minicraft.item;

import minicraft.core.Game;
import minicraft.entity.mob.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Recipe {
	public static final HashMap<String, HashSet<String>> costSets = new HashMap<>();

	private HashMap<String, Integer> costs = new HashMap<String, Integer>();  // A list of costs for the recipe
	private String product; // The result item of the recipe
	private int amount;
	private boolean canCraft; // Checks if the player can craft the recipe

	public Recipe(String createdItem, String... reqItems) {
		canCraft = false;
		String[] sep = createdItem.split("_");
		product = sep[0].toUpperCase(); // Assigns the result item
		amount = Integer.parseInt(sep[1]);

		for (int i = 0; i < reqItems.length; i++) {
			String[] curSep = reqItems[i].split("_");
			String curItem = curSep[0].toUpperCase(); // The current cost that's being added to costs.
			int amt = Integer.parseInt(curSep[1]);
			boolean added = false;

			for (String cost: costs.keySet().toArray(new String[0])) { // Loop through the costs that have already been added
				if (cost.equals(curItem)) {
					costs.put(cost, costs.get(cost)+amt);
					added = true;
					break;
				}
			}

			if (added) continue;
			costs.put(curItem, amt);
		}
	}

	public Item getProduct() {
		return Items.get(product);
	}
	public HashMap<String, Integer> getCosts() { return costs; }

	public int getAmount() { return amount; }
	public boolean getCanCraft() { return canCraft; }
	public boolean checkCanCraft(Player player) {
		canCraft = getCanCraft(player);
		return canCraft;
	}
	/** Checks if the player can craft the recipe */
	private boolean getCanCraft(Player player) {
		if (Game.isMode("creative")) return true;

		for (String cost: costs.keySet().toArray(new String[0])) { // Cycles through the costs list
			/// This method ONLY WORKS if costs does not contain two elements such that inventory.count will count an item it contains as matching more than once.
			if (player.getInventory().count(Items.get(cost)) < costs.get(cost)) {
				return false;
			}
		}

		return true;
	}

	// (WAS) abstract method given to the sub-recipe classes.
	public boolean craft(Player player) {
		if (!getCanCraft(player)) return false;

		if (!Game.isMode("creative")) {
			// Remove the cost items from the inventory.
			for (String cost: costs.keySet().toArray(new String[0])) {
				player.getInventory().removeItems(Items.get(cost), costs.get(cost));
			}
		}

		// Rdd the crafted items.
		for (int i = 0; i < amount; i++)
			 player.getInventory().add(getProduct());

		return true;
	}

	// This plan is deprecated.
	public static class RecipeCost {
		private HashMap<String, Integer> costs = new HashMap<>();
		private Set<String> items = new HashSet<>();
		int defaultAmount;

		/**
		 * Initializing the instance with singleton item.
		 * @param item The item as the cost.
		 * @param amount The amount of the item required as cost.
		 */
		public RecipeCost(String item, int amount) {
			costs.put(item, amount);
			items.add(item);
			defaultAmount = amount;
		}
		/**
		 * Initializing the instance with the specified items.
		 * @param amount The amount of the item required of one as cost.
		 * @param items The items as the cost.
		 */
		public RecipeCost(int amount, String... items) {
			for (String item : items)
				costs.put(item, amount);
			this.items.addAll(Arrays.asList(items));
			defaultAmount = amount;
		}
		/**
		 * Initializing the instance with the specified items.
		 * @param amount The amount of the item required of one as cost.
		 * @param items The items as the cost.
		 */
		public RecipeCost(int amount, Collection<String> items) {
			for (String item : items)
				costs.put(item, amount);
			this.items.addAll(items);
			defaultAmount = amount;
		}
		/**
		 * Initializing the instance with the specified items.
		 * @param amount The amount of the item required of one as cost.
		 * @param items The items as the cost.
		 * @param link Whether to use the given set instance or not.
		 */
		public RecipeCost(int amount, Set<String> items, boolean link) {
			defaultAmount = amount;
			for (String item : items)
				costs.put(item, amount);
			if (link)
				this.items = items;
			else
				this.items.addAll(items);
		}
		/**
		 * Initializing the instance with the specified item set.
		 * @param defaultAmount The default amount of the newly added item required of one as cost.
		 * @param costs The cost mapping.
		 * @param link Whether to use the given map instance or not.
		 */
		public RecipeCost(int defaultAmount, HashMap<String, Integer> costs, boolean link) {
			this.defaultAmount = defaultAmount;
			if (link) {
				this.costs = costs;
				this.items.addAll(costs.keySet());
			} else {
				this.costs.putAll(costs);
				this.items.addAll(costs.keySet());
			}
		}
		/**
		 * Initializing the instance with the specified item set.
		 * Warning: The map {@code costs} may be modified (add and remove) to synchronize all the values in the set {@code items}.
		 * @param defaultAmount The default amount of the newly added item required of one as cost.
		 * @param items The items as the cost.
		 * @param costs The cost mapping.
		 * @param link Whether to use the given map and set instances or not.
		 */
		public RecipeCost(int defaultAmount, HashSet<String> items, HashMap<String, Integer> costs, boolean link) {
			this.defaultAmount = defaultAmount;
			if (link) {
				this.costs = costs;
				this.items = items;
			} else {
				this.costs.putAll(costs);
				this.items.addAll(items);
			}

			for (String item : items) {
				if (!costs.containsKey(item))
					costs.put(item, defaultAmount);
			}
			costs.keySet().removeIf(s -> !items.contains(s));
		}

		/**
		 * Getting the set of available cost.
		 * @return The set of cost.
		 */
		public Set<String> getItems() {
			return new HashSet<>(items);
		}
		/**
		 * Getting the set of available cost linked.
		 * @return The set of linked cost.
		 */
		public Set<String> getLinkedItems() {
			return items;
		}

		/**
		 * Getting the amount of the specified item.
		 * @param item The item available in the costs.
		 * @return The amount of cost.
		 */
		public int getAmount(String item) {
			return costs.get(item);
		}

		/**
		 * Adding the cost into the list. Adding to the original value if existed.
		 * @param item The item as cost.
		 * @param amount The amount of the item required as cost.
		 * @return {@code true} if original value exists.
		 */
		public boolean addCost(String item, int amount) { return addCost(item, amount, false); }
		/**
		 * Adding the cost into the list.
		 * @param item The item as cost.
		 * @param amount The amount of the item required as cost.
		 * @param replace Whether to replace the original value if existed or add to the value.
		 * @return {@code true} if original value exists.
		 */
		public boolean addCost(String item, int amount, boolean replace) {
			items.add(item);
			if (replace || !costs.containsKey(item))
				return costs.put(item, amount) != null;
			else
				costs.put(item, costs.get(item) + amount);
			return true;
		}

		/**
		 * Setting the cost map values and reset the item list by the keys.
		 * @param costs The cost map.
		 */
		public void setCosts(HashMap<String, Integer> costs) {
			this.costs.clear();
			this.costs.putAll(costs);
			items.clear();
			items.addAll(costs.keySet());
		}
		/**
		 * Setting the cost map to the given instance and reset the item list by the keys.
		 * @param costs The cost map.
		 */
		public void setLinkedCosts(HashMap<String, Integer> costs) {
			this.costs = costs;
			items.clear();
			items.addAll(costs.keySet());
		}

		/**
		 * Getting the cost map.
		 * @return The new cost map instance.
		 */
		public HashMap<String, Integer> getCosts() {
			return new HashMap<>(costs);
		}
		/**
		 * Getting the cost map instance.
		 * @return The cost map instance.
		 */
		public HashMap<String, Integer> getLinkedCosts() {
			return costs;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			else if (o instanceof RecipeCost) {
				return defaultAmount == ((RecipeCost) o).defaultAmount &&
					costs.equals(((RecipeCost) o).costs) &&
					items.equals(((RecipeCost) o).items);
			} else
				return false;
		}

		@Override
		public int hashCode() {
			return 31 * (31 * (costs.hashCode() + items.hashCode()) + defaultAmount);
		}
	}
}
