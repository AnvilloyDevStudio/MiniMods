package io.github.anvilloystudio.minimods.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import org.tinylog.Logger;

import io.github.anvilloystudio.minimods.core.ModContainer;
import io.github.anvilloystudio.minimods.core.Mods;
import io.github.anvilloystudio.minimods.loader.ModLoadingHandler.ModLoadingException;

public class ModFindHander {
	static void findMods() {
		File[] files = readModsFolder();
		if (files.length == 0) {
			ModLoadingHandler.secondaryPro = new ModLoadingHandler.Progress(1);
			ModLoadingHandler.secondaryPro.cur = 1;
			ModLoadingHandler.secondaryPro.text = "No Mods";
			Logger.info("No mods found.");
		} else {
			ModLoadingHandler.secondaryPro = new ModLoadingHandler.Progress(files.length);
			for (File file : files) {
				ModLoadingHandler.secondaryPro.text = "Found: " + file.getName();
				try (JarFile jar = new JarFile(file)) {
					URLClassLoader child = new URLClassLoader(
						new URL[] {file.toURI().toURL()},
						ModFindHander.class.getClassLoader()
					);

					ModContainer mod = new ModContainer(jar, child);
					// Validating.
					Mods.mods.add(mod);
				} catch (IOException e) {
					throw new ModLoadingException(e);
				}

				ModLoadingHandler.secondaryPro.cur++;
			}
		}

		ModLoadingHandler.secondaryPro.text = "Adding Paths";
		for (ModContainer mod : Mods.mods) {
			LoaderInitialization.addToClassPath(mod.jarPath);
		}
	}

	private static File[] readModsFolder() {
		if (!new File(Mods.gameModsDir).exists())
			new File(Mods.gameModsDir).mkdirs();

		return new File(Mods.gameModsDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
	}
}
