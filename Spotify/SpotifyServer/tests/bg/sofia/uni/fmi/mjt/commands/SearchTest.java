package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SearchTest {

	private static Search search;

	@BeforeAll
	public static void init() {
		search = new Search();
	}

	@Test
	void testSearchWhenNoArgumentsAreGiven() {
		String actual = search.execute("", null, null, null);
		String expected = "Type song artists or song title to search for ";

		assertEquals(expected, actual, "Search command should have at least one argument ");
	}

	@Test
	void testSearchWhenNoSongsWereFound() {
		String actual = search.execute("args", null, null, null);
		String expected = "No songs were found. Try again ";

		assertEquals(expected, actual, "Search command should be executed with valid song name ");
	}

	@Test
	void testSearchForSongWhichCanBeFound() {
		String actual = search.execute("Lady Gaga - Shallow", null, null, null);
		String expected = "Lady Gaga & Bradley Cooper - Shallow";

		assertEquals(expected, actual);
	}

}
