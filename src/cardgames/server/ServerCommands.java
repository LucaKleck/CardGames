package cardgames.server;

// ServerCommands -> sent to client
public enum ServerCommands {
	// send data
	GAME_DATA,		// send game data to client
	KICK, 			// send kick to client
	BAN, 			// send ban to client
	CHAT_MESSAGE,	// send chat message from someone else
	SHUTDOWN, 		// send shutdown to client
	START_GAME,  	// send game start and game type (make clients start fake game)
	SERVER_MESSAGE,	// send server information as chat message
	SERVER_INFO
	// ask for data
	
	;
}
