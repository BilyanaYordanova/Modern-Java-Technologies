package bg.sofia.uni.fmi.mjt.commands;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.data.SongsDatabase;

public class Search extends Command {

	private List<Song> foundSongs;

	public Search() {
		foundSongs = new ArrayList<Song>();
	}

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		if (message.isEmpty()) {
			return "Type song artists or song title to search for ";
		}

		String[] wordsToSearch = message.trim().split("\\s+");

		StringBuilder resultMessage = new StringBuilder(Constants.EMPTY_STRING);

		for (Song song : SongsDatabase.getSongsCollection().keySet()) {
			for (String word : wordsToSearch) {

				if (song.getArtists().toLowerCase().contains(word.toLowerCase())
						|| song.getSongTitle().toLowerCase().contains(word.toLowerCase())) {
					if (!foundSongs.contains(song)) {
						resultMessage.append(song.getArtists() + " - " + song.getSongTitle() + "\n");
						foundSongs.add(song);
					}
				}
			}
		}

		if (foundSongs.isEmpty()) {
			return "No songs were found. Try again ";
		}

		return resultMessage.toString().trim();
	}

	public List<Song> getFoundSongs() {
		return foundSongs;
	}

	public void setFoundSongs(List<Song> foundSongs) {
		this.foundSongs = foundSongs;
	}
}
