package io.github.anvilloystudio.minimods.loader;

import io.github.anvilloystudio.minimods.core.Mods;

import java.io.File;

public class ModConfigFileHandler {
	public static final String configDir = Mods.gameDir + "/config/" + (LoaderUtils.isInLoaderStage()? "loader": "game");

	private static boolean initialized = false;

	public static void loadLoaderStageModConfig() {
		if (!LoaderUtils.isInLoaderStage())
			throw new IllegalStateException("Loader Stage ModConfig can only be loaded in loader stage.");
		if (initialized)
			throw new IllegalStateException("Loader Stage ModConfig can only be loaded once.");

		File dir = new File(configDir);
		FileHandler.checkAndReplaceWithDir(dir);
		dir.mkdirs();
		File[] files = dir.listFiles(f -> f.isFile() && f.getName().endsWith(".cfg"));
		for (File f : files) {

		}
	}
}
