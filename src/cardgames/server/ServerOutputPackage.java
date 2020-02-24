package cardgames.server;

import java.util.ArrayList;

public class ServerOutputPackage {
	private ServerCommands command;
	private ArrayList<Object> data;

	// for GameAction data will be GameAction Object with command gameAction
	
	public ServerOutputPackage(ServerCommands command, ArrayList<Object> data) {
		this.command = command;
		this.data = data;
	}
	
	public ServerCommands getCommand() {
		return command;
	}

	public ArrayList<Object> getData() {
		return data;
	}
}
