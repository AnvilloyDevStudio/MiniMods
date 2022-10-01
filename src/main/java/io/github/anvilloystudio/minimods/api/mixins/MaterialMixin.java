package io.github.anvilloystudio.minimods.api.mixins;

import java.util.ArrayList;
import java.util.Arrays;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.anvilloystudio.minimods.api.MaterialMixinEnumUtil;
import io.github.anvilloystudio.minimods.api.MaterialMixinEnumUtil.MaterialMixinEnumData;
import minicraft.item.ToolType;
import minicraft.level.tile.Tile.Material;

/** Referring https://gist.github.com/LlamaLad7/0b553d5ae04e4eb44d3a1e8558be9151. */
@Mixin(Material.class)
public class MaterialMixin {
	@Shadow(remap = false)
	@Mutable
	private static @Final Material[] $VALUES;

	@Invoker(value = "<init>", remap = false)
	private static Material invokeInit(String internalName, int internalId, ToolType requiredTool) {
		throw new AssertionError();
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lminicraft/level/tile/Tile/Material;$VALUES:[Lminicraft/level/tile/Tile/Material;", shift = At.Shift.AFTER, remap = false), remap = false)
    private static void addVariants(CallbackInfo ci) {
        ArrayList<Material> variants = new ArrayList<>(Arrays.asList($VALUES));
        // This means our code will still work if other mods add more variants!
		for (MaterialMixinEnumData data : MaterialMixinEnumUtil.getData()) {
			Material toAdd = invokeInit(data.internalName, variants.get(variants.size() - 1).ordinal() + 1, data.requiredTool);

			variants.add(toAdd);
		}
        $VALUES = variants.toArray(new Material[0]);
    }
}
