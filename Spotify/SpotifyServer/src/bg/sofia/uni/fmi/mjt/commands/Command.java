package bg.sofia.uni.fmi.mjt.commands;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.data.User;

public abstract class Command {

	protected String message;
	protected ByteBuffer buffer;
	protected SocketChannel socketChannel;
	protected Selector selector;
	protected User user;

	public abstract String execute(String message, ByteBuffer buffer, SocketChannel socketChannel, Selector selector);

	protected User getKeyByValueFromMap(Map<User, SocketChannel> map, SocketChannel value) {
		for (User user : map.keySet()) {
			if (map.get(user).equals(value)) {
				return user;
			}
		}
		return null;
	}
}
