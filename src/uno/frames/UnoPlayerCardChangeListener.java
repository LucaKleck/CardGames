package uno.frames;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class UnoPlayerCardChangeListener implements PropertyChangeListener {
	private UnoPlayerPanel playerHandPanel;
	
	public UnoPlayerCardChangeListener(UnoPlayerPanel playerHandPanel) {
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
