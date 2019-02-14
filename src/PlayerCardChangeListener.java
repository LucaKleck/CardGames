import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlayerCardChangeListener implements PropertyChangeListener {
	private PlayerPanel playerHandPanel;
	
	public PlayerCardChangeListener(PlayerPanel playerHandPanel) {
		this.playerHandPanel = playerHandPanel;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().matches("selectedCardFlag")) {
			if(((Boolean) evt.getNewValue()).booleanValue() == true) {
				playerHandPanel.updateField();
			}
		}
	}

}
