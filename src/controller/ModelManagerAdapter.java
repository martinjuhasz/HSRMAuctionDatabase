/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package controller;


public abstract class ModelManagerAdapter implements ModelManagerListener {

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdate(controller.ModelManager)
	 */
	@Override
	public void didUpdate(ModelManager manager) {}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateUser(controller.ModelManager)
	 */
	@Override
	public void didUpdateUser(ModelManager manager) {}
	
	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateCategory(controller.ModelManager)
	 */
	@Override
	public void didUpdateCategory(ModelManager manager) {}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#userDidLogin(controller.ModelManager)
	 */
	@Override
	public void userDidLogin(ModelManager manager) {}

	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateSearchTerms(controller.ModelManager)
	 */
	@Override
	public void didUpdateSearchTerms(ModelManager manager) {}
	
	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#userDidLogout(controller.ModelManager)
	 */
	@Override
	public void userDidLogout(ModelManager manager) {}
	
	/* (non-Javadoc)
	 * @see controller.ModelManagerListener#didUpdateAuction(controller.ModelManager)
	 */
	@Override
	public void didUpdateAuction(ModelManager manager){}
	
}
