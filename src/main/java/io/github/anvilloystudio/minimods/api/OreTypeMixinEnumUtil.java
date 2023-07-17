package io.github.anvilloystudio.minimods.api;

import minicraft.item.Item;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

public class OreTypeMixinEnumUtil {
	private static final HashSet<OreTypeMixinEnumData> toAdds = new HashSet<>();

	public static class OreTypeMixinEnumData {
		public final String internalName;
		public final Supplier<Item> drop;
		public final int color;

		public OreTypeMixinEnumData(String internalName, Supplier<Item> drop, int color) {
			this.internalName = internalName;
			this.drop = drop;
			this.color = color;
		}
	}

	/**
	 * Getting all the available data for adding variants.
	 * @return The list of variant data.
	 */
	public static ArrayList<OreTypeMixinEnumData> getData() {
		return new ArrayList<>(toAdds);
	}

	/**
	 * Adding a data to the internal set.
	 * @param toAdd The variant data to be added.
	 * @return true if the internal set did not already contain the specified element.
	 */
	public static boolean addVariant(OreTypeMixinEnumData toAdd) {
		try {
			Method findLoadedClassMethod = ModLoaderCommunication.getMethod("io.github.anvilloystudio.minimods.mixin.ModClassLoader", "findLoadedClassFwd", new Class<?>[] {String.class});
			if (findLoadedClassMethod.invoke(ToolTypeMixinEnumUtil.class.getClassLoader(), "minicraft.level.tile.OreTile.OreType") != null) {
				throw new IllegalStateException("minicraft.level.tile.OreTile.OreType enum class has been initialized already. No more variants are accepted to be added.");
			}
		} catch (SecurityException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return toAdds.add(toAdd);
	}
}
