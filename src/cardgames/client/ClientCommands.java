package cardgames.client;

// ClientCommands -> sent to server
public enum ClientCommands {
	// send data
	GAME_ACTION, 		// send gameAction to server
	CHAT,				// send chat message to server
	SET_ROLE,			// send role
	// ask for data
	CONNECT, 			// ask for connection + send self
	RECONNECT, 			// ask for reconnection to saved player profile
	DISCONNECT, 		// ask server to remove client
	SERVER_INFO, 		// ask for serverInfo
	CLIENT_LIST,		// ask for client list
	;
}
