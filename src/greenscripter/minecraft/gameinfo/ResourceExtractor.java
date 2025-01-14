package greenscripter.minecraft.gameinfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceExtractor {

	//java -DbundlerMainClass=net.minecraft.data.Main -jar mojang_1.21.jar --reports --server

	public static void main(String[] args) throws URISyntaxException, IOException {
		System.out.println(getJSONs("greenscripter/minecraft/resources/data/minecraft/tags/item"));
	}

	public static String getJSON(String path) throws URISyntaxException, IOException {
		InputStream in = ResourceExtractor.class.getClassLoader().getResource(path).openStream();
		return new String(in.readAllBytes());
	}

	public static Map<String, String> getJSONs(String path) throws URISyntaxException, IOException {
		URI uri = ResourceExtractor.class.getClassLoader().getResource(path).toURI();
		Path myPath;
		if (uri.getScheme().equals("jar")) {
			FileSystem fileSystem = null;
			try {
				fileSystem = FileSystems.getFileSystem(uri);
			} catch (Exception e) {

			}
			if (fileSystem == null) fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
			myPath = fileSystem.getPath(path);
		} else {
			myPath = Paths.get(uri);
		}
		Map<String, String> result = new HashMap<>();
		int pathLength = myPath.toString().length() + 1;
		try (Stream<Path> walk = Files.walk(myPath, 10)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path p = it.next();
				if (!Files.isDirectory(p)) {
					if (p.getFileName().toString().startsWith(".")) continue;

					String pathName = p.toString().substring(pathLength);
					result.put(pathName, Files.readString(p));
				}
			}
		}
		return result;
	}
}
