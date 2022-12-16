package io.github.anvilloystudio.minimods.loader;

import io.github.anvilloystudio.minimods.core.ModContainer;
import io.github.anvilloystudio.minimods.core.Mods;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class LoaderUtils {
	/**
	 * Reading the string from the input stream.
	 * @param in The input stream to be read.
	 * @return The returned string.
	 */
	public static String readStringFromInputStream(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return String.join("\n", reader.lines().toArray(String[]::new));
	}

	/**
	 * Referring https://stackoverflow.com/a/35083181.
	 * This class is used for getting the caller class from the point.<p>
	 * Use:
	 * <p>
	 * 		<code>
	 *            {@link CallingClass}.{@link CallingClass#INSTANCE}.{@link CallingClass#getCallingClasses()}
	 * 		</code>
	 * </p>
	 */
	@SuppressWarnings("deprecated")
	public static class CallingClass extends SecurityManager {
		public static final CallingClass INSTANCE = new CallingClass();

		public Class[] getCallingClasses() {
			return getClassContext();
		}
	}

	/**
	 * Getting the mod ID of the mod class calling the method calling this.
	 * @return The caller mod ID. `null` if the caller is in the core.
	 * @throws IllegalAccessException if the caller mod information is unable to be obtained.
	 */
	public static String getCallerModId() throws IllegalAccessException {
		return getCallerModId(1);
	}
	/**
	 * Getting the mod ID of the mod class calling the method calling this.
	 * @param i The number of calls tracing back from the method calling this.
	 * @return The caller mod ID. `null` if the caller is in the core.
	 * @throws IllegalAccessException if the caller mod information is unable to be obtained.
	 */
	public static String getCallerModId(int i) throws IllegalAccessException {
		Class<?>[] classes = LoaderUtils.CallingClass.INSTANCE.getCallingClasses();
		ClassLoader cl = classes[3 + i].getClassLoader();
		if (cl == LoaderUtils.class.getClassLoader()) { // If it is coremods.
			return null;
		} else if (cl instanceof URLClassLoader) {
			for (ModContainer mod : Mods.mods) {
				try {
					if (Paths.get(((URLClassLoader) cl).getURLs()[0].toURI()).equals(mod.jarPath))
						return mod.metadata.modId;
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}
		}

		throw new IllegalAccessException("Unable to obtain caller mod information.");
	}

	/**
	 * A simple check if it is now in the loader level.
	 * @return if it is in loader level.
	 */
	public static boolean isInLoaderStage() {
		return LoaderUtils.class.getClassLoader() == ClassLoader.getSystemClassLoader();
	}
}
