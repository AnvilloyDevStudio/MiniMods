package io.github.anvilloystudio.minimods.mod.core.ores;

import minicraft.item.Recipe;

public class Recipes {
	public static void init() {
		minicraft.item.Recipes.furnaceRecipes.add(new Recipe("Copper Ingot_1", "Copper Ore_4", "Coal_1"));
		minicraft.item.Recipes.furnaceRecipes.add(new Recipe("Tin Ingot_1", "Tin Ore_4", "Coal_1"));
		minicraft.item.Recipes.furnaceRecipes.add(new Recipe("Lead Ingot_1", "Lead Ore_4", "Coal_1"));
		minicraft.item.Recipes.furnaceRecipes.add(new Recipe("Silver Ingot_1", "Silver Ore_4", "Coal_1"));
		minicraft.item.Recipes.furnaceRecipes.add(new Recipe("Bronze Ingot_8", "Copper Ingot_7", "Tin Ingot_1", "Coal_1"));
		minicraft.item.Recipes.furnaceRecipes.add(new Recipe("Steel Ingot_10", "Iron Ingot_10", "Coal_1"));
	}
}
