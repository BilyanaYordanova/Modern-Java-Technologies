package bg.sofia.uni.fmi.mjt.data;

import java.util.Objects;

public class Song {

	private String artists;
	private String songTitle;
	private String filePath;

	public Song(String artists, String songTitle, String filePath) {
		this.artists = artists;
		this.songTitle = songTitle;
		this.filePath = filePath;
	}

	public String getArtists() {
		return artists;
	}

	public void setArtists(String artists) {
		this.artists = artists;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(artists, filePath, songTitle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Song other = (Song) obj;
		return Objects.equals(artists, other.artists) && Objects.equals(filePath, other.filePath)
				&& Objects.equals(songTitle, other.songTitle);
	}

	@Override
	public String toString() {
		return "artists: " + artists + ", song title: " + songTitle + "\n";
	}

}
