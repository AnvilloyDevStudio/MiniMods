package io.github.anvilloystudio.minimods.coremods.interfaces;

import io.github.anvilloystudio.minimods.coremods.GraphicComp;
import minicraft.gfx.SpriteSheet;

/** This class should only be implemented by {@link minicraft.gfx.Screen}. */
public interface ScreenRenderer {
	void render(int xp, int yp, int tile, int bits, SpriteSheet sheet, int whiteTint, boolean fullbright, GraphicComp.SpriteRenderer.Rotation rotation);
}
