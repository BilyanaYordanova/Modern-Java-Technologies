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

class CreatePlaylistTest {

	private static CreatePlaylist createPlaylist;

	@BeforeAll
	public static void init() {
		createPlaylist = new CreatePlaylist();
	}

	@Test
	void testCreatePlaylistWhenUserIsNotLoggedInThePlatform() {
		String actual = createPlaylist.execute("playlist", null, null, null);
		String expected = "It seems that you are not logged in the platform. Please, login first so you can create a playlist ";

		assertEquals(expected, actual,
				"Create playlist command should be executed only from users logged in the platform ");
	}

	@Test
	void testCreatePlaylistWhenMoreThanOneWordIsGivenAsArgument() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"src" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = createPlaylist.execute("playlist 1 2", null, socketChannel, null);
		String expected = "Please enter only one word for playlist name ";

		assertEquals(expected, actual, "Create playlist command should have only one argument ");
	}

	@Test
	void testCreatePlaylistWhenValidArgumentIsGiven() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = createPlaylist.execute("playlist", null, socketChannel, null);
		String expected = "Playlist <playlist> was successfully created";

		assertEquals(expected, actual);
		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}

	@Test
	void testCreatePlaylistWhenPlaylistAlreadyExists() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		createPlaylist.execute("playlist1", null, socketChannel, null);
		String actual = createPlaylist.execute("playlist1", null, socketChannel, null);
		String expected = "Playlist with name <playlist1> already exists ";

		assertEquals(expected, actual, "Playlists should have unique names ");
		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}

	@AfterEach
	private void clearCollectionOfCurrentlyLoggedUsers() {
		Login.currentlyLoggedUsers.clear();
	}
}
