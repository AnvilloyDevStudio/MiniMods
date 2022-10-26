package io.github.anvilloystudio.minimods.loader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.spongepowered.asm.service.MixinService;

import io.github.anvilloystudio.minimods.api.ModLoaderCommunication;
import io.github.anvilloystudio.minimods.core.ModContainer;
import io.github.anvilloystudio.minimods.core.Mods;
import io.github.anvilloystudio.minimods.coremods.Initialization;
import io.github.anvilloystudio.minimods.coremods.PostInitialization;
import io.github.anvilloystudio.minimods.coremods.PreInitialization;

public class ModHandler {
	/** Loading mods by entry. (Post-Init) Invoked in minicraft.core.Game. */
	public static void initMods() {
		try {
			// Since this is invoked inside Game, we need to interact the loader using reflection.
			ModLoaderCommunication.invokeVoid("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "toFront");
			MixinService.getService().getLogger("ModHandler").debug("Start loading mods from entrypoint static method #entry().");
			Field secondaryProField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "secondaryPro");
			secondaryProField.set(null, null);
			Field overallProField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "overallPro");
			Field progressText = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", "text");
			Field progressCur = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", "cur");

			progressCur.set(overallProField.get(null), 6);
			progressText.set(overallProField.get(null), "Phase 3: Post-Init");

			Field modEntryClassField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer", "entryClass");
			Field modMetadataField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer", "metadata");
			Field modMetadataNameField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer$ModMetadata", "name");

			Object[] mods = ModLoaderCommunication.getModList().stream().filter(m -> {
				try {
					return modEntryClassField.get(m) != null;
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					throw new RuntimeException(e1);
				}
			}).toArray();
			Object secondaryPro = ModLoaderCommunication.createInstance(
				"io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", new Class<?>[] {int.class}, new Object[] {mods.length});
			secondaryProField.set(null, secondaryPro);

			// Init coremods.
			progressText.set(secondaryPro, "MiniMods Coremods");
			PostInitialization.entry();
			for (Object mod : mods) {
				progressText.set(secondaryPro, modMetadataNameField.get(modMetadataField.get(mod)));
				if (modEntryClassField.get(mod) != null) try {
					((Class<?>) modEntryClassField.get(mod)).getDeclaredMethod("entry").invoke(null, new Object[0]);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}

				progressCur.set(secondaryPro, (int) progressCur.get(secondaryPro) + 1);
			}

			progressText.set(secondaryPro, "Completed");
			secondaryProField.set(null, null);
			progressText.set(overallProField.get(null), "Completed");

			try { // Wait a bit.
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			ModLoaderCommunication.invokeVoid("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "closeWindow");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | NoSuchFieldException | InstantiationException e1) {
			throw new RuntimeException(e1);
		}
	}

	/** Loading mods by preInit with PreInit phase. */
	public static void preInitPhaseMods() {
		MixinService.getService().getLogger("ModHandler").debug("Start loading mods from preInitpoint static method #preInit().");
		ModContainer[] mods = Mods.mods.stream().filter(m -> m.preInitClass != null).toArray(ModContainer[]::new);
		ModLoadingHandler.secondaryPro = new ModLoadingHandler.Progress(mods.length);

		// Init coremods.
		ModLoadingHandler.secondaryPro.text = "MiniMods Coremods";
		PreInitialization.preInit();

		for (ModContainer mod : mods) {
			ModLoadingHandler.secondaryPro.text = mod.metadata.name;
			try {
				mod.preInitClass.getDeclaredMethod("preInit").invoke(null, new Object[0]);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}

			ModLoadingHandler.secondaryPro.cur++;
		}
	}

	/** Loading mods by init with "Init" phase. */
	public static void initPhaseMods() {
		try {
			// Since this is invoked inside Game, we need to interact the loader using reflection.
			ModLoaderCommunication.invokeVoid("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "toFront");
			MixinService.getService().getLogger("ModHandler").debug("Start loading mods from entrypoint static method #init().");
			Field secondaryProField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "secondaryPro");
			Field progressText = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", "text");
			Field progressCur = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", "cur");

			Field modInitClassField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer", "initClass");
			Field modMetadataField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer", "metadata");
			Field modMetadataNameField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer$ModMetadata", "name");

			Object[] mods = ModLoaderCommunication.getModList().stream().filter(m -> {
				try {
					return modInitClassField.get(m) != null;
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					throw new RuntimeException(e1);
				}
			}).toArray();
			Object secondaryPro = ModLoaderCommunication.createInstance(
				"io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", new Class<?>[] {int.class}, new Object[] {mods.length});
			secondaryProField.set(null, secondaryPro);

			// Init coremods.
			progressText.set(secondaryPro, "MiniMods Coremods");
			Initialization.init();
			for (Object mod : mods) {
				progressText.set(secondaryPro, modMetadataNameField.get(modMetadataField.get(mod)));
				if (modInitClassField.get(mod) != null) try {
					((Class<?>) modInitClassField.get(mod)).getDeclaredMethod("init").invoke(null, new Object[0]);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}

				progressCur.set(secondaryPro, (int) progressCur.get(secondaryPro) + 1);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | NoSuchFieldException | InstantiationException e1) {
			throw new RuntimeException(e1);
		}
	}
}
