package io.github.anvilloystudio.minimods.api.mixins;

import minicraft.item.Item;
import minicraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;

@Mixin(Items.class)
public interface ItemsMixin {
	@Invoker(value = "add", remap = false)
	static void invokeAdd(Item item) {
		throw new AssertionError();
	}

	@Invoker(value = "addAll", remap = false)
	static void invokeAddAll(ArrayList<Item> items) {
		throw new AssertionError();
	}
}
