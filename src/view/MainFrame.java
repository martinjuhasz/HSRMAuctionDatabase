package view;

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
				tabPane.setVisible(true);
			}
		});
		
		setLayout(new MigLayout("rtl"));
		
		loginStatusPane = new LoginStatusPane(modelManager);
		add(loginStatusPane, "wrap");
		
		tabPane = new JTabbedPane();
		tabPane.setVisible(false);
		add(tabPane);
		
		tabPane.addTab("Benutzer", new UserPane(modelManager));
		tabPane.addTab("Kategorien", new CategoriesPane(modelManager));
		tabPane.addTab("Auktionen", new AuctionPane(modelManager));
		tabPane.addTab("Berichte", new ReportPane(modelManager));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,500);
		
		showLogin();
	}
	
	private void showLogin() {
		final MainFrame frame = this;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final LoginDialog loginD = new LoginDialog(frame, modelManager);
				loginD.setLoginCallback(new Callback() {
					
					@Override
					public void callback() {
						if (!modelManager.isLoggedIn()) {
							System.exit(0);
						} else {
							loginD.dispose();
						}
					}
				});
				loginD.setVisible(true);
			}
		});
	}

}
