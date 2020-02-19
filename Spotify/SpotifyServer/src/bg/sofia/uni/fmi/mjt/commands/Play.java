package bg.sofia.uni.fmi.mjt.commands;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.threads.SongStream;

public class Play extends Command {

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		this.user = getKeyByValueFromMap(Login.currentlyLoggedUsers, socketChannel);
		if (user == null) {
			return "It seems that you are not logged in the platform. Please, login first so you can play a song ";
		}

		List<Song> foundSongs = findSongsContainingGivenMessage(message, buffer, socketChannel, selector);

		if (foundSongs.size() > 1) {
			return String.format(
					"There seems to be more than one song which contains this word/s <%s>. Please try again ", message);
		} else if (foundSongs.size() == 0) {
			return "No songs were found. Please try again ";
		}

		this.buffer = buffer;
		this.socketChannel = socketChannel;
		this.selector = selector;
		Song songToPlay = foundSongs.get(0);

		Thread songStreamThread = new Thread(new SongStream(buffer, socketChannel, songToPlay));

		songStreamThread.start();

		return "";
	}

	private List<Song> findSongsContainingGivenMessage(String song, ByteBuffer buffer, SocketChannel socketChannel,
			Selector selector) {
		Search searchCommand = new Search();
		searchCommand.execute(song, buffer, socketChannel, selector);

		return searchCommand.getFoundSongs();
	}
}
