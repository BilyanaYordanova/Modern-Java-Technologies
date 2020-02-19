package bg.sofia.uni.fmi.mjt.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.checker.ArgumentsChecker;
import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.User;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;

public class Login extends Command {

	public static Map<User, SocketChannel> currentlyLoggedUsers = new HashMap<>();
	private static boolean isPasswordCorrect = false;

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		String[] messageTokens = message.trim().split(Constants.WHITESPACE);
		if (!ArgumentsChecker.areEnoughArgumentsReceived(messageTokens, Constants.STRING_SPLIT_LIMIT)) {
			return "Invalid number of arguments received. Type your email and password";
		}

		String email = messageTokens[0];
		String password = messageTokens[1];

		Path path = Paths
				.get("src" + File.separator + "resources" + File.separator + "data" + File.separator + "users.txt");

		if (!Files.exists(path)) {
			return "There are no registered users. Register in the platform so you can log in ";
		}

		if (currentlyLoggedUsers.keySet()
				.stream()
				.anyMatch(user -> user.getEmail().equals(email))) {
			return String.format("User with email <%s> is already logged in the platform ", email);
		}

		if (containsFileUser(path, email, password)) {
			if (!isPasswordCorrect) {
				return "Wrong password entered. Please try again ";
			}

			Path userPlaylistFilePath = Paths.get(
					"src" + File.separator + "resources" + File.separator + "data" + File.separator + email + ".json");
			User currentUser = new User(email, password.hashCode(), userPlaylistFilePath);
			currentlyLoggedUsers.put(currentUser, socketChannel);

			return String.format("User with email <%s> successfully logged in  ", email);
		} else {
			return String.format("User with email <%s> isn't registered. Register first so you can log in ", email);
		}

	}

	boolean containsFileUser(Path path, String email, String password) {
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			String currentLine = null;
			while ((currentLine = reader.readLine()) != null) {
				String[] currentLineTokens = currentLine.split("\\s+");
				if (currentLineTokens[0].equals(email)) {
					isPasswordCorrect = Integer.parseInt(currentLineTokens[1]) == password.hashCode();
					return true;
				}
			}
		} catch (IOException e) {
			String exceptionMessage = 
					"Failed to open BufferedReader to check if user has already been saved in registered users file when executing login command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
		return false;
	}
}
