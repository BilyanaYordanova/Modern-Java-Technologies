package bg.sofia.uni.fmi.mjt.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import bg.sofia.uni.fmi.mjt.commands.CommandsCollection;
import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.data.SongsDatabase;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;

public class SpotifyServer {

	public static Map<Song, AtomicInteger> mapSongToNumberListens = SongsDatabase.getSongsCollection();

	public static void main(String[] args) {
		SpotifyServer spotifyServer = new SpotifyServer();
		spotifyServer.connectWithClient();
	}

	private void connectWithClient() {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

			serverSocketChannel.bind(new InetSocketAddress(Constants.SERVER_HOST, Constants.SERVER_PORT));
			serverSocketChannel.configureBlocking(false);
			System.out.println("Server is started and waiting for clients to connect...");

			Selector selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);

			while (true) {
				int channels = selector.select();

				if (channels == 0) {
					System.out.println("Server is still waiting for clients to connect...");
					continue;
				}

				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

				while (selectionKeyIterator.hasNext()) {
					SelectionKey key = selectionKeyIterator.next();

					if (key.isReadable()) {
						SocketChannel socketChannel = (SocketChannel) key.channel();

						buffer.clear();
						int readFromBuffer = socketChannel.read(buffer);
						if (readFromBuffer <= 0) {
							System.out.println("There is nothing to read from the buffer, closing channel..");
							socketChannel.close();
							break;
						}

						buffer.flip();
						String message = new String(buffer.array(), 0, buffer.limit());
						executeCommand(socketChannel, message, buffer, selector);

					} else if (key.isAcceptable()) {
						ServerSocketChannel serverSockChannel = (ServerSocketChannel) key.channel();
						handleIfKeyAcceptable(serverSockChannel, selector);

					} else if (key.isWritable()) {
						SocketChannel socketChannel = (SocketChannel) key.channel();
						handleIfKeyWritable(buffer, socketChannel, selector);

					}
					selectionKeyIterator.remove();
				}
			}

		} catch (IOException e) {
			String exceptionMessage = 
					"Failed to register operations while working with socket channel in Server ";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	private void executeCommand(SocketChannel socketChannel, String messageFromClient, ByteBuffer buffer,
			Selector selector) {
		String[] userMessageTokens = messageFromClient.split(" ", Constants.STRING_SPLIT_LIMIT);
		String command = userMessageTokens[0];
		String message = "";

		if (userMessageTokens.length > 1) {
			message = userMessageTokens[1];
		}
		String messageToSend = CommandsCollection.getCommandsCollection().get(command)
				.execute(message, buffer, socketChannel, selector);

		if (!command.equals("play")) {

			buffer.clear();
			buffer.put(messageToSend.getBytes());
			try {
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_WRITE);
			} catch (IOException e) {
				String exceptionMessage = 
						"Failed to register OP_WRITE to socket channel while executing commands in Server";
				FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
				throw new RuntimeException(exceptionMessage);
			}

			if (messageToSend.equals(Constants.DISCONNECTED_MESSAGE)) {
				try {
					socketChannel.close();
				} catch (IOException e) {
					String exceptionMessage = "Failed closing socket channel after executing disconnect command";
					FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
					throw new RuntimeException(exceptionMessage);
				}
			}
		}
	}

	private void handleIfKeyAcceptable(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

	private void handleIfKeyWritable(ByteBuffer buffer, SocketChannel socketChannel, Selector selector)
			throws IOException {
		buffer.flip();
		socketChannel.write(buffer);
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
}
