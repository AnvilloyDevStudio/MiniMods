package io.github.anvilloystudio.minimods.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.github.anvilloystudio.minimods.loader.LoaderInitialization;
import io.github.anvilloystudio.minimods.loader.ModLoadingHandler;
import io.github.anvilloystudio.minimods.loader.ModLoadingHandler.ModLoadingException;
import minicraft.core.Game;
import minicraft.saveload.Version;

import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class Mods {
	public static final ArrayList<ModContainer> mods = new ArrayList<>();

    public static final Version VERSION = new Version("0.4.0");

	public static final ModVersion COMPAT_GAME_VERSION = new ModVersion(Game.VERSION.toString());
	public static final ModVersion COMPAT_LOADER_VERSION = new ModVersion(VERSION.toString());

	// A copy from FileHandler, this may need to update from the original code when updating
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
		if (debug) System.setProperty("mixin.debug", "true");
		if (OS.equalsIgnoreCase("windows 10")) { // https://stackoverflow.com/a/52767586
			// Set output mode to handle virtual terminal sequences
			Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
			DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
			HANDLE hOut = (HANDLE)GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

			DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
			Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
			GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

			int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
			DWORD dwMode = p_dwMode.getValue();
			dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
			Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
			SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
		}

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

	public static void checkModToLoaderCompatibility(ModContainer mod) throws ModLoadingException {
		ModContainer.ModMetadata meta = mod.metadata;
		if (!meta.gameVersion.containsVersion(COMPAT_GAME_VERSION))
			throw new ModLoadingException(String.format("Incompatible mod: %s: compatible game version %s; current: %s",
				meta.modId, meta.gameVersion.toString(), Game.VERSION));
		if (!meta.loaderVersion.containsVersion(COMPAT_LOADER_VERSION))
			throw new ModLoadingException(String.format("Incompatible mod: %s: compatible loader version %s; current: %s",
				meta.modId, meta.loaderVersion.toString(), VERSION));
	}
}
