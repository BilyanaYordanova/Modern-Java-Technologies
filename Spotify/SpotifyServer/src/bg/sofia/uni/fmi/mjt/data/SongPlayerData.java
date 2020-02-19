package bg.sofia.uni.fmi.mjt.data;

public class SongPlayerData {
	
	private Boolean isPlaying;
	private Boolean stopSong;

	public SongPlayerData(Boolean isPlaying, Boolean stopSong) {
		super();
		this.isPlaying = isPlaying;
		this.stopSong = stopSong;
	}

	public Boolean getStopSong() {
		return stopSong;
	}

	public void setStopSong(Boolean stopSong) {
		this.stopSong = stopSong;
	}

	public Boolean getIsPlaying() {
		return isPlaying;
	}

	public void setIsPlaying(Boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

}
