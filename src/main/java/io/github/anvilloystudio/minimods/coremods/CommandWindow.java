package io.github.anvilloystudio.minimods.coremods;

import javax.annotation.Nullable;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jetbrains.annotations.NotNull;

import io.github.anvilloystudio.minimods.api.ConsoleColors;
import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.Vector2;
import io.github.anvilloystudio.minimods.coremods.mixins.InitializerMixin;
import io.github.anvilloystudio.minimods.coremods.mixins.LevelMixin;
import minicraft.core.Action;
import minicraft.core.Game;
import minicraft.core.Initializer;
import minicraft.core.Renderer;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.Settings;
import minicraft.entity.Arrow;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.Spark;
import minicraft.entity.furniture.Furniture;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.PassiveMob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.Particle;
import minicraft.saveload.Save;
import minicraft.screen.WorldSelectDisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class CommandWindow {
	private static JFrame frame;
	private static JTextField input;

	/** The default number of columns. */
	private static int defaultWidth = 15;

	private static final HashMap<String, String> validators = new HashMap<>();

	static {
		// Basic Settings.
		frame = new JFrame("Command Window");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setAutoRequestFocus(true);

		// Command input.
		input = new JTextField();
		input.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setSelectionColor(Color.WHITE);
		input.setSelectedTextColor(Color.BLACK);
		input.setColumns(defaultWidth);
		input.getCaret().setBlinkRate(0);
		input.setCaretColor(Color.WHITE);
		input.getDocument().addDocumentListener(new DocumentListener() { // Listen for changes in the text
			public void changedUpdate(DocumentEvent e) {
				updateField();
			}
			public void removeUpdate(DocumentEvent e) {
				updateField();
			}
			public void insertUpdate(DocumentEvent e) {
				updateField();
			}

			public void updateField() {
				input.setColumns(Math.max(input.getText().length(), defaultWidth));
				input.validate();
				frame.pack();
				frame.setLocationRelativeTo(null);
			}
		});
		input.addActionListener(e -> { // When enter pressed.
			String text = input.getText();
			if (!text.isEmpty() && text.charAt(0) == '/')
				invokeCommand(text);
			input.setText("");
		});

		// Finalize Frame.
		frame.add(input, BorderLayout.CENTER);
		frame.setSize(input.getSize());
		frame.pack();
		frame.setLocationRelativeTo(null);

		// Adding validators.
		validators.put("Integer", "[0-9]+");
		validators.put("Decimal", "[0-9]+(?:\\.[0-9]+)?");
	}

	public static void init() {
		ModProcedure.tickables2.add(input -> {
			if (input.getKey("F4").clicked) {
				toFront();
			}
		});

		if (Game.debug) {
			new Thread(() -> {
				while (InitializerMixin.getFrame() == null || !InitializerMixin.getFrame().isVisible()) {}
				frame.setVisible(true);
				frame.toBack();
			}, "Command Window Starter").start();
		}
	}

	public static void toFront() {
		frame.setState(Frame.NORMAL);
		frame.toFront();
	}

	public static long tickNano;
	public static long tickStart;
	public static Action measureCallback = null;

	public static enum Command {
		HELP("help", new String[] {"?"}, "Getting help with these commands.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				System.out.println("The list of available commands:");
				for (Command cmd : values()) {
					System.out.println("\t" + cmd.syntax + "\t" + cmd.desc);
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) {
				if (indices.length > 2) return new Vector2<>(indices[2], indices[indices.length - 1]);
				return null;
			}
		},
		KILL("kill [<target>]", new String[0], "Killing the specified entities.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (args.length == 1) {
					if (Renderer.readyToRenderGameplay)
						Game.player.die();
					else
						System.out.println("Unable to kill player: Not in world.");
				} else if (args.length > 2) {
					sendSyntaxError(oriCmd, new Vector2<>(indices[4], indices[indices.length - 1]), "Too many arguments.");
				} else {
					try {
						Set<Entity> entities = targetSelector(args[1], Game.player);
						for (Entity e : entities) {
							if (e == Game.player && !Renderer.readyToRenderGameplay)
								System.out.println("Unable to kill player: Not in world.");
							else
								e.die();
						}
					} catch (CommandSyntaxError e1) {
						sendSyntaxError(oriCmd, new Vector2<>(indices[2] + e1.index0, indices[3] + e1.index1), e1.msg);
					} catch (InvalidCommandArgumentError e1) {
						sendInvalidArgumentError(e1);
					}
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		TIME("time ((add|set) <time>|set (day|night|noon|midnight)|query (daytime|gametime|day))", new String[0], "Setting the time to the specified.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (args.length == 1) {
					sendInvalidArgumentError("action", "Missing value", "Missing necessary argument.");
					return;
				}

				if (args[1].equals("add")) {
					if (args.length == 2) {
						sendInvalidArgumentError("time", "Missing value", "Missing necessary argument.");
						return;
					} else if (args.length > 2) {
						sendSyntaxError(oriCmd, new Vector2<>(indices[4], indices[indices.length - 1]), "Too many arguments.");
						return;
					}

					try {
						Updater.setTime(Updater.tickCount + Integer.parseInt(args[2]));
					} catch (NumberFormatException e) {
						sendInvalidArgumentError(new InvalidCommandArgumentError("time", args[2], "Unparsable value.", e));
					}
				} else if (args[1].equals("set")) {
					if (args.length == 2) {
						sendInvalidArgumentError("time", "Missing value", "Missing necessary argument.");
						return;
					} else if (args.length > 2) {
						sendSyntaxError(oriCmd, new Vector2<>(indices[4], indices[indices.length - 1]), "Too many arguments.");
						return;
					}

					switch (args[2]) {
						case "day":
							Updater.changeTimeOfDay(Updater.Time.Morning);
							return;
						case "night":
							Updater.changeTimeOfDay(Updater.Time.Evening);
							return;
						case "noon":
							Updater.changeTimeOfDay(Updater.Time.Day);
							return;
						case "midnight":
							Updater.changeTimeOfDay(Updater.Time.Night);
							return;
					}

					try {
						Updater.setTime(Integer.parseInt(args[2]));
					} catch (NumberFormatException e) {
						sendInvalidArgumentError(new InvalidCommandArgumentError("time", args[2], "Unparsable value.", e));
					}
				} else if (args[1].equals("query")) {
					if (args.length == 2) {
						sendInvalidArgumentError("type", "Missing value", "Missing necessary argument.");
						return;
					}

					switch (args[2]) {
						case "daytime":
							System.out.println(Updater.tickCount);
							break;
						case "gametime":
							System.out.println(Updater.gameTime);
							break;
						case "day":
							System.out.println((int) Math.ceil(Updater.gameTime / (double) Updater.dayLength));
							break;
						default:
							sendInvalidArgumentError("type", args[2], "Invalid argument.");
					}
				} else {
					sendInvalidArgumentError("action", args[1], "Invalid argument.");
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		GAMEMODE("gamemode <gamemode>", new String[0], "Setting the gamemode.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (args.length == 1) {
					sendInvalidArgumentError("gamemode", "Missing value", "Missing necessary argument.");
					return;
				}

				switch (args[1]) {
					case "0":
					case "s":
					case "survival":
						Settings.set("mode", "survival");
						break;
					case "1":
					case "c":
					case "creative":
						Settings.set("mode", "creative");
						break;
					case "hardcore":
					case "score":
						sendInvalidArgumentError("gamemode", args[1], "Gamemode not supported.");
						break;
					default:
						sendInvalidArgumentError("gamemode", args[1], "Invalid argument.");
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		SAVE("save", new String[0], "Saving the world.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (Renderer.readyToRenderGameplay) {
					System.out.println("Starting world save.");
					new Save(WorldSelectDisplay.getWorldName());
					System.out.println("World saved.");
				} else {
					System.out.println("Unable to save: Not in world.");
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		STATUS("status", new String[] {"stats"}, "Checking the current world status.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				System.out.println("Running " + Game.NAME + ' ' + Game.VERSION + (Game.debug ? " (debug mode)" : ""));
				System.out.println("Fps: " + Initializer.getCurFps());
				System.out.println("Debug: " + (Game.debug ? "On" : "Off"));
				System.out.println("Mode: " + Settings.get("mode"));
				System.out.println("World Size: " + World.lvlw + "x" + World.lvlh);
				System.out.println("Current level: " + Game.currentLevel);
				System.out.println("Players connected: " + Game.server.getNumPlayers());

				for (String info: Game.server.getClientInfo())
					 System.out.println("\t"+info);
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		TPS("tps", new String[0], "Measuring the current world tps.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				measureCallback = () -> {
					System.out.println("Current world tps:");
					System.out.println(DateFormat.getDateInstance(DateFormat.FULL).format(new Date(TimeUnit.NANOSECONDS.toMillis(tickStart))));
					System.out.println("Tick time: @ " + (tickNano / 1000000.0) + " ms ticks [" + InitializerMixin.getTik() + " tps]");
					System.out.println("TPS: " + Updater.normSpeed * Updater.gamespeed);
					System.out.println("Day time: " + Updater.tickCount + " (" + Updater.getTime() + ")");
					System.out.println("Game time: " + Updater.gameTime);
				};
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		SETTILE("settile <x> <y> <tile>", new String[0], "Setting the tile to the specified tile.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (args.length == 1) { // Checking length > 1.
					sendInvalidArgumentError("x", "Missing value", "Missing necessary argument.");
					return;
				}

				int x; // Parsing first argument.
				if (args[1].charAt(0) == '~') {
					x = Game.player.x;
					if (args[1].length() > 1) try {
						x += Integer.parseInt(args[1].substring(1));
					} catch (NumberFormatException e) {
						sendInvalidArgumentError(new InvalidCommandArgumentError("x", args[1].substring(1), "Unparsable value.", e));
						return;
					}
				} else try {
					x = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sendInvalidArgumentError(new InvalidCommandArgumentError("x", args[1], "Unparsable value.", e));
					return;
				}

				if (args.length == 2) { // Checking length > 2
					sendInvalidArgumentError("y", "Missing value", "Missing necessary argument.");
					return;
				}

				int y; // Parsing second argument.
				if (args[2].charAt(0) == '~') {
					y = Game.player.y;
					if (args[2].length() > 1) try {
						y += Integer.parseInt(args[2].substring(1));
					} catch (NumberFormatException e) {
						sendInvalidArgumentError(new InvalidCommandArgumentError("y", args[2].substring(1), "Unparsable value.", e));
						return;
					}
				} else try {
					y = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					sendInvalidArgumentError(new InvalidCommandArgumentError("y", args[2], "Unparsable value.", e));
					return;
				}

				if (args.length == 3) { // Checking length > 3.
					sendInvalidArgumentError("tile", "Missing value", "Missing necessary argument.");
					return;
				}

				try {
					Game.levels[Game.currentLevel].setTile(x, y, args[3]);
				} catch (NullPointerException e) {
					sendInvalidArgumentError("tile", args[3], "Invalid tile.");
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		SETDATA("setdata <x> <y> <data>", new String[0], "Setting the specific tile data.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (args.length == 1) { // Checking length > 1.
					sendInvalidArgumentError("x", "Missing value", "Missing necessary argument.");
					return;
				}

				int x; // Parsing first argument.
				if (args[1].charAt(0) == '~') {
					x = Game.player.x;
					if (args[1].length() > 1) try {
						x += Integer.parseInt(args[1].substring(1));
					} catch (NumberFormatException e) {
						sendInvalidArgumentError(new InvalidCommandArgumentError("x", args[1].substring(1), "Unparsable value.", e));
						return;
					}
				} else try {
					x = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sendInvalidArgumentError(new InvalidCommandArgumentError("x", args[1], "Unparsable value.", e));
					return;
				}

				if (args.length == 2) { // Checking length > 2
					sendInvalidArgumentError("y", "Missing value", "Missing necessary argument.");
					return;
				}

				int y; // Parsing second argument.
				if (args[2].charAt(0) == '~') {
					y = Game.player.y;
					if (args[2].length() > 1) try {
						y += Integer.parseInt(args[2].substring(1));
					} catch (NumberFormatException e) {
						sendInvalidArgumentError(new InvalidCommandArgumentError("y", args[2].substring(1), "Unparsable value.", e));
						return;
					}
				} else try {
					y = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					sendInvalidArgumentError(new InvalidCommandArgumentError("y", args[2], "Unparsable value.", e));
					return;
				}

				if (args.length == 3) { // Checking length > 3.
					sendInvalidArgumentError("data", "Missing value", "Missing necessary argument.");
					return;
				}

				try {
					Game.levels[Game.currentLevel].setData(x, y, Integer.parseInt(args[3]));
				} catch (NumberFormatException e) {
					sendInvalidArgumentError(new InvalidCommandArgumentError("data", args[3], "Unparsable value.", e));
				}
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		SEED("seed", new String[0], "Getting the world seed.") {
			@Override
			public void execute(String oriCmd, String[] args, Integer[] indices) {
				if (Renderer.readyToRenderGameplay)
					System.out.println("Level seed: " + Game.levels[Game.currentLevel].getSeed());
				else
					System.out.println("Unable to get seed: Not in world.");
			}

			@Override
			public String[] getMapping(int index) { return new String[0]; }

			@Override
			public Vector2<Integer> validate(String[] args, Integer[] indices) { return null; }
		},
		;

		public final String syntax;
		public final String desc;
		private final String[] aliases;
		/** Used for {@link #getMapping(int)}. */
		@SuppressWarnings("unused")
		private final String[] arguments;

		Command(String syntax, @NotNull String[] aliases, String desc) {
			this.syntax = syntax;
			this.desc = desc;
			this.aliases = aliases;
			ArrayList<String> out = new ArrayList<>();
			for (String str0 : splitSyntax(syntax)) {
				String str1 = str0.length() > 2 && (str0.charAt(0) == '<' || str0.charAt(0) == '[' || str0.charAt(0) == '(') ?
					str0.substring(1, str0.length() - 1) : str0;
				ArrayList<String> temp = splitSyntax(str1);
				if (temp.size() == 1) temp.clear(); // The string is literal or 2 chars only.
				// Removes if they are literal.
				temp.removeIf(s -> s.charAt(0) != '<' && s.charAt(0) != '[' && s.charAt(0) != '(');
				out.add(str0);
				out.addAll(temp);
			}

			arguments = out.toArray(new String[0]);
		}

		private static ArrayList<String> splitSyntax(String input) {
			ArrayList<String> out = new ArrayList<>();
			int lastIdx = 0;
			// 0 <>; 1 []; 2 ()
			ArrayList<Integer> bracketCounter = new ArrayList<>();
			char openBracket0 = '<';
			char openBracket1 = '[';
			char openBracket2 = '(';
			char closeBracket0 = '>';
			char closeBracket1 = ']';
			char closeBracket2 = ')';
			char spaceChar = ' ';
			Supplier<Boolean> level0Checker = () -> {
				if (bracketCounter.isEmpty()) return false;
				return bracketCounter.get(bracketCounter.size() - 1) == 0;
			};

			Predicate<Integer> checkDiff = ch -> {
				if (bracketCounter.isEmpty()) return true;
				return bracketCounter.get(bracketCounter.size() - 1) != ch;
			};

			for (int i = 0; i < input.length(); i++) {
				char ch = input.charAt(i);
				if (ch == spaceChar && bracketCounter.isEmpty()) {
					String str = input.substring(lastIdx, i).trim();
					lastIdx = i;
					if (!str.isEmpty()) out.add(str);
				} else if (ch == openBracket0) {
					if (level0Checker.get()) throw new RuntimeException(String.format("Invalid char %s index %s in <> level. Input: \"%s\"", ch, i, input));
					bracketCounter.add(0);
				} else if (ch == closeBracket0) {
					if (checkDiff.test(0)) throw new RuntimeException(String.format("Invalid closing char %s index %s. Input: \"%s\"", ch, i, input));
					bracketCounter.remove(bracketCounter.size() - 1);
				} else if (ch == openBracket1) {
					if (level0Checker.get()) throw new RuntimeException(String.format("Invalid char %s index %s in <> level. Input: \"%s\"", ch, i, input));
					bracketCounter.add(1);
				} else if (ch == closeBracket1) {
					if (checkDiff.test(1)) throw new RuntimeException(String.format("Invalid closing char %s index %s. Input: \"%s\"", ch, i, input));
					bracketCounter.remove(bracketCounter.size() - 1);
				} else if (ch == openBracket2) {
					if (level0Checker.get()) throw new RuntimeException(String.format("Invalid char %s index %s in <> level. Input: \"%s\"", ch, i, input));
					bracketCounter.add(2);
				} else if (ch == closeBracket2) {
					if (checkDiff.test(2)) throw new RuntimeException(String.format("Invalid closing char %s index %s. Input: \"%s\"", ch, i, input));
					bracketCounter.remove(bracketCounter.size() - 1);
				}
			}

			String str = input.substring(lastIdx, input.length()).trim();
			if (!str.isEmpty()) {
				out.add(str);
			}

			if (bracketCounter.size() > 0)
				throw new RuntimeException(String.format("Bracket not closed. Input: \"%s\"", input));

			return out;
		}

		/**
		 * Executing the command with the provided <i>args</i>.
		 * @param args The whole command.
		 * @param indices The argument indices.
		 */
		public abstract void execute(String oriCmd, String[] args, Integer[] indices);
		/**
		 * Unused since multi-colored in text field is not possible.
		 * Validating the command with the provided <i>args</i>.
		 * @param args The whole command.
		 * @param indices The argument indices.
		 * @return If the command valid, {@code null} return. Or else invalid with the vector.
		 */
		public abstract Vector2<Integer> validate(String[] args, Integer[] indices);
		/**
		 * Unused since tab word not implemented.
		 * Getting the valid arguments with the corresponding argument.
		 * @param index The index of {@code arguments}.
		 * @return The valid arguments. Must include the argument itself.
		 * @see {@link #arguments}
		 */
		public abstract String[] getMapping(int index);

		public boolean isAlias(String alias) {
			for (String a : aliases)
				if (alias.equals(a)) return true;
			return false;
		}
	}

	/**
	 * Invoking a command.
	 * @param cmd The command to invoke.
	 */
	public static void invokeCommand(String cmd) {
		if (cmd == null || cmd.isEmpty()) return;
		if (cmd.charAt(0) != '/') return; // Not a command.

        ArrayList<String> out = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>(); // #length() == out#length() * 2.
        int lastIdx = 0;
        boolean inQuote = false;
        char backSlash = '\\';
        char quote = '\"';
        char spaceChar = ' ';
        Predicate<Integer> ifSlashed = i -> {
            if (i >= 0) return cmd.charAt(i - 1) == backSlash;
            return false;
        };

        for (int i = 0; i < cmd.length(); i++) {
            char ch = cmd.charAt(i);
            if (ch == spaceChar && !inQuote) {
                String str = cmd.substring(lastIdx, i).trim();
                if (!str.isEmpty()) {
                    if (str.charAt(0) == quote) str = str.replaceAll("(?<!\\\\)\"", "").replaceAll("\\\\(?=\")", "");
                    out.add(str);
                    indices.add(lastIdx);
                    indices.add(i);
                }

                lastIdx = i;
            } else if (ch == quote) {
                boolean slashed = ifSlashed.test(i);
                if (!inQuote && slashed) {
					sendSyntaxError(cmd, new Vector2<>(i, i + 1), "Invalid char.");
					return;
				} else if (!slashed) inQuote = !inQuote;
            }
        }

        String str = cmd.substring(lastIdx, cmd.length()).trim();
        if (!str.isEmpty()) {
			if (str.charAt(0) == quote) str = str.replaceAll("(?<!\\\\)\"", "").replaceAll("\\\\(?=\")", "");
			out.add(str);
			indices.add(lastIdx);
			indices.add(cmd.length());
		}

        if (inQuote) {
            sendSyntaxError(cmd, new Vector2<>(Pattern.compile("(?<!\\\\)\"").matcher(cmd).end() - 1, cmd.length()), "Quotation not closed.");
			return;
		}

		Command command = null;
		try {
			command = Command.valueOf(out.get(0).substring(1).toUpperCase());
		} catch (IllegalArgumentException e) {
			if (command == null) for (Command cmd0 : Command.values()) {
				if (cmd0.isAlias(out.get(0).substring(1))) {
					command = cmd0;
					break;
				}
			}
		}

		if (command == null) {
			System.out.println(String.format("%sCommand Not Found: %s%s", ConsoleColors.RED, ConsoleColors.RESET, out.get(0).substring(1)));
			return;
		}

		command.execute(cmd, out.toArray(new String[0]), indices.toArray(new Integer[0]));
	}

	/**
	 * Validating the command.
	 * @param cmd The command for validation.
	 * @return If the command valid, {@code null} returned. Or else invalid with the returned vector.
	 */
	public static Vector2<Integer> validate(String cmd) {
		if (cmd == null || cmd.isEmpty()) return null;
		if (cmd.charAt(0) != '/') return null; // Not a command.

        ArrayList<String> out = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>(); // #length() == out#length() * 2.
        int lastIdx = 0;
        boolean inQuote = false;
        char backSlash = '\\';
        char quote = '\"';
        char spaceChar = ' ';
        Predicate<Integer> ifSlashed = i -> {
            if (i >= 0) return cmd.charAt(i - 1) == backSlash;
            return false;
        };

        for (int i = 0; i < cmd.length(); i++) {
            char ch = cmd.charAt(i);
            if (ch == spaceChar && !inQuote) {
                String str = cmd.substring(lastIdx, i).trim();
                if (!str.isEmpty()) {
                    if (str.charAt(0) == quote) str = str.replaceAll("(?<!\\\\)\"", "").replaceAll("\\\\(?=\")", "");
                    out.add(str);
                    indices.add(lastIdx);
                    indices.add(i);
                }

                lastIdx = i;
            } else if (ch == quote) {
                boolean slashed = ifSlashed.test(i);
                if (!inQuote && slashed) {
					return new Vector2<>(i, i + 1);
				} else if (!slashed) inQuote = !inQuote;
            }
        }

        String str = cmd.substring(lastIdx, cmd.length()).trim();
        if (!str.isEmpty()) {
			if (str.charAt(0) == quote) str = str.replaceAll("(?<!\\\\)\"", "").replaceAll("\\\\(?=\")", "");
			out.add(str);
			indices.add(lastIdx);
			indices.add(cmd.length());
		}

        if (inQuote) {
			return new Vector2<>(Pattern.compile("(?<!\\\\)\"").matcher(cmd).end() - 1, cmd.length());
		}

		Command command = null;
		try {
			command = Command.valueOf(out.get(0).substring(1));
		} catch (IllegalArgumentException e) {
			if (command == null) for (Command cmd0 : Command.values()) {
				if (cmd0.isAlias(out.get(0).substring(1))) {
					command = cmd0;
					break;
				}
			}
		}

		return command == null ? new Vector2<>(0, out.get(0).length()) :
			command.validate(out.toArray(new String[0]), indices.toArray(new Integer[0]));
	}

	/**
	 * Using the target selector to select target entities.
	 * @param input The string input. Must starts with <i>@</i>.
	 * @param executor The command executor. There will be another argument for
	 * 	the execution coordination when multiplayer is usable.
	 * @return The targets.
	 * @throws CommandSyntaxError If syntax error occurs.
	 * @throws InvalidCommandArgumentError If argument invalid.
	 */
	private static Set<Entity> targetSelector(String input, Player executor) throws CommandSyntaxError, InvalidCommandArgumentError {
		if (input.charAt(0) != '@') throw new CommandSyntaxError(input, new Vector2<>(0, 1), "Unparsable character.");
		// Only one player.
		if (input.charAt(1) == 'p' || input.charAt(1) == 'r' || input.charAt(1) == 'a' || input.charAt(1) == 's') {
			if (input.length() > 2) throw new CommandSyntaxError(input, new Vector2<>(2, input.length()), "Not more argument accepted.");
			return Collections.singleton(executor);
		} else if (input.charAt(1) == 'e') {
			Predicate<Entity> filter = e -> true;
			if (input.length() > 2) {
				if (input.charAt(2) != '[')
					throw new CommandSyntaxError(input, new Vector2<>(2, 3), "Unparsable character.");
				if (input.length() == 3)
					throw new CommandSyntaxError(input, new Vector2<>(2, 3), "Unparsable argument.");

				HashMap<String, String> map = new HashMap<>();
				int end = -1;
				String key = "";
				String val = "";
				int typ = 0; // 0 for key, 1 for val.
				for (int i = 3; i < input.length(); i++) {
					char ch = input.charAt(i);
					String str = new String(new char[] {ch});
					if (Pattern.matches("\\w|\\.|-|!", str)) {
						if (typ == 0) key += ch;
						else val += ch;
					} else if (ch == '=') {
						if (typ == 0) typ = 1;
						else
							throw new CommandSyntaxError(input, new Vector2<>(i, i + 1), "Invalid character.");
					} else if (ch == ',') {
						if (map.containsKey(key))
							throw new CommandSyntaxError(input, new Vector2<>(i - val.length() - 1 - key.length(), i), "Duplicated key.");
						if (typ == 0)
							throw new CommandSyntaxError(input, new Vector2<>(i, i + 1), "Invalid character.");
						map.put(key, val);
						key = "";
						val = "";
						typ = 0;
					} else if (ch == ']') {
						if (i + 1 > input.length()) // No more argument accepted.
							throw new CommandSyntaxError(input, new Vector2<>(i + 1, input.length()), "Invalid argument.");

						if (input.charAt(i - 1) == ',') // For ",]".
							throw new CommandSyntaxError(input, new Vector2<>(i - 1, i + 1), "Invalid character.");

						// Put the last pair.
						if (map.containsKey(key))
							throw new CommandSyntaxError(input, new Vector2<>(i - val.length() - 1 - key.length(), i), "Duplicated key.");
						if (typ == 0)
							throw new CommandSyntaxError(input, new Vector2<>(i, i + 1), "Invalid character.");
						map.put(key, val);
						key = "";
						val = "";
						typ = 0;
						end = 0;
						break;
					} else {
						throw new CommandSyntaxError(input, new Vector2<>(i, i + 1), "Unparsable character.");
					}
				}

				if (end == -1)
					throw new CommandSyntaxError(input, new Vector2<>(input.length() - 1, input.length()), "Unended argument.");

				int x = executor.x;
				int y = executor.y;
				if (map.containsKey("x")) try {
					x = Integer.parseInt(map.get("x"));
				} catch (NumberFormatException e) {
					throw new InvalidCommandArgumentError("target filter",
						String.format("\"%s\" with key \"x\"", map.get("x")), "Unparsable value.", e);
				}

				if (map.containsKey("y")) try {
					x = Integer.parseInt(map.get("y"));
				} catch (NumberFormatException e) {
					throw new InvalidCommandArgumentError("target filter",
						String.format("\"%s\" with key \"y\"", map.get("y")), "Unparsable value.", e);
				}

				for (Entry<String, String> entry : map.entrySet()) {
					switch (entry.getKey()) {
						case "x":
						case "y":
							break; // Applied before.
						case "type":
							switch (entry.getValue()) {
								case "furniture":
									filter = filter.and(e -> e instanceof Furniture);
									break;
								case "mob":
									filter = filter.and(e -> e instanceof Mob);
									break;
								case "enemy":
									filter = filter.and(e -> e instanceof EnemyMob);
									break;
								case "passive":
									filter = filter.and(e -> e instanceof PassiveMob);
									break;
								case "player":
									filter = filter.and(e -> e instanceof Player);
									break;
								case "particle":
									filter = filter.and(e -> e instanceof Particle);
									break;
								case "arrow":
									filter = filter.and(e -> e instanceof Arrow);
									break;
								case "item":
									filter = filter.and(e -> e instanceof ItemEntity);
									break;
								case "spark":
									filter = filter.and(e -> e instanceof Spark);
									break;
								default:
									throw new InvalidCommandArgumentError("target filter",
										String.format("\"%s\" with key \"type\"", entry.getValue()), "Invalid entity type.");
							}

							break;
						case "dx":
							int ox = x;
							int dx;
							try {
								dx = Integer.parseInt(entry.getValue());
							} catch (NumberFormatException e) {
								throw new InvalidCommandArgumentError("target",
									String.format("\"%s\" with key \"%s\"", entry.getValue(), entry.getKey()), "Unparsable value.", e);
							}

							filter = filter.and(e -> e.x >= ox && e.x <= dx);
							break;
						case "dy":
							int oy = y;
							int dy;
							try {
								dy = Integer.parseInt(entry.getValue());
							} catch (NumberFormatException e) {
								throw new InvalidCommandArgumentError("target",
									String.format("\"%s\" with key \"%s\"", entry.getValue(), entry.getKey()), "Unparsable value.", e);
							}

							filter = filter.and(e -> e.y >= oy && e.y <= dy);
							break;
						case "distance":
							int dist;
							try {
								dist = Integer.parseInt(entry.getValue());
							} catch (NumberFormatException e) {
								throw new InvalidCommandArgumentError("target",
									String.format("\"%s\" with key \"%s\"", entry.getValue(), entry.getKey()), "Unparsable value.", e);
							}

							filter = filter.and(e -> e.isWithin(dist, executor));
							break;
						// case "gamemode": // Cannot use this filter.
						// 	filter = filter.and(e -> e instanceof Player);
						// 	break;
						default:
							throw new CommandSyntaxError(input, new Vector2<>(2, input.length()), "Invalid filter key: " + entry.getKey() + ".");
					}
				}
			}

			HashSet<Entity> entities = new HashSet<>();
			Set<Entity> levelEntities;
			/*for (int i = 0; i < World.levels.length; i++)*/ synchronized (levelEntities = ((LevelMixin) /*World.levels[i]*/executor.getLevel()).getEntities()) {
				for (Entity e : levelEntities) {
					if (filter.test(e)) entities.add(e);
				}
			}

			return entities;
		} else {
			throw new CommandSyntaxError(input, new Vector2<>(1, 2), "Unparsable target variable.");
		}
	}

	private static class CommandSyntaxError extends Exception {
		public final String input, msg;
		public final int index0, index1;
		private String expected = null;

		public CommandSyntaxError(String input, Vector2<Integer> index, String msg) {
			super(msg);
			this.input = input;
			this.msg = msg;
			this.index0 = index.x;
			this.index1 = index.y;
		}

		@SuppressWarnings("unused")
		public CommandSyntaxError(String input, Vector2<Integer> index, String msg, Throwable cause) {
			super(msg, cause);
			this.input = input;
			this.msg = msg;
			this.index0 = index.x;
			this.index1 = index.y;
		}

		@SuppressWarnings("unused")
		public CommandSyntaxError setExpected(String expected) {
			this.expected = expected;
			return this;
		}
		@Nullable
		public String getExpected() { return expected; }
	}

	private static class InvalidCommandArgumentError extends Exception {
		public final String name, value, msg;

		public InvalidCommandArgumentError(String name, String value, String msg) {
			super(msg);
			this.name = name;
			this.msg = msg;
			this.value = value;
		}

		public InvalidCommandArgumentError(String name, String value, String msg, Throwable cause) {
			super(msg, cause);
			this.name = name;
			this.msg = msg;
			this.value = value;
		}
	}

	@SuppressWarnings("unused")
	private static void sendSyntaxError(CommandSyntaxError error) {
		sendSyntaxError(error.input, new Vector2<>(error.index0, error.index1), error.msg);
		if (error.getCause() != null) System.out.println(String.format("%sCause: %s%s", ConsoleColors.RED, error.getCause().toString(), ConsoleColors.RESET));
	}
	private static void sendSyntaxError(String input, Vector2<Integer> index, String msg) {
		System.out.println(String.format("%sInvalid syntax: %s%s", ConsoleColors.RED, ConsoleColors.RESET, input));
		System.out.println(String.format("                %s%s%s%s", String.join("", Collections.nCopies(index.x, " ")), ConsoleColors.RED_BOLD_BRIGHT,
			String.join("", Collections.nCopies(index.y - index.x, "^")), ConsoleColors.RESET));
		System.out.println(msg);
	}

	private static void sendInvalidArgumentError(InvalidCommandArgumentError error) {
		sendInvalidArgumentError(error.name, error.value, error.msg);
		if (error.getCause() != null) System.out.println(String.format("%sCause: %s%s", ConsoleColors.RED, error.getCause().toString(), ConsoleColors.RESET));
	}
	private static void sendInvalidArgumentError(String name, String value, String msg) {
		System.out.println(String.format("%sInvalid argument with: %s%s", ConsoleColors.RED, ConsoleColors.RESET, name));
		System.out.println(String.format("\t%s", value));
		System.out.println(msg);
	}
}
