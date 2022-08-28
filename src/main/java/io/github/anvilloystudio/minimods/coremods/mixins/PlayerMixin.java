package io.github.anvilloystudio.minimods.coremods.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import minicraft.entity.mob.Player;
import minicraft.gfx.Rectangle;

@Mixin(Player.class)
public interface PlayerMixin {
	@Invoker(value = "getInteractionBox", remap = false)
	public Rectangle invokeGetInteractionBox(int range);
}
