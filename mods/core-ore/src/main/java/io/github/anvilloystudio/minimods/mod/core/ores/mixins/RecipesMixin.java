package io.github.anvilloystudio.minimods.mod.core.ores.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import minicraft.item.Recipes;

@Mixin(Recipes.class)
public class RecipesMixin {
	// Recipe of L33 Anvil * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 16, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe16(String[] reqItems) { return new String[] {"Iron Ingot_5"}; }
	// Recipe of L37 Iron Fishing Rod * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 20, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe20(String[] reqItems) { return new String[] {"Iron Ingot_10", "String_3"}; }
	// Recipe of L38 Gold Fishing Rod * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 21, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe21(String[] reqItems) { return new String[] {"Gold Ingot_10", "String_3"}; }
	// Recipe of L77 Iron Armor * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 54, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe54(String[] reqItems) { return new String[] {"Iron Ingot_10"}; }
	// Recipe of L78 Gold Armor * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 55, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe55(String[] reqItems) { return new String[] {"Gold Ingot_10"}; }
	// Recipe of L80 Empty Bucket * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 57, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe57(String[] reqItems) { return new String[] {"Iron Ingot_5"}; }
	// Recipe of L81 Iron Lantern * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 58, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe58(String[] reqItems) { return new String[] {"Iron Ingot_8", "slime_5", "glass_4"}; }
	// Recipe of L82 Gold Lantern * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 59, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe59(String[] reqItems) { return new String[] {"Gold Ingot_10", "slime_5", "glass_4"}; }
	// Recipe of L83 Iron Sword * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 60, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe60(String[] reqItems) { return new String[] {"Wood_5", "Iron Ingot_5"}; }
	// Recipe of L85 Iron Axe * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 62, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe62(String[] reqItems) { return new String[] {"Wood_5", "Iron Ingot_5"}; }
	// Recipe of L86 Iron Hoe * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 63, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe63(String[] reqItems) { return new String[] {"Wood_5", "Iron Ingot_5"}; }
	// Recipe of L87 Iron Pickaxe * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 64, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe64(String[] reqItems) { return new String[] {"Wood_5", "Iron Ingot_5"}; }
	// Recipe of L88 Iron Shovel * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 65, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe65(String[] reqItems) { return new String[] {"Wood_5", "Iron Ingot_5"}; }
	// Recipe of L89 Iron Bow * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 66, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe66(String[] reqItems) { return new String[] {"Wood_5", "Iron Ingot_5", "string_2"}; }
	// Recipe of L90 Gold Sword * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 67, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe67(String[] reqItems) { return new String[] {"Wood_5", "Gold Ingot_5"}; }
	// Recipe of L92 Gold Axe * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 69, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe69(String[] reqItems) { return new String[] {"Wood_5", "Gold Ingot_5"}; }
	// Recipe of L93 Gold Hoe * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 70, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe70(String[] reqItems) { return new String[] {"Wood_5", "Gold Ingot_5"}; }
	// Recipe of L94 Gold Pickaxe * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 71, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe71(String[] reqItems) { return new String[] {"Wood_5", "Gold Ingot_5"}; }
	// Recipe of L95 Gold Shovel * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 72, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe72(String[] reqItems) { return new String[] {"Wood_5", "Gold Ingot_5"}; }
	// Recipe of L96 Gold Bow * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 73, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe73(String[] reqItems) { return new String[] {"Wood_5", "Gold Ingot_5", "string_2"}; }
	// Recipe of L104 Shear * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 81, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe81(String[] reqItems) { return new String[] {"Iron Ingot_4"}; }
	// Recipe of L106 (Iron -> Iron Ingot) * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 82, remap = false), index = 0, remap = false)
	private static String modifyClInitNewRecipe82R(String reqItems) { return "Iron Ingot_1"; }
	// Recipe of L107 (Gold -> Gold Ingot) * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 83, remap = false), index = 0, remap = false)
	private static String modifyClInitNewRecipe83R(String reqItems) { return "Gold Ingot_1"; }
	// Recipe of L116 Gold Apple * 1
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lminicraft/item/Recipe;<init>(Ljava/lang/String;[Ljava/lang/String;)V", ordinal = 90, remap = false), index = 1, remap = false)
	private static String[] modifyClInitNewRecipe90(String[] reqItems) { return new String[] {"apple_1", "Gold Ingot_8"}; }
}
