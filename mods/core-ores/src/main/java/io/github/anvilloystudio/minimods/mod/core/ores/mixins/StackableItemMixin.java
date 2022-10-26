package io.github.anvilloystudio.minimods.mod.core.ores.mixins;

import java.io.IOException;
import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.StackableItem;

@Mixin(StackableItem.class)
public class StackableItemMixin {
	@Invoker(value = "<init>", remap = false)
	private static StackableItem invokeInit(String name, Sprite sprite) {
		throw new AssertionError();
	}

	@Inject(method = "getAllInstances()Ljava/util/ArrayList;", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void injectMoreItems(CallbackInfoReturnable<ArrayList<Item>> ci, ArrayList<Item> items) {
		try {
			items.add(invokeInit("Copper Ore", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/copper_ore.png"))))); // Copper Ore
			items.add(invokeInit("Copper Ingot", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/copper_ingot.png"))))); // Copper Ingot
			items.add(invokeInit("Tin Ore", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/tin_ore.png"))))); // Tin Ore
			items.add(invokeInit("Tin Ingot", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/tin_ingot.png"))))); // Tin Ingot
			items.add(invokeInit("Lead Ore", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/lead_ore.png"))))); // Lead Ore
			items.add(invokeInit("Lead Ingot", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/lead_ingot.png"))))); // Lead Ingot
			items.add(invokeInit("Silver Ore", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/silver_ore.png"))))); // Silver Ore
			items.add(invokeInit("Silver Ingot", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/silver_ingot.png"))))); // Silver Ingot
			items.add(invokeInit("Bronze Ingot", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/bronze_ingot.png"))))); // Bronze Ingot
			items.add(invokeInit("Steel Ingot", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(StackableItem.class.getResourceAsStream("/assets/textures/items/steel_ingot.png"))))); // Steel Ingot
		} catch (IOException e) {
			throw new RuntimeException("Unable to initialize item(s).", e);
		}
	}

	@ModifyArg(method = "getAllInstances()Ljava/util/ArrayList;", at = @At(value = "INVOKE", target = "Lminicraft/item/StackableItem;<init>(Ljava/lang/String;Lminicraft/gfx/Sprite;)V", ordinal = 11, remap = false), index = 0, remap = false)
	private static String modifyNewItemName11(String name) { return "Iron Ingot"; }
	@ModifyArg(method = "getAllInstances()Ljava/util/ArrayList;", at = @At(value = "INVOKE", target = "Lminicraft/item/StackableItem;<init>(Ljava/lang/String;Lminicraft/gfx/Sprite;)V", ordinal = 12, remap = false), index = 0, remap = false)
	private static String modifyNewItemName12(String name) { return "Gold Ingot"; }
}
