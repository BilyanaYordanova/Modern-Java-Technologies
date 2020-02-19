package bg.sofia.uni.fmi.mjt.holders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMethodsHolder {

	public static void saveExceptionsToFile(String exceptionMessage) {
		Path path = Paths.get("src" + File.separator + "logs" + File.separator + "exceptions.txt");
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				throw new RuntimeException(String.format("Failed to create file for storing exceptions"));
			}
		}

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write(exceptionMessage + "\n");

		} catch (IOException e) {
			throw new RuntimeException("Exception thrown trying to create new BufferedWriter for exceptions file");
		}
	}
}
