package io.github.anvilloystudio.minimods.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.github.anvilloystudio.minimods.loader.LoaderInitialization;
import io.github.anvilloystudio.minimods.loader.ModContainer;
import io.github.anvilloystudio.minimods.loader.ModLoadingHandler;

public class Mods {
	public static final ArrayList<ModContainer> mods = new ArrayList<>();

    public static final String MODSVERSION = "0.4.0";

	// TODO A copy from Game#VERSION, this needed to be updated when updating
	public static final String GAMEVERSION = "2.1.3";

	// TODO A copy from FileHandler, this may need to update from the original code when updating
	public static final String OS;
	public static final String localGameDir;
	public static final String systemGameDir;
	public static String gameDir;
	public static String gameModsDir;
	public static boolean debug;
	public static boolean logClassLoad = false;

	// Copied from FileHandler.
	static {
		OS = System.getProperty("os.name").toLowerCase();
		String local = "playminicraft/mods/Minicraft_Plus";

		if (OS.contains("windows")) // windows
			systemGameDir = System.getenv("APPDATA");
		else {
			systemGameDir = System.getProperty("user.home");
			if (!OS.contains("mac"))
				local = "." + local; // linux
		}

		localGameDir = "/" + local;
		gameDir = systemGameDir + localGameDir;
		gameModsDir = systemGameDir + localGameDir + "/mods";
	}

	public final static String entrypoint = "minicraft.core.Game";

    public static void init() {
		LoaderInitialization.init();
	}

	public static void setDebug(boolean debug) {
		Mods.debug = debug;
	}

	public static void launchGame(ClassLoader loader, String[] args) {
		ModLoadingHandler.overallPro.cur = 6;
		ModLoadingHandler.overallPro.text = "Phase 3: Post-Init";
		ModLoadingHandler.secondaryPro = null;
		try {
			Class<?> c = loader.loadClass(entrypoint);
			Method m = c.getMethod("main", String[].class);
			m.invoke(null, (Object) args);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Game has crashed", e.getCause());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to start game", e);
		}
	}
}
