package cardgames.server;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerOutputPackage implements Serializable {
	private static final long serialVersionUID = 743574295289854966L;
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
	
	@Override
	public String toString() {
		if(data == null) return command.toString();
		return command.toString()+" with payload{"+data.toString()+"}";
	}
}
