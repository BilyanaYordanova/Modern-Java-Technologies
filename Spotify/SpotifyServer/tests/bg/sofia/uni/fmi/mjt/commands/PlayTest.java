package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import bg.sofia.uni.fmi.mjt.data.User;

class PlayTest {

	private static Play play;

	@BeforeAll
	public static void init() {
		play = new Play();
	}

	@Test
	void testPlayCommandWhenUserIsNotLoggedInThePlatform() {
		String actual = play.execute("song", null, null, null);
		String expected = "It seems that you are not logged in the platform. Please, login first so you can play a song ";

		assertEquals(expected, actual, "Play command should be executed only from users logged in the platform ");
	}
 
	@Test
	void testPlayCommandWithInvalidNameOfSong() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = play.execute("gaga uptown", null, socketChannel, null);
		String expected = "There seems to be more than one song which contains this word/s <gaga uptown>. Please try again ";

		assertEquals(expected, actual, "Play command should be executed with valid name of song ");
	}


	@Test
	void testPlayCommandWhenNoSongsWereFound() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = play.execute("args", null, socketChannel, null);
		String expected = "No songs were found. Please try again ";

		assertEquals(expected, actual, "Play command should be executed with valid name of song ");
	}

	@AfterEach
	private void clearCollectionOfCurrentlyLoggedUsers() {
		Login.currentlyLoggedUsers.clear();
	}

}
