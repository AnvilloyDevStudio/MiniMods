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
					Mods.checkModToLoaderCompatibility(mod); // Validating.
					Mods.mods.add(mod);
				} catch (IOException e) {
					throw new ModLoadingException(e);
				}

				ModLoadingHandler.secondaryPro.cur++;
			}
		}

		int count = 0;
        while (true) { // Sorting with their dependencies.
            for (int i = 0; i < Mods.mods.size(); i++) {
                if (count > Mods.mods.size()*Mods.mods.size()) {
                    throw new ModLoadingException("Dependency structure too complex.");
                }

                ModContainer.ModMetadata.ModDependency[] deps = Mods.mods.get(i).metadata.getDependencies();
                if (deps.length > 0) {
                    int index = i;
                    for (int j = 0; j < deps.length; j++) {
                        ModContainer.ModMetadata.ModDependency n = deps[j];
                        int jdx = Mods.mods.indexOf(Mods.mods.stream().filter(m -> m.metadata.modId.equals(n.modId)).findAny().orElse(null));
                        if (jdx == -1) {
                            if (n.essential) throw new ModLoadingException("Dependency not found: " + n.modId);
							Logger.debug("Unessential dependency does not exist: {} for {}", n.modId, Mods.mods.get(i).metadata.modId);
							continue; // Skip if not exist and not essential.
                        } else if (!n.version.containsVersion(Mods.mods.get(jdx).metadata.version)) {
							throw new ModLoadingException("Dependency not compatible: " + n.modId + " " + n.version + "; found: " + Mods.mods.get(jdx).metadata.version);
						}

                        if (jdx > index) index = jdx;
                    }

                    if (index > i) Mods.mods.add(index, Mods.mods.remove(i));
                }

                count++;
            }

            boolean valid = true;
            for (int i = 0; i < Mods.mods.size(); i++) {
                ModContainer.ModMetadata.ModDependency[] deps = Mods.mods.get(i).metadata.getDependencies();
                if (deps.length > 0) {
                    int index = i;
                    for (int j = 0; j < deps.length; j++) {
                        ModContainer.ModMetadata.ModDependency n = deps[j];
                        int jdx = Mods.mods.indexOf(Mods.mods.stream().filter(m -> m.metadata.modId.equals(n.modId)).findAny().orElse(null));
                        if (jdx > index) index = jdx;
                    }

                    if (index > i) valid = false;
                }
            }

            if (valid) break;
        }

		// Adding mods to the classpaths.
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
