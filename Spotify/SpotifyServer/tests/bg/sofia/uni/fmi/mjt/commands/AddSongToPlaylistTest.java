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

class AddSongToPlaylistTest {

	private static AddSongToPlaylist addSongToPlaylist;

	@BeforeAll
	public static void init() {
		addSongToPlaylist = new AddSongToPlaylist();
	}

	@Test
	void testAddSongToPlaylistWhenUserIsNotLoggedInThePlatform() {
		String actual = addSongToPlaylist.execute("song", null, null, null);
		String expected = "It seems that you are not logged in the platform. Please, login first so you can add a song to playlist ";

		assertEquals(expected, actual,
				"Add song to playlist command should be executed only from users logged in the platform ");
	}

	@Test
	void testAddSongToPlaylistWithInvalidNumberOfArguments() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = addSongToPlaylist.execute("playlist", null, socketChannel, null);
		String expected = "Not enough arguments entered. Please type playlist name and song to add ";

		assertEquals(expected, actual, "Add song to playlist command should be executed with two or more arguments ");
	}

	@Test
	void testAddSongToPlaylistWhenMoreThanOneSongHasBeenFound() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = addSongToPlaylist.execute("playlist lady gaga uptown", null, socketChannel, null);
		String expected = "There seems to be more than one song which contains this word/s <lady gaga uptown>. Please try again ";

		assertEquals(expected, actual, "Add song to playlist command should be executed with valid song name ");
	}

	@Test
	void testAddSongToPlaylistWhenNoSongsWereFound() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = addSongToPlaylist.execute("playlist args", null, socketChannel, null);
		String expected = "No songs were found. Please try again ";

		assertEquals(expected, actual, "Add song to playlist command should be executed with valid song name ");
	}

	@Test
	void testAddSongToPlaylistWhenUserHasNoPlaylists() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = addSongToPlaylist.execute("playlist lady gaga", null, socketChannel, null);
		String expected = "You have no playlists. Create one playlist so you can add songs to it";

		assertEquals(expected, actual,
				"Add song to playlist command should be executed when user has at least one playlist ");
	}

	@Test
	void testAddSongToPlaylistWhenPlaylistDoesNotExist() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		CreatePlaylist createPlaylist = new CreatePlaylist();
		createPlaylist.execute("playlist1", null, socketChannel, null);

		String actual = addSongToPlaylist.execute("playlist2 lady gaga", null, socketChannel, null);
		String expected = "Playlist with name <playlist2> doesn't exist ";

		assertEquals(expected, actual,
				"Add song to playlist command should be executed when given as argument playlist exists ");

		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}

	@Test
	void testAddSongToPlaylistWhenPlaylistExistsAndSongHasNotBeenAdded() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		CreatePlaylist createPlaylist = new CreatePlaylist();
		createPlaylist.execute("playlist3", null, socketChannel, null);

		String actual = addSongToPlaylist.execute("playlist3 lady gaga", null, socketChannel, null);
		String expected = "Song <Lady Gaga & Bradley Cooper - Shallow> was successfully added to playlist with name <playlist3> ";
		assertEquals(expected, actual);

		try {
			Files.delete(user.getPlaylistsFilePath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete playlist file ");
		}
	}

	@Test
	void testAddSongToPlaylistWhenPlaylistExistsAndSongHasAlreadyBeenAdded() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		CreatePlaylist createPlaylist = new CreatePlaylist();
		createPlaylist.execute("playlist4", null, socketChannel, null);

		addSongToPlaylist.execute("playlist4 lady gaga", null, socketChannel, null);
		String actual = addSongToPlaylist.execute("playlist4 lady gaga", null, socketChannel, null);
		String expected = "Song <Lady Gaga & Bradley Cooper - Shallow> has already been added to playlist with name <playlist4> ";
		assertEquals(expected, actual,
				"Add song to playlist command should be executed when given song hasn't already been added to the playlist ");

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
