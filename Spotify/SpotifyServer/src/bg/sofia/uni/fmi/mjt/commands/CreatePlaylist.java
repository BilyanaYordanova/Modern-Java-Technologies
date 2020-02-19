package bg.sofia.uni.fmi.mjt.commands;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.ArrayList;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.Playlist;
import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.data.User;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;
import bg.sofia.uni.fmi.mjt.holders.PlaylistJsonMethodsHolder;

public class CreatePlaylist extends Command {

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		this.user = getKeyByValueFromMap(Login.currentlyLoggedUsers, socketChannel);
		if (user == null) {
			return "It seems that you are not logged in the platform. Please, login first so you can create a playlist ";
		}

		if (!isMessageValidPlaylistName(message)) {
			return "Please enter only one word for playlist name ";
		}

		Playlist playlistToAdd = new Playlist(message, new ArrayList<Song>());
		Path path = user.getPlaylistsFilePath();

		try {
			FileMethodsHolder.createFileIfItDoesntExist(path);
		} catch (IOException e) {
			String exceptionMessage = "Failed to create playlist file when executing create-playlist command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}

		this.socketChannel = socketChannel;
		if (user.getPlaylists().size() == 0) {
			return executeCommandIfPlaylistsAreNotExtractedFromFile(path, user, playlistToAdd, message);

		} else {
			return executeCommandIfPlaylistsAreExtractedFromFile(path, user, playlistToAdd, message);
		}
	}

	private String executeCommandIfPlaylistsAreNotExtractedFromFile(Path path, User user, Playlist playlistToAdd,
			String message) {
		PlaylistJsonMethodsHolder.getPlaylistsFromJsonFile(path, socketChannel);

		if (PlaylistJsonMethodsHolder.isPlaylistsEmpty()
				|| !PlaylistJsonMethodsHolder.playlistExists(playlistToAdd.getNamePlaylist())) {

			PlaylistJsonMethodsHolder.savePlaylistInJsonFile(path, playlistToAdd, socketChannel);
			user.setPlaylists(PlaylistJsonMethodsHolder.playlists);

			return String.format("Playlist <%s> was successfully created", message);
		} else {
			return String.format("Playlist with name <%s> already exists ", message);
		}
	}

	private String executeCommandIfPlaylistsAreExtractedFromFile(Path path, User user, Playlist playlistToAdd,
			String message) {
		PlaylistJsonMethodsHolder.playlists = user.getPlaylists();

		if (!PlaylistJsonMethodsHolder.playlistExists(playlistToAdd.getNamePlaylist())) {
			PlaylistJsonMethodsHolder.savePlaylistInJsonFile(path, playlistToAdd, socketChannel);

			return String.format("Playlist <%s> was successfully created", message);
		} else {
			return String.format("Playlist with name <%s> already exists ", message);
		}
	}

	private boolean isMessageValidPlaylistName(String message) {
		String[] tokens = message.split(Constants.WHITESPACE);
		if (tokens.length == 1) {
			return true;
		}
		return false;
	}
}
