package io.github.anvilloystudio.minimods.coremods.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import minicraft.entity.mob.Player;
import minicraft.gfx.Rectangle;

@Mixin(Player.class)
public interface PlayerAccessorMixin {
	@Invoker(value = "getInteractionBox", remap = false)
	Rectangle invokeGetInteractionBox(int range);
}
