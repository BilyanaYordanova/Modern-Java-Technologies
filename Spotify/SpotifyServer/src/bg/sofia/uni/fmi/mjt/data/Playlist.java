package bg.sofia.uni.fmi.mjt.data;

import java.util.List;
import java.util.Objects;

public class Playlist {

	private String namePlaylist;
	private List<Song> songsInPlaylist;

	public Playlist(String namePlaylist, List<Song> songsInPlaylist) {
		super();
		this.namePlaylist = namePlaylist;
		this.songsInPlaylist = songsInPlaylist;
	}

	public String getNamePlaylist() {
		return namePlaylist;
	}

	public void setNamePlaylist(String namePlaylist) {
		this.namePlaylist = namePlaylist;
	}

	public List<Song> getSongsInPlaylist() {
		return songsInPlaylist;
	}

	public void setSongsInPlaylist(List<Song> songsInPlaylist) {
		this.songsInPlaylist = songsInPlaylist;
	}

	@Override
	public int hashCode() {
		return Objects.hash(namePlaylist, songsInPlaylist);
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
		Playlist other = (Playlist) obj;
		return Objects.equals(namePlaylist, other.namePlaylist)
				&& Objects.equals(songsInPlaylist, other.songsInPlaylist);
	}

	@Override
	public String toString() {
		return "Name of playlist: " + namePlaylist + ",\n" + "Songs in playlist: " + "\n" + songsInPlaylist;
	}
}
