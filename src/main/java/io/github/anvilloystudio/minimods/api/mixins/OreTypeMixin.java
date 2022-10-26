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

import io.github.anvilloystudio.minimods.api.OreTypeMixinEnumUtil;
import io.github.anvilloystudio.minimods.api.OreTypeMixinEnumUtil.OreTypeMixinEnumData;
import minicraft.item.Item;
import minicraft.level.tile.OreTile.OreType;

/** Referring https://gist.github.com/LlamaLad7/0b553d5ae04e4eb44d3a1e8558be9151. */
@Mixin(OreType.class)
public class OreTypeMixin {
	@Shadow(remap = false)
	@Mutable
	private static @Final OreType[] $VALUES;

	@Invoker(value = "<init>", remap = false)
	private static OreType invokeInit(String internalName, int internalId, Item drop, int color) {
		throw new AssertionError();
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lminicraft/level/tile/OreTile$OreType;$VALUES:[Lminicraft/level/tile/OreTile$OreType;", shift = At.Shift.AFTER, remap = false), remap = false)
    private static void addVariants(CallbackInfo ci) {
        ArrayList<OreType> variants = new ArrayList<>(Arrays.asList($VALUES));
        // This means our code will still work if other mods add more variants!
		for (OreTypeMixinEnumData data : OreTypeMixinEnumUtil.getData()) {
			OreType toAdd = invokeInit(data.internalName, variants.get(variants.size() - 1).ordinal() + 1, data.drop.get(), data.color);

			variants.add(toAdd);
		}
        $VALUES = variants.toArray(new OreType[0]);
    }
}
