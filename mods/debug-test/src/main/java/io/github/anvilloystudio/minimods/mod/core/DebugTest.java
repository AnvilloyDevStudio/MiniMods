package io.github.anvilloystudio.minimods.mod.core;

import io.github.anvilloystudio.minimods.api.ModConfig;

public class DebugTest {
	public static void main(String[] args) {
		System.out.println("THIS JAR IS NOT PURPOSED TO BE EXECUTED DIRECTLY.");
	}

	public static void entry() {
	}

	public static void init() {
	}

	public static void preInit() {
		ModConfig cfg = new ModConfig();
		cfg.put("test", "test");
		cfg.saveFile();
	}
}
