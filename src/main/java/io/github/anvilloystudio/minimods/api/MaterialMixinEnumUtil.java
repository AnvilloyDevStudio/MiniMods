package io.github.anvilloystudio.minimods.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import minicraft.item.ToolType;

public class MaterialMixinEnumUtil {
	private static HashSet<MaterialMixinEnumData> toAdds = new HashSet<>();

	public static class MaterialMixinEnumData {
		public final String internalName;
		public final ToolType requiredTool;

		public MaterialMixinEnumData(String internalName, ToolType requiredTool) {
			this.internalName = internalName;
			this.requiredTool = requiredTool;
		}
	}

	/**
	 * Getting all the available data for adding variants.
	 * @return The list of variant data.
	 */
	public static ArrayList<MaterialMixinEnumData> getData() {
		return new ArrayList<>(toAdds);
	}

	/**
	 * Adding a data to the internal set.
	 * @param toAdd The variant data to be added.
	 * @return true if the internal set did not already contain the specified element.
	 */
	public static boolean addVariant(MaterialMixinEnumData toAdd) {
		try {
			Method findLoadedClassMethod = ModLoaderCommunication.getMethod("io.github.anvilloystudio.minimods.mixin.ModClassLoader", "findLoadedClassFwd", new Class<?>[] {String.class});
			if (findLoadedClassMethod.invoke(ToolTypeMixinEnumUtil.class.getClassLoader(), "minicraft.level.tile.Tile.Material") != null) {
				throw new IllegalStateException("minicraft.level.tile.Tile.Material enum class has been initialized already. No more variants are accepted to be added.");
			}
		} catch (SecurityException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return toAdds.add(toAdd);
	}
}
