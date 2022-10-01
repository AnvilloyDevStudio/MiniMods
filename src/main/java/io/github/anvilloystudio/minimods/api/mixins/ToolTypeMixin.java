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

import io.github.anvilloystudio.minimods.api.ToolTypeMixinEnumUtil;
import io.github.anvilloystudio.minimods.api.ToolTypeMixinEnumUtil.ToolTypeMixinEnumData;
import minicraft.item.ToolType;

/** Referring https://gist.github.com/LlamaLad7/0b553d5ae04e4eb44d3a1e8558be9151. */
@Mixin(ToolType.class)
public class ToolTypeMixin {
	@Shadow(remap = false)
	@Mutable
	private static @Final ToolType[] $VALUES;

	@Invoker(value = "<init>", remap = false)
	private static ToolType invokeInit(String internalName, int internalId, int xPos, int dur) {
		throw new AssertionError();
	}
	@Invoker(value = "<init>", remap = false)
	private static ToolType invokeInit(String internalName, int internalId, int xPos, int dur, boolean noLevel) {
		throw new AssertionError();
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lminicraft/item/ToolType;$VALUES:[Lminicraft/item/ToolType;", shift = At.Shift.AFTER, remap = false), remap = false)
    private static void addVariants(CallbackInfo ci) {
        ArrayList<ToolType> variants = new ArrayList<>(Arrays.asList($VALUES));
        // This means our code will still work if other mods add more variants!
		for (ToolTypeMixinEnumData data : ToolTypeMixinEnumUtil.getData()) {
			ToolType toAdd = !data.noLevel? invokeInit(data.internalName, variants.get(variants.size() - 1).ordinal() + 1, data.xPos, data.dur)
				: invokeInit(data.internalName, variants.get(variants.size() - 1).ordinal() + 1, data.xPos, data.dur, data.noLevel);

			variants.add(toAdd);
		}
        $VALUES = variants.toArray(new ToolType[0]);
    }
}
