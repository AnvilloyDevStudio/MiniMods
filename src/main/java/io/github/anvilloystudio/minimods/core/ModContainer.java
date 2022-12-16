package io.github.anvilloystudio.minimods.core;

import io.github.anvilloystudio.minimods.core.ModVersion.MalformedModVersionFormatException;
import io.github.anvilloystudio.minimods.loader.LoaderInitialization;
import io.github.anvilloystudio.minimods.loader.LoaderUtils;
import io.github.anvilloystudio.minimods.loader.ModLoadingHandler.ModLoadingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class ModContainer {
	public final Class<?> entryClass;
	public final Class<?> preInitClass;
	public final Class<?> initClass;
	public final Manifest manifest;
	public final ModMetadata metadata;
	public final ModSettings settings;
	public final String mixinConfig;
	public final Path jarPath;

	public ModContainer(JarFile jar, URLClassLoader child) {
		try (URLClassLoader gameMobLoader = new URLClassLoader(child.getURLs(), LoaderInitialization.getTargetClassLoader())) {
			metadata = new ModMetadata(new JSONObject(LoaderUtils.readStringFromInputStream(jar.getInputStream(jar.getEntry("mod.json")))));
			if (!metadata.entrypoint.isEmpty()) {
				Class<?> clazz = Class.forName(metadata.entrypoint, false, gameMobLoader);
				boolean valid = false;
				try {
					clazz.getDeclaredMethod("entry");
					valid = true;
				} catch (NoSuchMethodException | SecurityException e) {
					Logger.warn("Method #entry is unable to get in {}.", clazz.getSimpleName(), e);
				} finally {
					if (valid)
						entryClass = clazz;
					else
						entryClass = null;
				}
			} else
				entryClass = null;

			if (!metadata.preInitpoint.isEmpty()) {
				Class<?> clazz = Class.forName(metadata.preInitpoint, false, child);
				boolean valid = false;
				try {
					clazz.getDeclaredMethod("preInit");
					valid = true;
				} catch (NoSuchMethodException | SecurityException e) {
					Logger.warn("Method #preInit is unable to get in {}.", clazz.getSimpleName(), e);
				} finally {
					if (valid)
						preInitClass = clazz;
					else
						preInitClass = null;
				}
			} else
				preInitClass = null;

			if (!metadata.initpoint.isEmpty()) {
				Class<?> clazz = Class.forName(metadata.initpoint, false, gameMobLoader);
				boolean valid = false;
				try {
					clazz.getDeclaredMethod("init");
					valid = true;
				} catch (NoSuchMethodException | SecurityException e) {
					Logger.warn("Method #init is unable to get in {}.", clazz.getSimpleName(), e);
				} finally {
					if (valid)
						initClass = clazz;
					else
						initClass = null;
				}
			} else
				initClass = null;

			ZipEntry entry;
			if ((entry = jar.getEntry("settings.json")) != null) {
				settings = new ModSettings(new JSONObject(LoaderUtils.readStringFromInputStream(jar.getInputStream(entry))));
			} else settings = new ModSettings(new JSONObject());

			manifest = jar.getManifest();

			jarPath = Paths.get(jar.getName());

			if (jar.getEntry(metadata.modId + ".mixins.json") != null)
				mixinConfig = metadata.modId + ".mixins.json";
			else
				mixinConfig = null;
		} catch (IOException | ClassNotFoundException | JSONException | MalformedModVersionFormatException e) {
			throw new ModLoadingException("Unable to load mod file: " + jar.getName(), e);
		}
	}

	public static class ModMetadata {
		public final String modId;
		public final String name;
		public final String description;
		public final String author;
		public final String entrypoint;
		public final String preInitpoint;
		public final String initpoint;
		// Versioning.
		public final ModVersion version;
		// public final ModVersion apiVersion;
		public final VersionRange gameVersion; // The target game version.
		public final VersionRange loaderVersion; // The target loader version.
		// Dependencies.
		private final ArrayList<ModDependency> dependencies = new ArrayList<>();

		private ModMetadata(JSONObject json) throws JSONException, MalformedModVersionFormatException {
			modId = json.getString("id");
			if (modId.isEmpty()) throw new JSONException("modId cannot be empty.");

			name = json.getString("name");
			description = json.optString("description");
			version = new ModVersion(json.optString("version", "1.0.0"));
			// if (json.has("apiVersion")) {
			// 	apiVersion = new ModVersion(json.getString("apiVersion"));
			// } else
			// 	apiVersion = version;

			gameVersion = VersionRange.createFromVersionSpec(json.getString("gameVersion"));
			loaderVersion = VersionRange.createFromVersionSpec(json.getString("loaderVersion"));

			author = json.optString("author");
			entrypoint = json.optString("entrypoint");
			preInitpoint = json.optString("preInitpoint");
			initpoint = json.optString("initpoint");

			if (json.has("dependencies")) {
				JSONArray deps = json.getJSONArray("dependencies");
				for (int i = 0; i < deps.length(); i++) {
					dependencies.add(new ModDependency(deps.getJSONObject(i)));
				}
			}
		}

		public ModDependency[] getDependencies() { return dependencies.toArray(new ModDependency[0]); }

		public static class ModDependency {
			public final String modId;
			public final VersionRange version;
			public final boolean essential;

			public ModDependency(JSONObject json) throws JSONException, MalformedModVersionFormatException {
				modId = json.getString("modId");
				version = VersionRange.createFromVersionSpec(json.getString("version"));
				essential = json.optBoolean("essential", true);
			}
		}
	}

	public static class ModSettings {
		/** Whether to reset default value when the value is out of set bounds in config. */
		public final boolean configValueResetDefaultIfOutOfScope;
		/** Whether to reset default value when the value is not matched with the type of bounds or default value or missing in config. */
		public final boolean configNumericLimitIgnoreOtherTypedValue;

		private ModSettings(JSONObject json) {
			configValueResetDefaultIfOutOfScope = json.optBoolean("configValueResetDefaultIfOutOfScope", true);
			configNumericLimitIgnoreOtherTypedValue = json.optBoolean("configNumericLimitIgnoreOtherTypedValue", false);
		}
	}
}
