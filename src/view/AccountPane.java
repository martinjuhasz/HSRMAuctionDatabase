/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
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
	
	/**
	 * Instantiates a new account pane.
	 *
	 * @param modelManager the model manager
	 */
	public AccountPane(ModelManager modelManager) {
		
		this.setLayout(new MigLayout("fill"));
		
		modelManager.addModelManagerListener(this);
		
		accountTabPane = new JTabbedPane();
		add(accountTabPane, "grow");
		
		registerPane = new RegisterPane(modelManager);
		
		accountTabPane.addTab("Mein Profil", registerPane);
		accountTabPane.addTab("gewonnene Auktionen", new WonAuctionsPane(modelManager));
		
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdate(controller.ModelManager)
	 */
	@Override
	public void didUpdate(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateUser(controller.ModelManager)
	 */
	@Override
	public void didUpdateUser(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateCategory(controller.ModelManager)
	 */
	@Override
	public void didUpdateCategory(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateAuction(controller.ModelManager)
	 */
	@Override
	public void didUpdateAuction(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#userDidLogin(controller.ModelManager)
	 */
	@Override
	public void userDidLogin(ModelManager manager) {
		UserModel userModel = manager.getUserModel(manager.getLoginUserID());
		registerPane.setUser(userModel.getFirst());
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#userDidLogout(controller.ModelManager)
	 */
	@Override
	public void userDidLogout(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateSearchTerms(controller.ModelManager)
	 */
	@Override
	public void didUpdateSearchTerms(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}
}
