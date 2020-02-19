package bg.sofia.uni.fmi.mjt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;

public class SpotifyClientWriter {

	private ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);

	public static void main(String[] args) {
		SpotifyClientWriter spotifyClient = new SpotifyClientWriter();
		spotifyClient.manageConnectionWithServer();
	}

	private void manageConnectionWithServer() {
		try (SocketChannel socketChannel = SocketChannel.open(); 
				Scanner scanner = new Scanner(System.in)) {

			socketChannel.connect(new InetSocketAddress(Constants.SERVER_HOST, Constants.SERVER_PORT));

			System.out.println("Client is successfully connected to the server.\n" + "Enter your command: ");

			Thread clientThread = new Thread(new SpotifyClientThreadReader(socketChannel));
			clientThread.start();

			while (true) {
				String message = scanner.nextLine().trim();

				String[] inputs = message.split("\\s+");
				String command = inputs[0];

				if (isCommandValid(command)) {

					buffer.clear();
					buffer.put(message.getBytes());
					buffer.flip();
					socketChannel.write(buffer);

				} else {
					printMessageWithValidCommands();
				}
			}

		} catch (IOException e) {
			String exceptionMessage = "Failed writing client input data to socket channel";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	private boolean isCommandValid(String command) {
		return command.equals("register") || command.equals("login") || command.equals("disconnect")
				|| command.equals("search") || command.equals("top") || command.equals("create-playlist")
				|| command.equals("add-song-to") || command.equals("show-playlist") || command.equals("play")
				|| command.equals("stop");
	}

	private void printMessageWithValidCommands() {
		System.out.println(
				"Invalid command entered! Type one of this commands: \r\n" + "  - register <email> <password> \r\n"
						+ "  - login <email> <password> \r\n" + "  - disconnect \r\n" + "  - search <words> \r\n"
						+ "  - top <number> \r\n" + "  - create-playlist <name_of_the_playlist> \r\n"
						+ "  - add-song-to <name_of_the_playlist> <song> \r\n"
						+ "  - show-playlist <name_of_the_playlist> \r\n" + "  - play <song> \r\n" + "  - stop \r\n");
	}
}
