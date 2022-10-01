package io.github.anvilloystudio.minimods.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

public class ToolTypeMixinEnumUtil {
	private static HashSet<ToolTypeMixinEnumData> toAdds = new HashSet<>();

	public static class ToolTypeMixinEnumData {
		public final String internalName;
		public final int xPos, dur;
		public final boolean noLevel;

		public ToolTypeMixinEnumData(String internalName, int xPos, int dur) {
			this.internalName = internalName;
			this.xPos = xPos;
			this.dur = dur;
			noLevel = false;
		}
		public ToolTypeMixinEnumData(String internalName, int xPos, int dur, boolean noLevel) {
			this.internalName = internalName;
			this.xPos = xPos;
			this.dur = dur;
			this.noLevel = noLevel;
		}
	}

	/**
	 * Getting all the available data for adding variants.
	 * @return The list of variant data.
	 */
	public static ArrayList<ToolTypeMixinEnumData> getData() {
		return new ArrayList<>(toAdds);
	}

	/**
	 * Adding a data to the internal set.
	 * @param toAdd The variant data to be added.
	 * @return true if the internal set did not already contain the specified element.
	 */
	public static boolean addVariant(ToolTypeMixinEnumData toAdd) {
		try {
			Method findLoadedClassMethod = ModLoaderCommunication.getMethod("io.github.anvilloystudio.minimods.mixin.ModClassLoader", "findLoadedClassFwd", new Class<?>[] {String.class});
			if (findLoadedClassMethod.invoke(ToolTypeMixinEnumUtil.class.getClassLoader(), "minicraft.item.ToolType") != null) {
				throw new IllegalStateException("minicraft.item.ToolType enum class has been initialized already. No more variants are accepted to be added.");
			}
		} catch (SecurityException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return toAdds.add(toAdd);
	}
}
