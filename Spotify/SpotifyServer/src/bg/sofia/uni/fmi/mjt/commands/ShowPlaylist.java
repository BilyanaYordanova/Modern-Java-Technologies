package bg.sofia.uni.fmi.mjt.commands;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.checker.ArgumentsChecker;
import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.Playlist;
import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;
import bg.sofia.uni.fmi.mjt.holders.PlaylistJsonMethodsHolder;

public class ShowPlaylist extends Command {

	private Path path;

	/***
	 * message is the name of the playlist to be shown
	 */
	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		this.user = getKeyByValueFromMap(Login.currentlyLoggedUsers, socketChannel);
		if (user == null) {
			return "It seems that you are not logged in the platform. Please, login first so you can use show-playlist command ";
		}

		String[] tokens = message.trim().split("\\s+");
		if (!ArgumentsChecker.areEnoughArgumentsReceived(tokens, 1)) {
			return "Valid playlist name should be only one word. Please try again ";
		}

		this.socketChannel = socketChannel;
		this.path = user.getPlaylistsFilePath();
		try {
			FileMethodsHolder.createFileIfItDoesntExist(path);
		} catch (IOException e) {
			String exceptionMessage =
					"Failed to create playlist file when executing show-playlist command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}

		if (user.getPlaylists().size() == 0) {
			return executeIfPlaylistsAreNotExtractedFromFile(message);
		} else {
			return executeIfPlaylistsAreExtractedFromFile(message);
		}
	}

	private String executeIfPlaylistsAreNotExtractedFromFile(String playlistName) {
		PlaylistJsonMethodsHolder.getPlaylistsFromJsonFile(path, socketChannel);

		if (PlaylistJsonMethodsHolder.isPlaylistsEmpty()) {
			return "You have no playlists. You can create one using command: create-playlist <name_of_the_playlist>";
		}

		return showPlaylist(playlistName);
	}

	private String executeIfPlaylistsAreExtractedFromFile(String playlistName) {
		PlaylistJsonMethodsHolder.playlists = user.getPlaylists();

		return showPlaylist(playlistName);
	}

	private String showPlaylist(String playlistName) {
		List<Playlist> foundPlaylists = findPlaylistsByName(playlistName);

		if (foundPlaylists.size() == 0) {
			return String.format("Playlist with name <%s> doesn't exist ", playlistName);
		}

		Playlist playlistToShow = foundPlaylists.get(0);

		StringBuilder songs = new StringBuilder(Constants.EMPTY_STRING);
		for (Song song : playlistToShow.getSongsInPlaylist()) {
			songs.append(song.toString());
		}

		return String.format("Playlist name: %s \nSongs in playlist: \n%s", playlistName, songs.toString().trim());
	}

	private List<Playlist> findPlaylistsByName(String playlistName) {
		return PlaylistJsonMethodsHolder.playlists
				.stream()
				.filter(playlist -> playlist.getNamePlaylist().equals(playlistName))
				.collect(Collectors.toList());
	}
}
