package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bg.sofia.uni.fmi.mjt.data.User;

class LoginTest {

	private static Login login;

	@BeforeAll
	public static void init() {
		login = new Login();
	}

	@Test
	void testLoginWithMoreThanEnoughArguments() {
		String actual = login.execute("email password args", null, null, null);
		String expected = "Invalid number of arguments received. Type your email and password";

		assertEquals(expected, actual, "Login command should have only two arguments ");
	}

	@Test
	void testLoginWithLessThanEnoughArguments() {
		String actual = login.execute("email", null, null, null);
		String expected = "Invalid number of arguments received. Type your email and password";

		assertEquals(expected, actual, "Login command should have only two arguments ");
	}

	@Test
	void testLoginWithUserWhichIsAlreadyLogged() {
		User user = new User("email", "password".hashCode(), Paths.get(
				"src" + File.separator + "resources" + File.separator + "data" + File.separator + "email" + ".json"));

		Login.currentlyLoggedUsers.put(user, null);

		String actual = login.execute("email password", null, null, null);
		String expected = String.format("User with email <email> is already logged in the platform ");

		assertEquals(expected, actual, "User which is already logged in the platform can't login again with the same credentials ");
	}

	@Test
	void testLoginWhenUserIsNotRegistered() {
		String actual = login.execute("email1 password1", null, null, null);
		String expected = "User with email <email1> isn't registered. Register first so you can log in ";

		assertEquals(expected, actual, "User which hasn't been registered can't login in the platform ");
	}

}
