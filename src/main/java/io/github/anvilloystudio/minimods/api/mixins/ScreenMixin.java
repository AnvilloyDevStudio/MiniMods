package io.github.anvilloystudio.minimods.api.mixins;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.interfaces.ScreenRenderer;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenRenderer {
	@Shadow(remap = false)
	private int xOffset, yOffset;
	@Shadow(remap = false)
	@Final
	private static int BIT_MIRROR_X, BIT_MIRROR_Y;
	@Shadow(remap = false)
	public int[] pixels;

	@Unique
	@Override
	public void render(int xp, int yp, int tile, int bits, SpriteSheet sheet, int whiteTint, boolean fullbright, GraphicComp.SpriteRenderer.Rotation rotation) {
		// xp and yp are originally in level coordinates, but offset turns them to screen coordinates.
		xp -= xOffset; //account for screen offset
		yp -= yOffset;

		// Determines if the image should be mirrored...
		boolean mirrorX = (bits & BIT_MIRROR_X) > 0; // Horizontally.
		boolean mirrorY = (bits & BIT_MIRROR_Y) > 0; // Vertically.

		int xTile = tile % 32; // Gets x position of the spritesheet "tile"
		int yTile = tile / 32; // Gets y position
		int toffs = xTile * 8 + yTile * 8 * sheet.width; // Gets the offset of the sprite into the spritesheet pixel array, the 8's represent the size of the box. (8 by 8 pixel sprite boxes)

		// THIS LOOPS FOR EVERY PIXEL
		for (int y = 0; y < 8; y++) { // Loops 8 times (because of the height of the tile)
			if (y + yp < 0 || y + yp >= Screen.h) continue; // If the pixel is out of bounds, then skip the rest of the loop.
			for (int x = 0; x < 8; x++) { // Loops 8 times (because of the width of the tile)
				if (x + xp < 0 || x + xp >= Screen.w) continue; // Skip rest if out of bounds.

				int ys = y; // Current y pixel
				if (mirrorY) ys = 7 - y; // Reverses the pixel for a mirroring effect
				int xs = x; // Current x pixel
				if (mirrorX) xs = 7 - x; // Reverses the pixel for a mirroring effect
				{ // Handling rotation
					int t = xs;
					switch (rotation) {
						case CLOCKWISE: xs = ys; ys = 7 - t; break;
						case ANTICLOCKWISE: xs = 7 - ys; ys = t; break;
						case INVERTED: xs = 7 - xs; ys = 7 - ys; break;
					}
				}

				int col = sheet.pixels[toffs + xs + ys * sheet.width]; // Gets the color of the current pixel from the value stored in the sheet.

				boolean isTransparent = (col >> 24 == 0);

				if (!isTransparent) {
					int index = (x + xp) + (y + yp) * Screen.w;

					if (whiteTint != -1 && col == 0x1FFFFFF) {
						// If this is white, write the whiteTint over it
						pixels[index] = ColorMixin.upgrade(whiteTint);
					} else {
						// Inserts the colors into the image
						if (fullbright) {
							pixels[index] = Color.WHITE;
						} else {
							pixels[index] = ColorMixin.upgrade(col);
						}
					}
				}
			}
		}
	}
}
