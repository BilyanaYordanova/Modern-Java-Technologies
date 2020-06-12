package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RegisterTest {

	private static Register register;

	@BeforeAll
	public static void init() {
		register = new Register();
		register.setPath(Paths
				.get("tests" + File.separator + "resources" + File.separator + "data" + File.separator + "users.txt"));
	}

	@Test
	void testRegisterWithMoreThanEnoughArguments() {
		String actual = register.execute("email password notNeededArgument", null, null, null);
		String expected = "Invalid number of arguments received. Type email and password";

		assertEquals(expected, actual, "Register command should have only two arguments ");
	}

	@Test
	void testRegisterWithLessThanEnoughArguments() {
		String actual = register.execute("email ", null, null, null);
		String expected = "Invalid number of arguments received. Type email and password"; 

		assertEquals(expected, actual, "Register command should have only two arguments ");
	}

	@Test
	void testRegisterWithEmailContainingInvalidFileNameChars() {
		String actual = register.execute("email* password", null, null, null);
		String expected = "The email you have typed contains invalid characters. Please, type valid email ";

		assertEquals(expected, actual, "The email used for register should contain only valid file name characters ");
	}

	@Test
	void testRegisterUserWhenEmailAndPasswordAreValid() {
		String actual = register.execute("email2 password", null, null, null);
		String expected = "User with email <email2> was successfully registered ";

		assertEquals(expected, actual);
	}

	@Test
	void testRegisterUsersWithSameEmails() {
		String actual = register.execute("email1 password", null, null, null);
		actual = register.execute("email1 password", null, null, null);
		String expected = "User with email <email1> already exists ";

		assertEquals(expected, actual, "User can't register with email which is already registered in the platform ");
	}
	
	@AfterAll
	public static void cleanUp() {
		try {
			Files.delete(register.getPath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete users file ");
		}
	}
}
