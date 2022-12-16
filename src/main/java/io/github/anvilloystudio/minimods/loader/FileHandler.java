package io.github.anvilloystudio.minimods.loader;

import org.tinylog.Logger;

import java.io.File;

public class FileHandler {
	public static void checkAndReplaceWithDir(File file) {
		if (file.isFile()) {
			Logger.warn("File \"{}\" is not a directory, it is replaced by a newly created directory. All contents in the path are removed.");
			file.delete();
			file.mkdirs();
		}
	}
	public static void checkAndDeleteIfDir(File file) {
		if (file.isDirectory()) {
			Logger.warn("File \"{}\" is a directory, it is replaced by a newly created empty file. All contents in the path are removed.");
			file.delete();
		}
	}
}
