import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton btnJoinAsClient;
	private JButton btnStartAsHost;

	public MainMenuPanel(MainJFrame container) {
		
		JTextField txtUsername = new JTextField();
		txtUsername.setText("UserName");
		add(txtUsername);
		txtUsername.setColumns(10);
		
		btnStartAsHost = new JButton("Start As Host");
		btnStartAsHost.addActionListener(e -> {
			container.add(new UnoPlayingFieldPanel(new Player(txtUsername.getText()), false));
			container.remove(this);
			container.revalidate();
		});
		add(btnStartAsHost);
		
		btnJoinAsClient = new JButton("Join as Client");
		btnJoinAsClient.addActionListener(e -> {
			container.add(new UnoPlayingFieldPanel(new Player(txtUsername.getText()), true ));
			container.remove(this);
			container.revalidate();
		});
		add(btnJoinAsClient);
	}
	
}
