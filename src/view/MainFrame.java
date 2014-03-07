/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class MainFrame extends JFrame {
	
	private ModelManager modelManager;
	private LoginStatusPane loginStatusPane;
	private JTabbedPane tabPane;
	private List<Tab> tabs;

	/**
	 * Instantiates a new main frame.
	 *
	 * @param modelManager the model manager
	 */
	public MainFrame(final ModelManager modelManager) {
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void userDidLogout(ModelManager manager) {
				tabPane.setVisible(false);
				showLogin();
			}
			
			@Override
			public void userDidLogin(ModelManager manager) {
				updateTabs();
				tabPane.setVisible(true);
			}
		});
		
		setLayout(new MigLayout("fill, insets 20", "", "[55!][]"));
		
		ImagePanel logoPanel = new ImagePanel("logo.gif", ImagePanel.SIZE_FILL);
		add(logoPanel, "w 128!, h 47!");
		
		loginStatusPane = new LoginStatusPane(modelManager);
		add(loginStatusPane, "growx, wrap");
		
		tabPane = new JTabbedPane();
		tabPane.setVisible(false);
		add(tabPane, "grow, spanx 2, gapy 10");
		
		tabs = new ArrayList<>();
		tabs.add(new Tab("Benutzer", new UserPane(modelManager), true));
		tabs.add(new Tab("Kategorien", new CategoriesPane(modelManager), true));
		tabs.add(new Tab("Auktionen", new AuctionPane(modelManager), false));
		tabs.add(new Tab("Suche", new SearchPane(modelManager), false));
		tabs.add(new Tab("Berichte", new ReportPane(modelManager), true));
		tabs.add(new Tab("Account", new AccountPane(modelManager), false));
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(950,650);
		
		showLogin();
	}
	
	/**
	 * Update tabs.
	 */
	private void updateTabs() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				tabPane.removeAll();
				for(Tab tab : tabs) {
					if (!tab.isRequireAdmin() || modelManager.isAdmin()) {
						tabPane.addTab(tab.getTitle(), tab.getPanel());
					}
				}
			}
		});
	}
	
	/**
	 * Show login.
	 */
	private void showLogin() {
		final MainFrame frame = this;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final LoginDialog loginD = new LoginDialog(frame, modelManager);
				loginD.setLoginCallback(new Callback() {
					
					@Override
					public void callback(int status) {
						if (status == LoginDialog.CALLBACK_STATUS_LOGIN) {
							if (!modelManager.isLoggedIn()) {
								System.exit(0);
							} else {
								loginD.dispose();
							}
						} else {
							showRegister();
						}
					}
				});
				loginD.setVisible(true);
			}
		});
	}
	
	/**
	 * Show register.
	 */
	public void showRegister() {
		final MainFrame frame = this;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final RegisterDialog registerD = new RegisterDialog(frame, modelManager);
				registerD.setRegisterCallback(new Callback() {
					
					@Override
					public void callback(int status) {
						registerD.dispose();
					}
				});
				registerD.setVisible(true);
			}
		});
	}

}
