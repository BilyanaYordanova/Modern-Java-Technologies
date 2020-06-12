package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import bg.sofia.uni.fmi.mjt.data.User;

class StopTest {

	private static Stop stop;

	@BeforeAll
	public static void init() {
		stop = new Stop();
	}
	
	@Test
	void testStopCommandWhenUserIsNotLoggedInThePlatform() {
		String actual = stop.execute("song", null, null, null);
		String expected = "It seems that you are not logged in the platform. Please, login first so you can stop a song ";

		assertEquals(expected, actual, "Stop command should be executed only from users logged in the platform ");
	}
	
	@Test
	void testStopCommandWithArguments() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = stop.execute("args", null, socketChannel, null);
		String expected = "Stop command should have no arguments ";

		assertEquals(expected, actual, "Stop command should be executed with no arguments ");
	}

	@Test
	void testStopCommandWhenNoSongIsPlaying() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"tests" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = stop.execute("", null, socketChannel, null);
		String expected = "There seems to be no song playing ";

		assertEquals(expected, actual);
	}
}
