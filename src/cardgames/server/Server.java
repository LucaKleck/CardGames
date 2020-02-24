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
	
	public ArrayList<Client> getClients() {
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ClientHandler c : clientHandlers) {
			clients.add(c.getClient());
		}
		return clients;
	}
	
	public ArrayList<Client> getClients(ClientRole cr) {
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ClientHandler c : clientHandlers) {
			if(cr == c.getRole()) clients.add(c.getClient());
		}
		return clients;
	}
	
	
	public Game getGame() {
		return game;
	}
	
	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	
	public boolean startGame() {
		game = GameFactory.createGame(serverInfo.getGameMode(), this, null);
		if(game == null) return false;
		return true;
	}
	
	public void run() {
		while (true) {
			try {
				// Accept and create new client handler
				Socket clientSocket = s.accept();
				ClientHandler newClient = new ClientHandler(clientSocket, this);
				clientHandlers.add(newClient);
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
		for(ClientHandler clientHandler : clientHandlers) {
			for(Client c : clientTargetList) {
				if(c.equals(clientHandler.getClient())) {
					clientHandler.sendServerOutputPackage(serverOutputPackage);
				}
			}
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
	
	public void sendServerOutputPackage(ServerOutputPackage serverOutputPackage, Client target) {
		ArrayList<Client> clientTargetList = new ArrayList<Client>();
		clientTargetList.add(target);
		multicastServerOutputPackage(serverOutputPackage, clientTargetList);
	}
	
}
