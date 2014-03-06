package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerAdapter;

public class LoginStatusPane extends JPanel {

	private ModelManager modelManager;
	private JLabel userNameLabel;
	private JButton logoutButton;
	
	public LoginStatusPane(ModelManager mManager) {
		
		setLayout(new MigLayout());
		
		this.modelManager = mManager;
		this.modelManager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void userDidLogin(ModelManager manager) {
				refresh();
			}
			
			@Override
			public void userDidLogout(ModelManager manager) {
				refresh();
			}
		});
		
		userNameLabel = new JLabel();
		add(userNameLabel, "gapleft push");
		
		logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				modelManager.logout();
				
			}
		});
		add(logoutButton, "gapleft push");
		
		refresh();
	}

	public void refresh() {
		userNameLabel.setText(modelManager.getLoginUserName());
		logoutButton.setEnabled(modelManager.isLoggedIn());
	}
}