package io.github.anvilloystudio.minimods.coremods.mixins;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import minicraft.entity.Entity;
import minicraft.level.Level;

@Mixin(Level.class)
public interface LevelMixin {
	@Accessor(value = "entities", remap = false)
	public Set<Entity> getEntities();
}
