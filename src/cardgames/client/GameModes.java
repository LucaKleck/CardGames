package cardgames.client;

public enum GameModes {
	NO_GAME ( "No Game", (byte) 0),
	UNO ( "UNO", (byte) 1 );

	private final String modeName;
	/**
	 * Tells clients the game mode of the server <br>
	 * 0 = no game selected <br>
	 * 1 = UNO <br>
	 * 2-254 unassigned <br>
	 */
	private final Byte modeID;
	
	GameModes(String modeName, byte modeID) {
		this.modeName = modeName;
		this.modeID = modeID;
	}

	public String getModeName() {
		return modeName;
	}

	public Byte getModeID() {
		return modeID;
	}
	
}
