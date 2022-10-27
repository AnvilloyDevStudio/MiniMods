package io.github.anvilloystudio.minimods.mod.core.ores.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import minicraft.entity.mob.Zombie;

@Mixin(Zombie.class)
public class ZombieMixin {
	@ModifyArg(method = "die()V", at = @At(value = "INVOKE", target = "Lminicraft/item/Items;get(Ljava/lang/String;)Lminicraft/item/Item;", ordinal = 3, remap = false), index = 0, remap = false)
	private String modifyDieGetIron(String name) {
		return "Iron Ingot";
	}
}
