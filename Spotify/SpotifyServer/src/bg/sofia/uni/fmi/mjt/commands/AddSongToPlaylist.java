package bg.sofia.uni.fmi.mjt.commands;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.Playlist;
import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;
import bg.sofia.uni.fmi.mjt.holders.PlaylistJsonMethodsHolder;

public class AddSongToPlaylist extends Command {

	private Path path;
	private String playlistName;
	private Song songToAdd;

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		this.user = getKeyByValueFromMap(Login.currentlyLoggedUsers, socketChannel);
		if (user == null) {
			return "It seems that you are not logged in the platform. Please, login first so you can add song to playlist ";
		}

		String[] inputTokens = message.split(Constants.WHITESPACE, 2);
		if (inputTokens.length < 2) {
			return "Not enough arguments entered. Please type playlist name and song to add ";
		}

		this.playlistName = inputTokens[0];
		String songTokens = inputTokens[1];

		List<Song> foundSongs = findSongsContainingGivenSongTokens(songTokens, buffer, socketChannel, selector);

		if (foundSongs.size() > 1) {
			return String.format(
					"There seems to be more than one song which contains this word/s <%s>. Please try again ",
					songTokens);
		} else if (foundSongs.size() == 0) {
			return "No songs were found. Please try again ";
		}

		this.songToAdd = foundSongs.get(0);
		this.path = user.getPlaylistsFilePath();
		this.socketChannel = socketChannel;

		try {
			FileMethodsHolder.createFileIfItDoesntExist(path);
		} catch (IOException e) {
			String exceptionMessage = "Failed to create playlist file when executing add-song-to command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}

		if (user.getPlaylists().size() == 0) {
			return executeIfPlaylistsAreNotExtractedFromFile();
		} else {
			return executeIfPlaylistsAreExtractedFromFile();
		}
	}

	private String executeIfPlaylistsAreNotExtractedFromFile() {
		PlaylistJsonMethodsHolder.getPlaylistsFromJsonFile(path, socketChannel);

		if (PlaylistJsonMethodsHolder.isPlaylistsEmpty()) {
			return "You have no playlists. Create one playlist so you can add songs to it";
		}

		return addSongToPlaylist();
	}

	private String executeIfPlaylistsAreExtractedFromFile() {
		PlaylistJsonMethodsHolder.playlists = user.getPlaylists();

		return addSongToPlaylist();
	}

	private String addSongToPlaylist() {

		List<Playlist> foundPlaylists = findPlaylistsByName(playlistName);
		PlaylistJsonMethodsHolder.playlists = findPlaylistsNotEqualToName(playlistName);

		if (foundPlaylists.size() == 0) {
			return String.format("Playlist with name <%s> doesn't exist ", playlistName);
		}

		Playlist playlist = foundPlaylists.get(0);
		List<Song> songsInPlaylist = playlist.getSongsInPlaylist();
		if (songsInPlaylist.contains(songToAdd)) {
			return String.format("Song <%s> has already been added to playlist with name <%s> ",
					songToAdd.getArtists() + " - " + songToAdd.getSongTitle(), playlistName);
		}

		songsInPlaylist.add(songToAdd);
		playlist.setSongsInPlaylist(songsInPlaylist);

		PlaylistJsonMethodsHolder.savePlaylistInJsonFile(path, playlist, socketChannel);
		user.setPlaylists(PlaylistJsonMethodsHolder.playlists);

		return String.format("Song <%s> was successfully added to playlist with name <%s> ",
				songToAdd.getArtists() + " - " + songToAdd.getSongTitle(), playlistName);
	}

	/***
	 * 
	 * @return list of songs which in their name of artist or title contain the
	 *         string given as input from the client
	 * 
	 */
	private List<Song> findSongsContainingGivenSongTokens(String song, ByteBuffer buffer, SocketChannel socketChannel,
			Selector selector) {
		Search searchCommand = new Search();
		searchCommand.execute(song, buffer, socketChannel, selector);

		return searchCommand.getFoundSongs();
	}

	private List<Playlist> findPlaylistsByName(String playlistName) {
		return PlaylistJsonMethodsHolder.playlists
				.stream()
				.filter(playlist -> playlist.getNamePlaylist().equals(playlistName))
				.collect(Collectors.toList());
	}

	private List<Playlist> findPlaylistsNotEqualToName(String playlistName) {
		return PlaylistJsonMethodsHolder.playlists
				.stream()
				.filter(playlist -> !playlist.getNamePlaylist().equals(playlistName))
				.collect(Collectors.toList());
	}

}
