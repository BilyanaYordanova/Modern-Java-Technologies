package bg.sofia.uni.fmi.mjt.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SongsDatabase {

	public static Map<Song, AtomicInteger> getSongsCollection() {
		Map<Song, AtomicInteger> mapSongToNumberListens = new HashMap<>();

		mapSongToNumberListens.put(
				createSongInstance("Lady Gaga & Bradley Cooper", "Shallow", "Lady Gaga, Bradley Cooper - Shallow.wav"),
				new AtomicInteger(0));

		mapSongToNumberListens.put(createSongInstance("Mark Ronson & Bruno Mars", "Uptown Funk",
				"Mark Ronson, Bruno Mars - Uptown Funk.wav"), new AtomicInteger(0));

		mapSongToNumberListens.put(
				createSongInstance("Michael Jackson", "Hollywood Tonight", "Michael Jackson - Hollywood Tonight.wav"),
				new AtomicInteger(0));

		mapSongToNumberListens.put(createSongInstance("Nickelback", "Rockstar", "Nickelback - Rockstar.wav"),
				new AtomicInteger(0));

		mapSongToNumberListens.put(createSongInstance("Pharrell Williams", "Happy", "Pharrell Williams - Happy.wav"),
				new AtomicInteger(0));

		mapSongToNumberListens.put(createSongInstance("Robin Schulz & Richard Judge", "Show me love",
				"Robin Schulz, Richard Judge - Show me love.wav"), new AtomicInteger(0));

		mapSongToNumberListens.put(createSongInstance("Sam Smith", "Fire on Fire", "Sam Smith - Fire on Fire.wav"),
				new AtomicInteger(0));

		return mapSongToNumberListens;
	}

	private static Song createSongInstance(String artists, String songTitle, String fileName) {
		return new Song(artists, songTitle,
				"src" + File.separator + "resources" + File.separator + "songs" + File.separator + fileName);
	}
}
