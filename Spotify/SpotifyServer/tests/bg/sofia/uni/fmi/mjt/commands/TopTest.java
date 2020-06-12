package bg.sofia.uni.fmi.mjt.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.server.SpotifyServer;

class TopTest {

	private static Top top;

	@BeforeAll
	public static void init() {
		top = new Top();
	}

	@Test
	void testTopWithNoArguments() {
		String actual = top.execute("", null, null, null);
		String expected = "Invalid number of arguments received. Type number of top songs to be shown ";

		assertEquals(expected, actual, "Top command should have only one argument ");
	}

	@Test
	void testTopWithMoreThanOneArgument() {
		String actual = top.execute("6 7", null, null, null);
		String expected = "Invalid number of arguments received. Type only one number of top songs to be shown ";

		assertEquals(expected, actual, "Top command should have only one argument ");
	}
	
	@Test
	void testGettingTopSongs() {
		Search search = new Search();
		search.execute("Lady Gaga - Shallow", null, null, null);
		List<Song> foundSongs = search.getFoundSongs();
		Song songToPlay = foundSongs.get(0);
		
		int counter = SpotifyServer.mapSongToNumberListens.get(songToPlay).incrementAndGet();
		SpotifyServer.mapSongToNumberListens.remove(songToPlay);
		SpotifyServer.mapSongToNumberListens.put(songToPlay, new AtomicInteger(counter));
		
		String actual = top.execute("1", null, null, null);
		String expected = "Lady Gaga & Bradley Cooper - Shallow";
		assertEquals(expected, actual);
	}
}
