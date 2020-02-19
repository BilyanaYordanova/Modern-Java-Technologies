package bg.sofia.uni.fmi.mjt.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import bg.sofia.uni.fmi.mjt.checker.ArgumentsChecker;
import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;

public class Register extends Command {

	private Path path;

	public Register() {
		this.path = Paths
				.get("src" + File.separator + "resources" + File.separator + "data" + File.separator + "users.txt");
	}

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		String[] messageTokens = message.trim().split("\\s+");
		if (!ArgumentsChecker.areEnoughArgumentsReceived(messageTokens, Constants.STRING_SPLIT_LIMIT)) {
			return "Invalid number of arguments received. Type email and password";
		}

		String email = messageTokens[0];
		String password = messageTokens[1];

		if (containsEmailInvalidFileNameChars(email)) {
			return "The email you have typed contains invalid characters. Please, type valid email ";
		}

		try {
			FileMethodsHolder.createFileIfItDoesntExist(path);
		} catch (IOException e) {
			String exceptionMessage = "Failed to create users file when executing register command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}

		if (containsFileUser(path, email)) {
			return String.format("User with email <%s> already exists ", email);
		}

		writeUserDataToFile(path, email, password);

		return String.format("User with email <%s> was successfully registered ", email);
	}

	private boolean containsFileUser(Path path, String email) {
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			String currentLine = null;
			while ((currentLine = reader.readLine()) != null) {
				String[] currentLineTokens = currentLine.split("\\s+");
				if (currentLineTokens[0].equals(email)) {
					return true;
				}
			}
		} catch (IOException e) {
			String exceptionMessage =
					"Failed to open BufferedReader to check if user has already been saved in registered users file when executing register command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
		return false;
	}

	private void writeUserDataToFile(Path path, String email, String password) {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
			writer.write(email + " " + password.hashCode() + "\n");
		} catch (IOException e) {
			String exceptionMessage =
					"Failed to open BufferWriter to save new user data to registered users file when executing register command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	private boolean containsEmailInvalidFileNameChars(String email) {
		return email.contains("\\") || email.contains("/") || email.contains(":") || email.contains("*")
				|| email.contains("?") || email.contains("\"") || email.contains("<") || email.contains(">")
				|| email.contains("|");
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

}
