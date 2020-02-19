package bg.sofia.uni.fmi.mjt.threads;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.google.gson.Gson;

import bg.sofia.uni.fmi.mjt.data.AudioFormatDataHolder;
import bg.sofia.uni.fmi.mjt.data.Song;
import bg.sofia.uni.fmi.mjt.data.SongPlayerData;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;
import bg.sofia.uni.fmi.mjt.server.SpotifyServer;

public class SongStream implements Runnable {

	public static Map<SocketChannel, SongPlayerData> mapSocketChannelToPlayingSongData = new HashMap<>();
	private ByteBuffer buffer;
	private SocketChannel socketChannel;
	private Song songToPlay;

	public SongStream(ByteBuffer buffer, SocketChannel socketChannel, Song songToPlay) {
		this.buffer = ByteBuffer.allocate(1024);
		this.socketChannel = socketChannel;
		this.songToPlay = songToPlay;
		mapSocketChannelToPlayingSongData.put(socketChannel, new SongPlayerData(false, false));
	}

	@Override
	public void run() {
		Path songToPlayPath = Paths.get(songToPlay.getFilePath());
		try {
			AudioFormat format = AudioSystem.getAudioInputStream(
					new File(songToPlay.getFilePath())).getFormat();
			
			synchronized (socketChannel) {
				play(format, songToPlayPath, songToPlay);
			}

		} catch (UnsupportedAudioFileException e) {
			throw new RuntimeException(
					"UnsupportedAudioFileException thrown trying to stream song when executing play command ");
		} catch (IOException e) {
			throw new RuntimeException("IOException thrown trying to stream song when executing play command");
		}

	}

	private void play(AudioFormat format, Path songToPlayPath, Song songToPlay) {
		writeAudioFormatDataToBuffer(format, songToPlay);
		streamSong(songToPlayPath);
	}

	private void writeAudioFormatDataToBuffer(AudioFormat format, Song songToPlay) {
		AudioFormatDataHolder holder = new AudioFormatDataHolder(format.getChannels(), format.getEncoding().toString(),
				format.getFrameRate(), format.getFrameSize(), format.getSampleRate(), format.getSampleSizeInBits(),
				format.isBigEndian());

		Gson gson = new Gson();
		String holderInJson = gson.toJson(holder);

		buffer.clear();
		buffer.put("stream \n".getBytes());
		buffer.put(holderInJson.getBytes());
		buffer.put("\n".getBytes());
		buffer.put(
				String.format("Song <%s> is playing... \n", songToPlay.getArtists() + " - " + songToPlay.getSongTitle())
						.getBytes());
		buffer.flip();

		try {
			socketChannel.write(buffer);
			buffer.clear();
		} catch (IOException e) {
			throw new RuntimeException(
					"IOException thrown trying to write buffer data to socket channel when executing play command ");
		}
	}

	private void streamSong(Path songToPlayPath) {
		try (AudioInputStream stream = AudioSystem.getAudioInputStream(new File(songToPlayPath.toString()))) {
			byte[] data = new byte[1024];
			int bytesRead = 0;
			mapSocketChannelToPlayingSongData.get(socketChannel).setIsPlaying(true);
			SpotifyServer.mapSongToNumberListens.get(songToPlay).incrementAndGet();
			
			while (true) {
				bytesRead = stream.read(data, 0, data.length);
				if (bytesRead == -1) {
					break;
				}

				if (mapSocketChannelToPlayingSongData.get(socketChannel).getStopSong()) {
					buffer.clear();
					buffer.put("stop".getBytes());
					buffer.flip();
					socketChannel.write(buffer);
					return;
				}

				buffer.put(data);
				buffer.flip();

				socketChannel.write(buffer);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					throw new RuntimeException("InterruptedException when trying to call Thread.sleep() method ");
				}
				buffer.clear();
			}

			mapSocketChannelToPlayingSongData.get(socketChannel).setIsPlaying(false);

			buffer.put("\nend".getBytes());
			buffer.flip();
			socketChannel.write(buffer);
			buffer.clear();
			
		} catch (IOException e1) {
			String exceptionMessage = 
					"Failed writing data to socket channel while streaming song to client";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		} catch (UnsupportedAudioFileException e2) {
			String exceptionMessage = 
					"Failed getting audio input stream while streaming song to client";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}
}
