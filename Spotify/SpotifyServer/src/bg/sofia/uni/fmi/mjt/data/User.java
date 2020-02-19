package bg.sofia.uni.fmi.mjt.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

	private String email;
	private int password;
	private List<Playlist> playlists;
	private Path playlistsFilePath;

	public User(String email, int password, Path playlistsFilePath) {
		this.email = email;
		this.password = password;
		playlists = new ArrayList<Playlist>();
		this.playlistsFilePath = playlistsFilePath;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPassword() {
		return password;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public List<Playlist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(List<Playlist> playlists) {
		this.playlists = playlists;
	}

	public Path getPlaylistsFilePath() {
		return playlistsFilePath;
	}

	public void setPlaylistsFilePath(Path playlistsFilePath) {
		this.playlistsFilePath = playlistsFilePath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, password);
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
		User other = (User) obj;
		return Objects.equals(email, other.email) && password == other.password;
	}

	@Override
	public String toString() {
		return "User [email=" + email + ", password=" + password + "]";
	}

}
