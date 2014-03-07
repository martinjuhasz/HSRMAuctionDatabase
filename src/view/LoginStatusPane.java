/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class LoginStatusPane extends JPanel {

	private ModelManager modelManager;
	private JLabel userNameLabel;
	private JButton logoutButton;
	
	/**
	 * Instantiates a new login status pane.
	 *
	 * @param mManager the m manager
	 */
	public LoginStatusPane(ModelManager mManager) {
		
		setLayout(new MigLayout("rtl"));
		
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
		
		logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				modelManager.logout();
				
			}
		});
		add(logoutButton, "gapleft push");
		
		userNameLabel = new JLabel();
		add(userNameLabel, "gapleft push");
		
		refresh();
	}

	/**
	 * Refresh.
	 */
	public void refresh() {
		userNameLabel.setText(modelManager.getLoginUserName());
		logoutButton.setEnabled(modelManager.isLoggedIn());
	}
}
