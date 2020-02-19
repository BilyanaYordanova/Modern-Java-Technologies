package bg.sofia.uni.fmi.mjt.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandsCollection {
 
	public static Map<String, Command> getCommandsCollection() {
		Map<String, Command> mapCommandNameToCommandInstance = new HashMap<>();

		mapCommandNameToCommandInstance.put("register", new Register());
		mapCommandNameToCommandInstance.put("login", new Login());
		mapCommandNameToCommandInstance.put("disconnect", new Disconnect());
		mapCommandNameToCommandInstance.put("search", new Search());
		mapCommandNameToCommandInstance.put("top", new Top());
		mapCommandNameToCommandInstance.put("create-playlist", new CreatePlaylist()); 
		mapCommandNameToCommandInstance.put("add-song-to", new AddSongToPlaylist());
		mapCommandNameToCommandInstance.put("show-playlist", new ShowPlaylist());
		mapCommandNameToCommandInstance.put("play", new Play());
		mapCommandNameToCommandInstance.put("stop", new Stop());
		
		return mapCommandNameToCommandInstance;
	}
}
