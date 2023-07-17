package io.github.anvilloystudio.minimods.api;

import com.electronwill.nightconfig.core.ConcurrentConfigSpec;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import io.github.anvilloystudio.minimods.loader.FileHandler;
import io.github.anvilloystudio.minimods.loader.LoaderUtils;
import io.github.anvilloystudio.minimods.loader.ModConfigFileHandler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class is used for mod configuration.
 * There are 2 stages of configs:
 * 	The loader stage - available only in pre-init stage.
 * 	The game stage - available only after pre-init stage.
 */
public class ModConfig {
	private static final HashMap<String, ModConfig> configs = new HashMap<>();

	/**
	 * Get the available and registered config from the list.
	 * @param modId The mod ID of the associated mod config.
	 * @return The available associated mod config. `null` otherwise.
	 */
	@Nullable
	public static ModConfig getConfig(String modId) {
		return configs.get(modId);
	}

	/**
	 * Registering the given config to the mod config list.
	 * @param cfg The related mod config.
	 * @return `true` if there was no associated mod config. `false` and the given config was not added to the list otherwise.
	 */
	public static boolean registerConfig(ModConfig cfg) {
		return registerConfig(cfg.modId, cfg);
	}
	/**
	 * Registering the given config to the mod config list.
	 * @param modId The associated mod ID of the given config.
	 * @param cfg The related mod config.
	 * @return `true` if there was no associated mod config. `false` and the given config was not added to the list otherwise.
	 */
	public static boolean registerConfig(String modId, ModConfig cfg) {
		return configs.putIfAbsent(modId, cfg) == null; // The config exists.
	}

	private final String modId;
	private final File file;
	private final CommentedFileConfig config;
	private final ConcurrentConfigSpec configSpec = new ConcurrentConfigSpec();

	/**
	 * Creating a mod config by the mod package it called by.
	 */
	public ModConfig() {
		this.modId = getCallerModId();
		this.file = new File(ModConfigFileHandler.configDir, this.modId + ".cfg");
		FileHandler.checkAndDeleteIfDir(file);
		try {
			//noinspection ResultOfMethodCallIgnored
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		config = CommentedFileConfig.ofConcurrent(file, TomlFormat.instance()).checked();
	}
	/**
	 * Creating a mod config by the mod ID and the file.<br/>
	 * This instance is not added into the config list.
	 * @param modId The mod ID.
	 * @param file The config file (in TOML format).
	 */
	public ModConfig(String modId, File file) {
		this.modId = modId;
		this.file = file;
		config = CommentedFileConfig.of(file, TomlFormat.instance()).checked();
	}

	private static String getCallerModId() {
		try {
			return LoaderUtils.getCallerModId(1);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Getting the mod ID this config associating with.
	 * @return The mod ID.
	 */
	public String getModId() {
		return modId;
	}
	/**
	 * Getting the config instance of this mod config.
	 * @return The config object.
	 */
	public CommentedFileConfig getConfig() {
		return config;
	}
	/**
	 * Getting the config spec instance of this mod config.
	 * @return The config spec object.
	 */
	public ConfigSpec getConfigSpec() {
		return configSpec;
	}

	/**
	 * Checking if the config spec matches the current config.
	 * @return {@code true} if it matches.
	 */
	public boolean validate() {
		return configSpec.isCorrect(config);
	}
	/**
	 * Checking if the config spec matches the current config.
	 * If true, correcting the config by the config spec.
	 * @return The number of modifications.
	 */
	public int validateAndCorrect() {
		return configSpec.correct(config);
	}

	/**
	 * Force reloading all the configs from the config file.
	 * Overriding all existed configs, even if the file is empty.<br/>
	 * Warning: All unsaved changes will be lost.
	 */
	public void forceReloadConfig() {
		config.load();
	}

	/**
	 * Saving all the configs into the config file.
	 * All unloaded changes will be lost.
	 */
	public void saveFile() {
		config.save();
	}
}
