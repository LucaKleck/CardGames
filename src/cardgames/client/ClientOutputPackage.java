package cardgames.client;

import java.util.ArrayList;

public class ClientOutputPackage {
	private ClientCommands command;
	private ArrayList<Object> data;

	// for GameAction data will be GameAction Object with command gameAction
	
	public ClientOutputPackage(ClientCommands command, ArrayList<Object> data) {
		this.command = command;
		this.data = data;
	}

	public ClientCommands getCommand() {
		return command;
	}

	public ArrayList<Object> getData() {
		return data;
	}
}
