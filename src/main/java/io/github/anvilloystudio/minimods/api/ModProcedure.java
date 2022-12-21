package io.github.anvilloystudio.minimods.api;

import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import minicraft.screen.Display;

import java.util.concurrent.CopyOnWriteArrayList;

public class ModProcedure {
	/** These {@link Display} objects are rendered on GUI HUD. */
	public static final CopyOnWriteArrayList<Display> displays0 = new CopyOnWriteArrayList<>();
	/** These {@link Display} objects are rendered on GUI HUD Debug Screen. */
	public static final CopyOnWriteArrayList<Display> displays1 = new CopyOnWriteArrayList<>();
	/** These {@link Display} objects are rendered on screen. */
	public static final CopyOnWriteArrayList<Display> displays2 = new CopyOnWriteArrayList<>();

	/** These {@link Tickable} objects are invoked when {@link minicraft.core.Updater#paused} is false. */
	public static final CopyOnWriteArrayList<Tickable> tickables0 = new CopyOnWriteArrayList<>();
	/** These {@link Tickable} objects are invoked when {@link minicraft.core.Updater#paused} is true. */
	public static final CopyOnWriteArrayList<Tickable> tickables1 = new CopyOnWriteArrayList<>();
	/** These {@link Tickable} objects are invoked when focus. */
	public static final CopyOnWriteArrayList<Tickable> tickables2 = new CopyOnWriteArrayList<>();
}
