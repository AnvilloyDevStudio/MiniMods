package io.github.anvilloystudio.minimods.mixin;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class ModMixinServiceBootstrap implements IMixinServiceBootstrap {
	@Override
	public String getName() {
		return "MiniMods";
	}

	@Override
	public String getServiceClassName() {
		return "minicraft.minimods.mixin.ModMixinService";
	}

	@Override
	public void bootstrap() {
		// already done in LoaderInitialization.
	}
}
