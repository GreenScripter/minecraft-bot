package greenscripter.minecraft.gameinfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceExtractor {

	//java -DbundlerMainClass=net.minecraft.data.Main -jar mojang_1.20.4.jar --reports --server
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		System.out.println(getJSONs("greenscripter/minecraft/resources/data/minecraft/tags/items"));
	}

	public static String getJSON(String path) throws URISyntaxException, IOException {
		URI uri = ResourceExtractor.class.getClassLoader().getResource(path).toURI();
		return Files.readString(Paths.get(uri));
	}

	public static Map<String, String> getJSONs(String path) throws URISyntaxException, IOException {
		URI uri = ResourceExtractor.class.getClassLoader().getResource(path).toURI();
		Path myPath;
		if (uri.getScheme().equals("jar")) {
			FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
			myPath = fileSystem.getPath(path);
		} else {
			myPath = Paths.get(uri);
		}
		Map<String, String> result = new HashMap<>();
		try (Stream<Path> walk = Files.walk(myPath, 1)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path p = it.next();
				if (!Files.isDirectory(p)) {
					result.put(p.getFileName().toString(), Files.readString(p));
				}
			}
		}
		return result;
	}
}
