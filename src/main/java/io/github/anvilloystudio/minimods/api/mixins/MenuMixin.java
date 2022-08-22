package io.github.anvilloystudio.minimods.api.mixins;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import minicraft.screen.Menu;
import minicraft.screen.entry.ListEntry;

@Mixin(Menu.class)
public interface MenuMixin {
	@Invoker(value = "getEntries", remap = false)
	public ListEntry[] invokeGetEntries();
	@Invoker(value = "updateSelectedEntry", remap = false)
	public void invokeUpdateSelectedEntry(ListEntry newEntry);
	@Invoker(value = "updateEntry", remap = false)
	public void invokeUpdateEntry(int idx, ListEntry newEntry);
	@Accessor(value = "entries", remap = false)
	public ArrayList<ListEntry> getEntries();
}
