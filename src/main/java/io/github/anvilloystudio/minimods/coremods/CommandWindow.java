package io.github.anvilloystudio.minimods.coremods;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

import minicraft.core.Game;

public class CommandWindow {
	private static JFrame frame;
	private static JTextField input;
	private static boolean enabled = false;

	/** The default number of columns. */
	private static int defaultWidth;

	static {
		// Basic Settings.
		frame = new JFrame("Command Window");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.setLocationRelativeTo(null);

		// Command input.
		input = new JTextField();
		defaultWidth = input.getColumns();
		frame.add(input, BorderLayout.CENTER);
		frame.setSize(input.getSize());
		frame.pack();
	}

	public static void init() {
		if (Game.debug) {
			new Thread(() -> {
				frame.setVisible(true);
			}, "Command Window Starter").start();
			frame.toBack();
			enabled = true;
		}
	}
}
