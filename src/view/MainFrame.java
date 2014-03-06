package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import model.ModelManager;
import model.ModelManagerAdapter;
import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {
	
	private ModelManager modelManager;
	private LoginStatusPane loginStatusPane;
	private JTabbedPane tabPane;
	private List<Tab> tabs;

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
		
		setLayout(new MigLayout("fill", "", "[55!][]"));
		
		loginStatusPane = new LoginStatusPane(modelManager);
		add(loginStatusPane, "growx, wrap");
		
		tabPane = new JTabbedPane();
		tabPane.setVisible(false);
		add(tabPane, "grow");
		
		tabs = new ArrayList<>();
		tabs.add(new Tab("Benutzer", new UserPane(modelManager), true));
		tabs.add(new Tab("Kategorien", new CategoriesPane(modelManager), true));
		tabs.add(new Tab("Auktionen", new AuctionPane(modelManager), false));
		tabs.add(new Tab("Berichte", new ReportPane(modelManager), true));
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(950,650);
		
		showLogin();
	}
	
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
