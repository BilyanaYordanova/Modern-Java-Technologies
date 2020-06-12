package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import bg.sofia.uni.fmi.mjt.data.User;
import bg.sofia.uni.fmi.mjt.holders.PlaylistJsonMethodsHolder;

class ShowPlaylistTest {

	private static ShowPlaylist showPlaylist;

	@BeforeAll
	public static void init() {
		showPlaylist = new ShowPlaylist();
	}

	@Test
	void testShowPlaylistWhenUserIsNotLoggedInThePlatform() {
		String actual = showPlaylist.execute("playlist", null, null, null);
		String expected = "It seems that you are not logged in the platform. Please, login first so you can use show-playlist command ";

		assertEquals(expected, actual,
				"Show playlist command should be executed only from users logged in the platform ");
	}

	@Test
	void testShowPlaylistWithInvalidNumberOfArguments() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = showPlaylist.execute("playlist 1 2", null, socketChannel, null);
		String expected = "Valid playlist name should be only one word. Please try again ";

		assertEquals(expected, actual, "Show playlist command should be executed with only one argument ");
	}

	@Test
	void testShowPlaylistWhenUserHasNoPlaylists() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));
		
		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = showPlaylist.execute("playlist4", null, socketChannel, null);
		String expected = "You have no playlists. You can create one using command: create-playlist <name_of_the_playlist>";

		assertEquals(expected, actual,
				"Show playlist command should be executed when user has at least one playlist ");
	
		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}

	@Test
	void testShowPlaylistWhenPlaylistDoesNotExist() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		CreatePlaylist createPlaylist = new CreatePlaylist();
		createPlaylist.execute("playlist1", null, socketChannel, null);

		String actual = showPlaylist.execute("playlist2", null, socketChannel, null);
		String expected = "Playlist with name <playlist2> doesn't exist ";

		assertEquals(expected, actual,
				"Show playlist command should be executed when given as argument playlist exists ");

		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}
	
	@Test
	void testShowPlaylistWhenPlaylistExists() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		CreatePlaylist createPlaylist = new CreatePlaylist();
		createPlaylist.execute("playlist3", null, socketChannel, null);
		
		String actual = showPlaylist.execute("playlist3", null, socketChannel, null);
		String expected = "Playlist name: playlist3 \nSongs in playlist: \n";
				
		assertEquals(expected, actual);
 
		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}
	
	@AfterEach
	private void cleanUp() {
		Login.currentlyLoggedUsers.clear();
		PlaylistJsonMethodsHolder.playlists.clear();
	}
}
