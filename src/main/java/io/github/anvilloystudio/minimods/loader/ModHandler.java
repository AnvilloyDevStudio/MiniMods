package io.github.anvilloystudio.minimods.loader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.spongepowered.asm.service.MixinService;

import io.github.anvilloystudio.minimods.api.ModLoaderCommunication;
import io.github.anvilloystudio.minimods.core.ModContainer;
import io.github.anvilloystudio.minimods.core.Mods;

public class ModHandler {
	/** Loading mods by entry. (Post-Init) Invoked in minicraft.core.Game. */
	public static void initMods() {
		try {
			// Since this is invoked inside Game, we need to interact the loader using reflection.
			ModLoaderCommunication.invokeVoid("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "toFront");
			MixinService.getService().getLogger("ModHandler").debug("Start loading mods from entrypoint static method #entry().");
			ModContainer[] mods = ModLoaderCommunication.getModList().stream().filter(m -> m.entryClass != null).toArray(ModContainer[]::new);
			Field secondaryProField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "secondaryPro");
			Field overallProField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler", "overallPro");
			secondaryProField.set(null, ModLoaderCommunication.createInstance(
				"io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", new Class<?>[] {int.class}, new Object[] {mods.length}));
			Object secondaryPro = secondaryProField.get(null);
			Field progressText = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", "text");
			Field progressCur = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.loader.ModLoadingHandler$Progress", "cur");
			for (ModContainer mod : mods) {
				progressText.set(secondaryPro, mod.metadata.name);
				if (mod.entryClass != null) try {
					mod.entryClass.getDeclaredMethod("entry").invoke(null, new Object[0]);
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

	/** Loading mods by init with Init phase. */
	public static void initPhaseMods() {
		MixinService.getService().getLogger("ModHandler").debug("Start loading mods from initpoint static method #init().");
		ModContainer[] mods = Mods.mods.stream().filter(m -> m.initClass != null).toArray(ModContainer[]::new);
		ModLoadingHandler.secondaryPro = new ModLoadingHandler.Progress(mods.length);
		for (ModContainer mod : mods) {
			ModLoadingHandler.secondaryPro.text = mod.metadata.name;
			try {
				mod.initClass.getDeclaredMethod("init").invoke(null, new Object[0]);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}

			ModLoadingHandler.secondaryPro.cur++;
		}
	}
}
