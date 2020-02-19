package bg.sofia.uni.fmi.mjt.commands;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.constants.Constants;

public class Disconnect extends Command {

	@Override
	public String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector) {
		if (!message.isEmpty()) {
			return "Disconnect command should have no arguments. Try again ";
		}

		if (!Login.currentlyLoggedUsers.isEmpty()) {
			Login.currentlyLoggedUsers.entrySet()
				.removeIf(entry -> entry.getValue().equals(socketChannel));
		}

		return Constants.DISCONNECTED_MESSAGE;
	}

}
