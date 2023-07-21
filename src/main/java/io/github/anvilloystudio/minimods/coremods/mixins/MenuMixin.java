package io.github.anvilloystudio.minimods.coremods.mixins;

import minicraft.screen.Menu;
import minicraft.screen.entry.ListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;

@Mixin(Menu.class)
public interface MenuMixin {
	@Invoker(value = "getEntries", remap = false)
	ListEntry[] invokeGetEntries();
	@Invoker(value = "updateSelectedEntry", remap = false)
	void invokeUpdateSelectedEntry(ListEntry newEntry);
	@Invoker(value = "updateEntry", remap = false)
	void invokeUpdateEntry(int idx, ListEntry newEntry);
	@Accessor(value = "entries", remap = false)
	ArrayList<ListEntry> getEntries();
}
