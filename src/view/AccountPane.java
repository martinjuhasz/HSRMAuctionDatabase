package view;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.UserModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerListener;

public class AccountPane extends JPanel implements ModelManagerListener {
	
	private JTabbedPane accountTabPane;
	private RegisterPane registerPane;
	
	public AccountPane(ModelManager modelManager) {
		
		this.setLayout(new MigLayout("fill"));
		
		modelManager.addModelManagerListener(this);
		
		accountTabPane = new JTabbedPane();
		add(accountTabPane, "grow");
		
		registerPane = new RegisterPane(modelManager);
		
		accountTabPane.addTab("Mein Profil", registerPane);
		accountTabPane.addTab("Bankdaten", null);
		accountTabPane.addTab("Suchanfragen", null);
		accountTabPane.addTab("gewonnene Auktionen", null);
		
	}

	@Override
	public void didUpdate(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didUpdateUser(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didUpdateCategory(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didUpdateAuction(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userDidLogin(ModelManager manager) {
		UserModel userModel = manager.getUserModel(manager.getLoginUserID());
		registerPane.setUser(userModel.getFirst());
	}

	@Override
	public void userDidLogout(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didUpdateSearchTerms(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}
}
