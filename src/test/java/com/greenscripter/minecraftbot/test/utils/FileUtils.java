package com.greenscripter.minecraftbot.test.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

	public static void copy(File source, File target) throws IOException {
		if (source.isDirectory()) {
			target.mkdir();
			for (File f : source.listFiles()) {
				copy(f, new File(target, f.getName()));
			}
		} else {
			Files.copy(source.toPath(), target.toPath());
		}
	}

	public static void delete(File target) throws IOException {
		if (target.isDirectory()) {
			for (File f : target.listFiles()) {
				delete(f);
			}
		}
		target.delete();
	}
}
