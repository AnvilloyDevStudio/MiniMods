package io.github.anvilloystudio.minimods.coremods;

import java.awt.BorderLayout;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.core.Mods;
import io.github.anvilloystudio.minimods.coremods.mixins.InitializerMixin;
import io.github.anvilloystudio.minimods.coremods.mixins.LevelMixin;
import io.github.anvilloystudio.minimods.coremods.mixins.PlayerMixin;
import minicraft.core.Game;
import minicraft.core.Initializer;
import minicraft.core.Renderer;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.Particle;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

public class ExternalDebugPanel {
	public volatile static boolean visible = false;
	private static final int width = 1000;
	private static final int height = 420;

	private static JFrame frame = new JFrame("MiniMods External Debug Panel");
	private static Canvas canvas = new Canvas();
	private static Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);

	private static boolean isVisible() { return visible; }
	private static Thread rendering = new Thread(() -> {
		while (true) {
			if (!isVisible()) continue;
			BufferStrategy bs = canvas.getBufferStrategy(); // Creates a buffer strategy to determine how the graphics should be buffered.
			Graphics2D g = (Graphics2D) bs.getDrawGraphics(); // Gets the graphics in which java draws the picture
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Draws a rect to fill the whole window (to cover last?)
			g.setColor(Color.WHITE);
			g.setFont(font);
			FontRenderContext frc = g.getFontRenderContext();

			ArrayList<String> info = new ArrayList<>();

			HashSet<Entity> entities = new HashSet<>();
			Set<Entity> levelEntities;
			for (int i = 0; i < World.levels.length; i++) synchronized (levelEntities = ((LevelMixin) World.levels[i]).getEntities()) {
				for (Entity e : levelEntities) {
					entities.add(e);
				}
			}

			info.add(String.format("%s %s (%s/%s)", Initializer.NAME, Initializer.VERSION, Mods.VERSION, "MiniMods"));
			info.add(InitializerMixin.getFra() + " fps T: " + Game.MAX_FPS);
			info.add(String.format("@ %s ms ticks", CommandWindow.tickNano / 1000000.0));
			info.add("P: " + entities.stream().filter(e -> e instanceof Particle).count() + " T: " + entities.size());
			info.add(Level.getLevelName(Game.levels[Game.currentLevel].depth));

			Function<Direction, String> getDir = dir -> {
				switch (dir) {
					case DOWN:
						return "South";
					case LEFT:
						return "West";
					case RIGHT:
						return "East";
					case UP:
						return "North";
					case NONE:
						return "N/A";
				}
				return "N/A";
			};
			Function<Direction, String> getTowards = dir -> {
				switch (dir) {
					case DOWN:
						return "Towards positive Y";
					case LEFT:
						return "Towards negative X";
					case RIGHT:
						return "Towards positive X";
					case UP:
						return "Towards negative Y";
					case NONE:
						return "N/A";
				}
				return "N/A";
			};

			info.add("X/Y: " + (Game.player.x / 16) + "-" + (Game.player.x % 16) + " / " + (Game.player.y / 16) + "-" + (Game.player.y % 16));
			info.add("Block: " + (Game.player.x >> 4) + " " + (Game.player.y >> 4));
			info.add("Facing: " + Game.player.dir + " (" + getDir.apply(Game.player.dir) + ") (" + getTowards.apply(Game.player.dir) + ")");

			int count = 0;
			for (String text : info) {
				LineMetrics bounds = font.getLineMetrics(text, frc);
				g.drawString(text, 0, bounds.getHeight() * count + bounds.getAscent());
				count++;
			}

			ArrayList<String> infoRight = new ArrayList<>();

			long allocatedMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

			infoRight.add("Java: " + System.getProperty("java.version") + " " + System.getProperty("sun.arch.data.model") + "bit");
			infoRight.add("Mem: " + ((allocatedMemory / 1048576) * 100 / (Runtime.getRuntime().maxMemory() / 1048576)) + "% " +
				(allocatedMemory / 1048576) + "/" + (Runtime.getRuntime().maxMemory() / 1048576) + "MB");
			infoRight.add("Allocated: " + ((allocatedMemory / 1048576) * 100 / (Runtime.getRuntime().totalMemory() / 1048576)) + "% " +
				(Runtime.getRuntime().totalMemory() / 1048576) + "MB");

			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			CentralProcessor cpu = hal.getProcessor();
			String name = cpu.getProcessorIdentifier().getName();

			infoRight.add("CPU: " + cpu.getPhysicalProcessorCount() + "x " + name);

			infoRight.add(String.format("Display: %sx%s", Renderer.WIDTH, Renderer.HEIGHT));
			List<GraphicsCard> gpus = hal.getGraphicsCards();
			for (GraphicsCard gpu : gpus) {
				infoRight.add(gpu.getVendor() + " " + gpu.getName());
			}

			try {
				int x = Game.player.x, y = Game.player.y - 2;

				x += Game.player.dir.getX()*Player.INTERACT_DIST;
				y += Game.player.dir.getY()*Player.INTERACT_DIST;

				Tile tile = Game.levels[Game.currentLevel].getTile(x >> 4, y >> 4);
				int tileData = Game.levels[Game.currentLevel].getData(x >> 4, y >> 4);

				infoRight.add("Targeted Tile: " + tile + " data: " + tileData);
			} catch (NullPointerException e) {}

			List<Entity> targetEntities = Game.player.getLevel().getEntitiesInRect(((PlayerMixin) Game.player).invokeGetInteractionBox(Player.INTERACT_DIST));
			for (Entity e : targetEntities) {
				infoRight.add("Targeted Entity: " + e);
			}

			int countR = 0;
			for (String text : infoRight) {
				LineMetrics bounds = font.getLineMetrics(text, frc);
				g.drawString(text, (float) (width - font.getStringBounds(text, frc).getWidth()), bounds.getHeight() * countR + bounds.getAscent());
				countR++;
			}

			// Release any system items that are using this method. (so we don't have crappy framerates)
			g.dispose();

			// Make the picture visible.
			bs.show();
		}
	}, "External Debug Panel Display Renderer");

	public static void init() {
		canvas.setSize(width, height);
		canvas.setBackground(Color.BLACK);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();

		try {
			BufferedImage logo = ImageIO.read(ExternalDebugPanel.class.getResourceAsStream("/resources/logo.png")); // Load the window logo
			frame.setIconImage(logo);
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		canvas.createBufferStrategy(3);
		rendering.start();

		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				ExternalDebugPanel.tick();
			}
		});
	}

	public static void tick() {
		if (Renderer.showinfo ^ visible) setVisible(Renderer.showinfo);
	}

	public static void setVisible(boolean v) {
		visible = v;
		frame.setVisible(visible);
	}
}
