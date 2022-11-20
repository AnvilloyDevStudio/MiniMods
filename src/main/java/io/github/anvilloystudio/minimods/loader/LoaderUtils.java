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

	public static String getCallerModId() throws IllegalAccessException {
		Class<?>[] classes = LoaderUtils.CallingClass.INSTANCE.getCallingClasses();
		ClassLoader cl = classes[3].getClassLoader();
		if (cl == LoaderUtils.class.getClassLoader()) { // If it is coremods.
			return null;
		} else if (cl instanceof URLClassLoader) {
			if (isInLoaderStage()) { // If it is in loader level.
				for (ModContainer mod : Mods.mods) {
					try {
						if (Paths.get(((URLClassLoader) cl).getURLs()[0].toURI()).equals(mod.jarPath))
							return mod.metadata.modId;
					} catch (URISyntaxException e) {
						throw new RuntimeException(e);
					}
				}
			} else { // If it is in Game class.
				// TODO
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
