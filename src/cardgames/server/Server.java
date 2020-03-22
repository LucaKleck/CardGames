package cardgames.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cardgames.client.Client;
import cardgames.constants.CardGamesConstants;
import cardgames.games.Game;
import cardgames.games.GameFactory;

public class Server implements Runnable {
	private ServerInfo serverInfo;

	private ExecutorService serverExecutorService;
	private ServerSocket s;
	private ArrayList<ClientHandler> clientHandlers;
	
	private Game game;
	
	
	/**
	 * @param serverName
	 * @throws IOException
	 * The server contains: <br>
	 *  1. Client connections and Client info <br>
	 *  2. {@link ServerInfo} Server Info <br>
	 *  3. Game board with all relevant info
	 */
	public Server(String serverName) throws IOException {
		// Server data
		serverInfo = new ServerInfo(serverName);
		// Start of server
		serverExecutorService = Executors.newSingleThreadExecutor();
		s = new ServerSocket(CardGamesConstants.PORT);
		clientHandlers = new ArrayList<ClientHandler>();
		serverExecutorService.execute(this);
	}
	
	public synchronized ArrayList<Client> getClients() {
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ClientHandler c : clientHandlers) {
			clients.add(c.getClient());
		}
		return clients;
	}
	/**
	 * Gets all clients with ClientRole cr
	 * @param cr
	 * @return
	 */
	public synchronized ArrayList<Client> getClients(ClientRole cr) {
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ClientHandler c : clientHandlers) {
			if(cr == c.getClient().getRole()) clients.add(c.getClient());
		}
		return clients;
	}
	
	
	public synchronized Game getGame() {
		return game;
	}
	
	public synchronized ServerInfo getServerInfo() {
		return serverInfo;
	}
	
	public boolean startGame() {
		game = GameFactory.createGame(serverInfo.getGameMode(), this, null,  null);
		for(ClientHandler clientHandler : clientHandlers) {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(game.createSynchData(clientHandler.getClient()));
			sendServerOutputPackage(new ServerOutputPackage(ServerCommands.START_GAME, data), clientHandler.getClient());
		}
		return true;
	}
	
	public void run() {
		while (true) {
			try {
				// Accept and create new client handler
				Socket clientSocket = s.accept();
				ClientHandler newClient = new ClientHandler(clientSocket, this);
				clientHandlers.add(newClient);
				// broadcast new arrival
				ArrayList<Object> data;
				data = new ArrayList<Object>();
				data.add(getClients());
				broadcastServerOutputPackage(new ServerOutputPackage(ServerCommands.CLIENT_LIST, data));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends to every Client in client target list
	 * @param serverOutputPackage
	 */
	public void multicastServerOutputPackage(ServerOutputPackage serverOutputPackage, ArrayList<Client> clientTargetList) {
		for(Client c : clientTargetList) {
			sendServerOutputPackage(serverOutputPackage, c);
		}
	}
	
	/**
	 * Sends to every Client
	 * @param serverOutputPackage
	 */
	public void broadcastServerOutputPackage(ServerOutputPackage serverOutputPackage) {
		for(ClientHandler clientHandler : clientHandlers) {
			clientHandler.sendServerOutputPackage(serverOutputPackage);
		}
	}
	
	/**
	 * Sends to every Client except origin
	 * @param serverOutputPackage
	 */
	public void broadcastServerOutputPackage(ServerOutputPackage serverOutputPackage, Client origin) {
		for(ClientHandler clientHandler : clientHandlers) {
			if(clientHandler.getClient().equals(origin)) continue;
			clientHandler.sendServerOutputPackage(serverOutputPackage);
		}
	}
	
	public synchronized void sendServerOutputPackage(ServerOutputPackage serverOutputPackage, Client target) {
		for(ClientHandler clientHandler : clientHandlers) {
			if(clientHandler.getClient().equals(target)) {
				clientHandler.sendServerOutputPackage(serverOutputPackage);
			}
		}
	}
	
}
