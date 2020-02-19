package bg.sofia.uni.fmi.mjt.commands;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.threads.SongStream;

public class Stop extends Command {

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		this.user = getKeyByValueFromMap(Login.currentlyLoggedUsers, socketChannel);
		if (user == null) {
			return "It seems that you are not logged in the platform. Please, login first so you can stop a song ";
		}

		if (!message.isEmpty()) {
			return "Stop command should have no arguments ";
		}

		if (!SongStream.mapSocketChannelToPlayingSongData.get(socketChannel).getIsPlaying()) {
			return "There seems to be no song playing ";
		}

		SongStream.mapSocketChannelToPlayingSongData.get(socketChannel).setStopSong(true);

		return "";
	}
}
