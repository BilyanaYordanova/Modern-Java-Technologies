package bg.sofia.uni.fmi.mjt.commands;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.server.SpotifyServer;

public class Top extends Command {

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		if (message.isEmpty()) {
			return "Invalid number of arguments received. Type number of top songs to be shown ";
		}

		String[] messageTokens = message.trim().split("\\s+");
		if (messageTokens.length > 1) {
			return "Invalid number of arguments received. Type only one number of top songs to be shown ";
		}

		StringBuilder result = new StringBuilder(Constants.EMPTY_STRING);

		SpotifyServer.mapSongToNumberListens.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(
						Collections.reverseOrder(Comparator.comparingInt(counter -> counter.intValue()))))
				.limit(Integer.parseInt(messageTokens[0]))
				.forEachOrdered(entry -> {
					result.append(entry.getKey().getArtists() + " - " + entry.getKey().getSongTitle() + "\n");
				});

		return result.toString().trim();
	}
}
