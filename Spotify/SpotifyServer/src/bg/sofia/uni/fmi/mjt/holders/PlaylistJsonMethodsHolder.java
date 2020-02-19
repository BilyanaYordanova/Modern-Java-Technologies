package bg.sofia.uni.fmi.mjt.holders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bg.sofia.uni.fmi.mjt.data.Playlist;

public class PlaylistJsonMethodsHolder {

	public static List<Playlist> playlists = new ArrayList<>();

	public static void getPlaylistsFromJsonFile(Path path, SocketChannel socketChannel) {
		// New
		playlists.clear();
		try {
			String jsonContent = Files.readString(path);

			if (!jsonContent.isEmpty()) {
				Gson gson = new Gson();
				Type listType = new TypeToken<List<Playlist>>() {
				}.getType();
				synchronized (socketChannel) {
					playlists = gson.fromJson(jsonContent, listType);
				}
			}

		} catch (IOException e) {
			String exceptionMessage = "Failed reading json content from playlists json file";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	public static void savePlaylistInJsonFile(Path path, Playlist playlist, SocketChannel socketChannel) {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			Gson gson = new Gson();
			synchronized (socketChannel) {
				playlists.add(playlist);
				gson.toJson(playlists, writer);
			}

		} catch (IOException e) {
			String exceptionMessage = "Failed to open BufferedWriter to save playlists data in json file";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	/***
	 * Check if any of the already created playlists has the same name value as
	 * playlistName
	 * 
	 * @param playlistName - name of the playlist to be created
	 * @return true if playlist with this name exists
	 */
	public static boolean playlistExists(String playlistName) {
		if (playlists.stream().anyMatch(playlist -> playlist.getNamePlaylist().equals(playlistName))) {
			return true;
		}
		return false;
	}

	public static boolean isPlaylistsEmpty() {
		return playlists.size() == 0;
	}

}
