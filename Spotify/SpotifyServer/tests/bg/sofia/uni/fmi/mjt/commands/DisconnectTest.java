package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.User;

class DisconnectTest {
 
	private static Disconnect disconnect;

	@BeforeAll
	public static void init() {
		disconnect = new Disconnect();
	}

	@Test
	void testDisconnectWithArguments() {
		String actual = disconnect.execute("args", null, null, null);
		String expected = "Disconnect command should have no arguments. Try again ";

		assertEquals(expected, actual, "Disconnect command should have no arguments ");
	}

	@Test
	void testDisconnectingUserFromServer() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"src" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));
		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);

		Login.currentlyLoggedUsers.put(user, socketChannel);

		String actual = disconnect.execute("", null, socketChannel, null);
		String expected = Constants.DISCONNECTED_MESSAGE;

		assertEquals(expected, actual);
	}

	@Test
	void testRemovingUserFromCurrentlyLoggedUsersCollectionWhenDisconnecting() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"src" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));
		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);

		Login.currentlyLoggedUsers.put(user, socketChannel);

		disconnect.execute("", null, socketChannel, null);
		Login.currentlyLoggedUsers.remove(user);

		assertFalse(Login.currentlyLoggedUsers.containsKey(user), "After disconnecting user from server user data shouldn't be kept in the collection of curretly logged users ");
	}
}
