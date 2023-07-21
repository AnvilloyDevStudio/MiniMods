package io.github.anvilloystudio.minimods.coremods;

import io.github.anvilloystudio.minimods.coremods.interfaces.ScreenRenderer;
import io.github.anvilloystudio.minimods.coremods.mixins.SpriteMixin;
import io.github.anvilloystudio.minimods.coremods.mixins.SpritePxMixin;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.gfx.SpriteSheet;
import org.jetbrains.annotations.Range;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GraphicComp {
	public static class ModSprite extends Sprite {
		/**
			This class needs to store a list of similar segments that make up a sprite, just once for everything. There's usually four groups, but the components are:
				-spritesheet location (x, y)
				-mirror type

			That's it!
			The screen's render method only draws one 8x8 pixel of the spritesheet at a time, so the "sprite size" will be determined by how many repetitions of the above group there are.
		*/

		public ModSprite(int pos, SpriteSheet sheet) {
			this(pos%32, pos/32, 1, 1, sheet);
		}

		public final ModPx[][] modSpritePixels;

		/**
		 * 	Creates a reference to an 8x8 sprite in a spritesheet. Specify the position and sheet of the sprite to create.
		 * @param sx X position of the sprite in spritesheet coordinates.
		 * @param sy Y position of the sprite in spritesheet coordinates.
		 * @param sheet What spritesheet to use.
		 */
		public ModSprite(int sx, int sy, SpriteSheet sheet) {
			this(sx, sy, 1, 1, sheet);
		}
		public ModSprite(int sx, int sy, int sw, int sh, SpriteSheet sheet) {
			this(sx, sy, sw, sh, sheet, 0);
		}

		public ModSprite(int sx, int sy, int sw, int sh, SpriteSheet sheet, int mirror) {
			this(sx, sy, sw, sh, sheet, mirror, false);
		}
		public ModSprite(int sx, int sy, int sw, int sh, SpriteSheet sheet, int mirror, boolean onepixel) {
			super(new Px[0][0]);
			sheetLoc = new Rectangle(sx, sy, sw, sh);

			modSpritePixels = new ModPx[sh][sw];
			for (int r = 0; r < sh; r++)
				for (int c = 0; c < sw; c++)
					modSpritePixels[r][c] = new ModPx(sx + (onepixel ? 0 : c), sy + (onepixel ? 0 : r), mirror, sheet);

			spritePixels = modSpritePixels;
		}
		public ModSprite(int sx, int sy, int sw, int sh, SpriteSheet sheet, boolean onepixel, int[][] mirrors) {
			super(new Px[0][0]);
			sheetLoc = new Rectangle(sx, sy, sw, sh);

			modSpritePixels = new ModPx[sh][sw];
			for (int r = 0; r < sh; r++)
				for (int c = 0; c < sw; c++)
					modSpritePixels[r][c] = new ModPx(sx + (onepixel? 0 : c), sy + (onepixel ? 0 : r), mirrors[r][c], sheet);

			spritePixels = modSpritePixels;
		}

		public ModSprite(ModPx[][] pixels) {
			super(pixels);
			modSpritePixels = pixels;
		}

		@Override
		public void renderRow(int r, Screen screen, int x, int y) {
			ModPx[] row = modSpritePixels[r];
			for (int c = 0; c < row.length; c++) { // Loop across through each column
				screen.render(x + c * 8, y, ((SpritePxMixin) row[c]).getSpritePos(), ((SpritePxMixin) row[c]).getMirror(), row[c].spriteSheet, this.color, false); // Render the sprite pixel.
			}
		}
		@Override
		public void renderRow(int r, Screen screen, int x, int y, int mirror) {
			ModPx[] row = modSpritePixels[r];
			for (int c = 0; c < row.length; c++) { // Loop across through each column
				screen.render(x + c * 8, y, ((SpritePxMixin) row[c]).getSpritePos(), mirror, row[c].spriteSheet, this.color, false); // Render the sprite pixel.
			}
		}
		@Override
		public void renderRow(int r, Screen screen, int x, int y, int mirror, int whiteTint) {
			ModPx[] row = modSpritePixels[r];
			for (int c = 0; c < row.length; c++) {
				screen.render(x + c * 8, y, ((SpritePxMixin) row[c]).getSpritePos(), (mirror != -1 ? mirror : ((SpritePxMixin) row[c]).getMirror()), row[c].spriteSheet, whiteTint, false);
			}
		}

		// @Override TODO 2.1.0
		// public void renderRow(int r, Screen screen, int x, int y, int mirror, int whiteTint, int color) {
		// 	ModPx[] row = modSpritePixels[r];
		// 	for (int c = 0; c < row.length; c++) {
		// 		screen.render(x + c * 8, y, ((SpritePxMixin) row[c]).getSpritePos(), (mirror != -1 ? mirror : ((SpritePxMixin) row[c]).getMirror()), row[c].spriteSheet, whiteTint, false, color);
		// 	}
		// }

		@Override
		protected void renderPixel(int c, int r, Screen screen, int x, int y, int mirror, int whiteTint) {
			screen.render(x, y, ((SpritePxMixin) modSpritePixels[r][c]).getSpritePos(), mirror, modSpritePixels[r][c].spriteSheet, whiteTint, false/*TODO , color */);
		}

		public void renderMirrored(int fullMirror, Screen screen, int x, int y) {
			int h = spritePixels.length;
			int w = spritePixels[0].length;
			boolean mirrorX = (fullMirror&2)>0;
			boolean mirrorY = (fullMirror&2)>0;
			for (int row = 0; row < h; row++) { // Loop down through each row
				int my = mirrorX? h-1-row: row;
				for (int col = 0; col<w; col++) {
					int mx = mirrorY? w-1-col: col;
					renderPixel(mx, my, screen, x+8*col, y+8*row);
				}
			}
		}
		public void renderMirrored(int fullMirror, Screen screen, int x, int y, int mirror) {
			int h = spritePixels.length;
			int w = spritePixels[0].length;
			boolean mirrorX = (fullMirror&1)>0;
			boolean mirrorY = (fullMirror&2)>0;
			for (int row = 0; row < h; row++) { // Loop down through each row
				int my = mirrorX? h-1-row: row;
				for (int col = 0; col<w; col++) {
					int mx = mirrorY? w-1-col: col;
					renderPixel(mx, my, screen, x+8*col, y+8*row, mirror);
				}
			}
		}
		public void renderMirrored(int fullMirror, Screen screen, int x, int y, int mirror, int whiteTint) {
			int h = spritePixels.length;
			int w = spritePixels[0].length;
			boolean mirrorX = (fullMirror&1)>0;
			boolean mirrorY = (fullMirror&2)>0;
			for (int row = 0; row < h; row++) { // Loop down through each row
				int my = mirrorX? h-1-row: row;
				for (int col = 0; col<w; col++) {
					int mx = mirrorY? w-1-col: col;
					renderPixel(mx, my, screen, x+8*col, y+8*row, mirror, whiteTint);
				}
			}
		}

		public static class ModPx extends Px {
			public final SpriteSheet spriteSheet;

			public ModPx(int sheetX, int sheetY, int mirroring, SpriteSheet sheet) {
				super(sheetX, sheetY, mirroring, 0);
				// pixelX and pixelY are the relative positions each pixel should have relative to the top-left-most pixel of the sprite.
				this.spriteSheet = sheet;
			}
		}
	}

	public static class SpriteRenderer {
		public enum Rotation {
			NONE, CLOCKWISE, ANTICLOCKWISE, INVERTED;

			public static final Rotation[] VALUES = values();

			/**
			 * Getting the actual rotation based on {@link minicraft.entity.Direction Direction} with {@code DOWN} as 0.
			 * @param dir should be in range 0 to 3 from constant ordinal values of {@code Direction}.
			 * @return the corresponding rotation
			 */
			public static Rotation getFromDirectionDown(@Range(from = 0, to = 3) int dir) {
				switch (dir) {
					case 0: default: return NONE; // DOWN
					case 1: return INVERTED; // UP
					case 2: return CLOCKWISE; // LEFT
					case 3: return ANTICLOCKWISE; // RIGHT
				}
			}
		}

		private static SpriteSheet getSpriteSheet(Sprite.Px px) {
			return px instanceof ModSprite.ModPx ? ((ModSprite.ModPx) px).spriteSheet : ((SpritePxMixin) px).getSpriteSheet();
		}

		public static void render(Screen screen, int x, int y, Sprite sprite, Rotation rotation) { render(screen, x, y, sprite, 0, rotation); }
		public static void render(Screen screen, int x, int y, Sprite sprite, int mirror, Rotation rotation) { render(screen, x, y, sprite, mirror, -1, rotation); }
		public static void render(Screen screen, int x, int y, Sprite sprite, int mirror, int whiteTint, Rotation rotation) {
			Sprite.Px[][] pixels = ((SpriteMixin) sprite).getSpritePixels();
			boolean mirrorX = (mirror & 1) > 0;
			boolean mirrorY = (mirror & 2) > 0;
			for (int r = 0; r < pixels.length; ++r) {
				for (int c = 0; c < pixels[r].length; ++c) {
					int rs = r, cs = c;
					if (mirrorX) rs = pixels.length - 1 - rs;
					if (mirrorY) cs = pixels[r].length - 1 - cs;
					int t = rs;
					if (pixels.length == pixels[r].length) switch (rotation) {
						case CLOCKWISE: rs = pixels.length - 1 - cs; cs = t; break;
						case ANTICLOCKWISE: rs = cs; cs = pixels.length - 1 - t; break;
						case INVERTED: rs = pixels.length - 1 - rs; cs = pixels.length - 1 - cs; break;
					}

					renderPixel(screen, x + c * 8, y + r * 8, pixels[rs][cs], mirror, whiteTint, rotation);
				}
			}
		}

		public static void renderPixel(Screen screen, int x, int y, Sprite.Px px, Rotation rotation) { renderPixel(screen, x, y, px, 0, rotation); }
		public static void renderPixel(Screen screen, int x, int y, Sprite.Px px, int mirror, Rotation rotation) { renderPixel(screen, x, y, px, mirror, -1, rotation); }
		public static void renderPixel(Screen screen, int x, int y, Sprite.Px px, int mirror, int whiteTint, Rotation rotation) {
			((ScreenRenderer) screen).render(x, y, ((SpritePxMixin) px).getSpritePos(), mirror, getSpriteSheet(px), whiteTint, false, rotation/*TODO , color */);
		}
	}

	public static Point renderPixelRotatedClockwise(int xs, int ys) {
		return new Point(7 - ys, xs);
	}
	public static Point renderPixelRotatedAnticlockwise(int xs, int ys) {
		return new Point(ys, 7 - xs);
	}
	public static Point renderPixelRotatedUpsideDown(int xs, int ys) {
		return new Point(7 - xs, 7 - ys);
	}

	/**
	 * Forming a <code>SpriteSheet</code> from an {@code InputStream}.
	 * @param is The {@code InputStream} of the image.
	 * @return The {@code SpriteSheet}.
	 * @throws IOException if an error occurs during reading or when not able to create required ImageInputStream.
	 */
	public static SpriteSheet getSpriteSheetFromInputStream(InputStream is) throws IOException {
		return new SpriteSheet(ImageIO.read(is));
	}

	/**
	 * Rotating the image by the specified degrees.
	 * Reference: https://stackoverflow.com/a/73588987.
	 * @param source The source of {@link BufferedImage}.
	 * @param degrees The degrees to rotate.
	 * @return The rotated {@link BufferedImage}.
	 */
	public static BufferedImage rotateBy(BufferedImage source, double degrees) {
		// The size of the original image
		int w = source.getWidth();
		int h = source.getHeight();
		// The angel of the rotation in radians
		double rads = Math.toRadians(degrees);
		// Some nice math which demonstrates I have no idea what I'm talking about
		// Okay, this calculates the amount of space the image will need in
		// order not be clipped when it's rotated
		double sin = Math.abs(Math.sin(rads));
		double cos = Math.abs(Math.cos(rads));
		int newWidth = (int) Math.floor(w * cos + h * sin);
		int newHeight = (int) Math.floor(h * cos + w * sin);

		// A new image, into which the original can be painted
		BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotated.createGraphics();
		// The transformation which will be used to actually rotate the image
		// The translation, actually makes sure that the image is positioned onto
		// the viewable area of the image
		AffineTransform at = new AffineTransform();
		at.translate((newWidth - w) / 2.0, (newHeight - h) / 2.0);

		// And we rotate about the center of the image...
		int x = w / 2;
		int y = h / 2;
		at.rotate(rads, x, y);
		g2d.setTransform(at);
		// And we paint the original image onto the new image
		g2d.drawImage(source, 0, 0, null);
		g2d.dispose();

		return rotated;
	}

	/**
	 * Rotating the {@link BufferedImage} clockwise by 90 degrees.
	 * @param src The source of {@link BufferedImage}.
	 * @return The rotated {@link BufferedImage}.
	 */
	public static BufferedImage rotateClockwise90(BufferedImage src) {
		return rotateBy(src, 90);
	}

	/**
	 * Rotating the {@link BufferedImage} anti-clockwise by 90 degrees.
	 * @param src The source of {@link BufferedImage}.
	 * @return The rotated {@link BufferedImage}.
	 */
	public static BufferedImage rotateAnticlockwise90(BufferedImage src) {
		return rotateBy(src, -90);
	}

	/**
	 * Rotating the {@link BufferedImage} by 180 degrees. (Inverted)
	 * @param src The source of {@link BufferedImage}.
	 * @return The rotated {@link BufferedImage}.
	 */
	public static BufferedImage rotate180(BufferedImage src) {
		return rotateBy(src, 180);
	}

	/**
	 * Getting a {@code Sprite} from the {@code SpriteSheet}. With no mirroring and default initial position <i>(0, 0)</i>.
	 * @param w The width.
	 * @param h The height.
	 * @param sheet The {@code SpriteSheet}.
	 * @return The sprite.
	 */
	public static Sprite getSpriteFromSheet(int w, int h, SpriteSheet sheet) { return getSpriteFromSheet(0, 0, w, h, sheet); }
	/**
	 * Getting a {@code Sprite} from the {@code SpriteSheet}. With no mirroring.
	 * @param x The initial x position.
	 * @param y The initial y position.
	 * @param w The width.
	 * @param h The height.
	 * @param sheet The {@code SpriteSheet}.
	 * @return The sprite.
	 */
	public static Sprite getSpriteFromSheet(int x, int y, int w, int h, SpriteSheet sheet) { return getSpriteFromSheet(x, y, w, h, sheet, 0); }
	/**
	 * Getting a {@code Sprite} from the {@code SpriteSheet}.
	 * @param x The initial x position.
	 * @param y The initial y position.
	 * @param w The width.
	 * @param h The height.
	 * @param sheet The {@code SpriteSheet}.
	 * @param mirror The mirroring of the {@code Px}s.
	 * @return The sprite.
	 */
	public static Sprite getSpriteFromSheet(int x, int y, int w, int h, SpriteSheet sheet, int mirror) {
		Sprite.Px[][] pixels = new Sprite.Px[h][w];
		for (int r = 0; r < h; r++) {
			for (int c = 0; c < w; c++) {
				pixels[r][c] = new Sprite.Px(x+c, y+r, mirror, sheet);
			}
		}

		return new Sprite(pixels);
	}
}
