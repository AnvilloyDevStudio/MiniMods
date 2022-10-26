package io.github.anvilloystudio.minimods.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.gfx.*;
import minicraft.screen.Display;

public class ModsDisplay extends Display {
	private static ArrayList<Object> mods = ModLoaderCommunication.getModList();
	private static int selectedIndex = 0;
	private static int pageIndex = 0;
	private List<List<String>> pages = new ArrayList<>();

	public ModsDisplay() {
		if (selectedIndex >= mods.size()) selectedIndex = 0;
		if (pageIndex >= mods.size()) selectedIndex = 0;
	}

	@Override
	public void tick(InputHandler input) {
		if (input.getKey("menu").clicked || input.getKey("attack").clicked || input.getKey("exit").clicked) {
			Game.exitMenu();
			return;
		} else if (input.getKey("cursor-down").clicked && selectedIndex < mods.size() - 1) {
			selectedIndex++;
			pageIndex = 0;
			Sound.select.play();
		} else if (input.getKey("cursor-up").clicked && selectedIndex > 0) {
			selectedIndex--;
			pageIndex = 0;
			Sound.select.play();
		} else if (input.getKey("cursor-left").clicked && pageIndex > 0) {
			pageIndex--;
		} else if (input.getKey("cursor-right").clicked && pageIndex < pages.size() -1) {
			pageIndex++;
		}
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0);

		// ModContainer reflection part.
		Field modMetadataField;
		Field modMetadataNameField;
		Field modMetadataVersionField;
		Field modMetadataDescriptionField;
		try {
			modMetadataField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer", "metadata");
			modMetadataNameField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer$ModMetadata", "name");
			modMetadataVersionField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer$ModMetadata", "version");
			modMetadataDescriptionField = ModLoaderCommunication.getField("io.github.anvilloystudio.minimods.core.ModContainer$ModMetadata", "description");
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException e2) {
			throw new RuntimeException(e2);
		}

        // Get skin above and below.
		String selectedUUUUU;
		String selectedUUUU;
		String selectedUUU;
		String selectedUU;
		String selectedU;
		String selectedD;
		String selectedDD;
		String selectedDDD;
		String selectedDDDD;
		String selectedDDDDD;
		try {
			selectedUUUUU = selectedIndex + 5 > mods.size() - 5 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex + 5)));
			selectedUUUU = selectedIndex + 4 > mods.size() - 4 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex + 4)));
			selectedUUU = selectedIndex + 3 > mods.size() - 3 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex + 3)));
			selectedUU = selectedIndex + 2 > mods.size() - 2 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex + 2)));
			selectedU = selectedIndex + 1 > mods.size() - 1 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex + 1)));
			selectedD = selectedIndex - 1 < 0 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex - 1)));
			selectedDD = selectedIndex - 2 < 0 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex - 2)));
			selectedDDD = selectedIndex - 3 < 0 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex - 3)));
			selectedDDDD = selectedIndex - 4 < 0 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex - 4)));
			selectedDDDDD = selectedIndex - 5 < 0 ? "" : (String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex - 5)));
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			throw new RuntimeException(e2);
		}

		// Title.
		Font.drawCentered("Mods", screen, Screen.h - 185, Color.YELLOW);

		// Render the menu.
		Font.draw(ModsDisplay.shortNameIfLong(selectedUUUUU), screen, 5, Screen.h - 55, Color.GRAY); // First unselected space
		Font.draw(ModsDisplay.shortNameIfLong(selectedUUUU), screen, 5, Screen.h - 65, Color.GRAY); // First unselected space
		Font.draw(ModsDisplay.shortNameIfLong(selectedUUU), screen, 5, Screen.h - 75, Color.GRAY); // First unselected space
		Font.draw(ModsDisplay.shortNameIfLong(selectedUU), screen, 5, Screen.h - 85, Color.GRAY); // First unselected space
		Font.draw(ModsDisplay.shortNameIfLong(selectedU), screen, 5, Screen.h - 95, Color.GRAY); // First unselected space
		if (mods.size() != 0) try {
			Font.draw(ModsDisplay.shortNameIfLong((String) modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex)))), screen, 5, Screen.h - 105, Color.WHITE); // Selection
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			throw new RuntimeException(e1);
		}
		Font.draw(ModsDisplay.shortNameIfLong(selectedD), screen, 5, Screen.h - 115, Color.GRAY); // Fourth space
		Font.draw(ModsDisplay.shortNameIfLong(selectedDD), screen, 5, Screen.h - 125, Color.GRAY); // Fourth space
		Font.draw(ModsDisplay.shortNameIfLong(selectedDDD), screen, 5, Screen.h - 135, Color.GRAY); // Fourth space
		Font.draw(ModsDisplay.shortNameIfLong(selectedDDDD), screen, 5, Screen.h - 145, Color.GRAY); // Fourth space
		Font.draw(ModsDisplay.shortNameIfLong(selectedDDDDD), screen, 5, Screen.h - 155, Color.GRAY); // Fourth space
		if (mods.size() == 0) Font.drawCentered("No mod available.", screen, Screen.h/2, Color.CYAN);
		FontStyle fs = new FontStyle();
		if (mods.size() != 0) {
			try {
				Font.drawCentered("Name: " + modMetadataNameField.get(modMetadataField.get(mods.get(selectedIndex))), screen, Screen.h-170, Color.WHITE);
				Font.drawCentered("Version: " + modMetadataVersionField.get(modMetadataField.get(mods.get(selectedIndex))), screen, Screen.h-158, Color.WHITE);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		fs.setXPos(Screen.w/2-10);
		fs.setYPos(Screen.h-140);
		List<String> des = new ArrayList<>();
		if (mods.size() != 0) try {
			for (String line : Arrays.asList(((String) modMetadataDescriptionField.get(modMetadataField.get(mods.get(selectedIndex)))).split("\n"))) {
				int br = (int)Math.ceil(line.length()/18)+1;
				for (int a = 0; a<br; a++) {
					int b = (a+1)*18;
					des.add(line.substring(a*18, b>line.length()? line.length(): b));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		List<List<String>> p = new ArrayList<>();
		p.add(new ArrayList<>());
		for (int a = 0; a<des.size(); a++) {
			if ((int)(a/9)>p.size() && a%9==0) p.add(new ArrayList<>());
			p.get(p.size()-1).add(des.get(a));
		}
		pages = p;
        Font.drawParagraph(pages.get(pageIndex), (Screen)screen, fs, 3);
		if (mods.size() != 0) Font.draw("Pages: "+(pageIndex+1)+"/"+p.size(), screen, Screen.w/2-10, Screen.h-32);
		// Help text.
		Font.drawCentered("Use "+ Game.input.getMapping("cursor-down") + " and " + Game.input.getMapping("cursor-up") + " to select mod.", screen, Screen.h - 9, Color.WHITE);
		Font.drawCentered("Use "+ Game.input.getMapping("cursor-left") + " and " + Game.input.getMapping("cursor-right") + " to move pages.", screen, Screen.h - 19, Color.WHITE);
	}

	// In case the name is too long ...
	private static String shortNameIfLong(String name) {
		return name.length() > 15 ? name.substring(0, 10) + "..." : name;
	}

	public static int getSelectedIndex() {
		return selectedIndex;
	}

	public static void setSelectedIndex(int selectedIndex) {
		ModsDisplay.selectedIndex = selectedIndex;
	}
}
