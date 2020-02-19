package bg.sofia.uni.fmi.mjt.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.google.gson.Gson;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.data.AudioFormatDataHolder;
import bg.sofia.uni.fmi.mjt.holders.FileMethodsHolder;

public class SpotifyClientThreadReader implements Runnable {

	private ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
	private SocketChannel socketChannel;

	public SpotifyClientThreadReader(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		try {
			while (true) {
				buffer.clear();
				if (socketChannel.read(buffer) == -1) {
					System.out.println("=> Disconnected from server");
					break;
				}
				buffer.flip();

				String serverReply = new String(buffer.array(), 0, buffer.limit());

				if (serverReply.contains("stream")) {
					String[] serverReplyLines = serverReply.split("\n", 3);
					String audioFormatHolderInJson = serverReplyLines[1];
					String songPlayingMessage = serverReplyLines[2];
					
					System.out.println("=> " + songPlayingMessage.trim());
					playSong(audioFormatHolderInJson);

				} else {
					System.out.print("=> ");
					System.out.print(serverReply + "\n");
				}
			}
		} catch (IOException e) {
			String exceptionMessage =
					"Failed reading data received from server when using socket channel ";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	private void playSong(String audioFormatInJson) {
		AudioFormat audioFormat = getAudioFormat(audioFormatInJson);

		try {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open();
			dataLine.start();

			streamSong(dataLine);

			dataLine.drain();
			dataLine.stop();
			dataLine.close();

		} catch (LineUnavailableException e) {
			String exceptionMessage = 
					"Failed getting SourceDataLine when executing play command";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	private void streamSong(SourceDataLine dataLine) {
		try {
			int readFromBuffer = 0;
			while (true) {
				buffer.clear();
				readFromBuffer = socketChannel.read(buffer);
				buffer.flip();

				if (readFromBuffer <= -1) {
					break;
				}

				String serverReply = new String(buffer.array(), 0, buffer.limit());
				if (serverReply.contains("end")) {
					System.out.println("The song ended. You can type another command to be executed ");
					break;
				}
				if (serverReply.contains("stop")) {
					System.out.println("The song was stopped. You can type another command to be executed ");
					break;
				}
				dataLine.write(buffer.array(), 0, readFromBuffer);

			}
		} catch (IOException e) {
			String exceptionMessage = 
					"Failed reading data for streaming song while play command is executing";
			FileMethodsHolder.saveExceptionsToFile(exceptionMessage);
			throw new RuntimeException(exceptionMessage);
		}
	}

	private AudioFormat getAudioFormat(String audioFormatInJson) {
		Gson gson = new Gson();
		AudioFormatDataHolder holder = gson.fromJson(audioFormatInJson, AudioFormatDataHolder.class);

		AudioFormat audioFormat = new AudioFormat(new Encoding(holder.getEncoding()), holder.getSampleRate(),
				holder.getSampleSizeInBits(), holder.getChannels(), holder.getFrameSize(), holder.getFrameRate(),
				holder.isBigEndian());

		return audioFormat;
	}
}
