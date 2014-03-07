/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package controller;


public interface ModelManagerListener {
	
	/**
	 * Did update.
	 *
	 * @param manager the manager
	 */
	public void didUpdate(ModelManager manager);
	
	/**
	 * Did update user.
	 *
	 * @param manager the manager
	 */
	public void didUpdateUser(ModelManager manager);
	
	/**
	 * Did update category.
	 *
	 * @param manager the manager
	 */
	public void didUpdateCategory(ModelManager manager);
	
	/**
	 * Did update auction.
	 *
	 * @param manager the manager
	 */
	public void didUpdateAuction(ModelManager manager);
	
	/**
	 * Did update search terms.
	 *
	 * @param manager the manager
	 */
	public void didUpdateSearchTerms(ModelManager manager);
	
	/**
	 * User did login.
	 *
	 * @param manager the manager
	 */
	public void userDidLogin(ModelManager manager);
	
	/**
	 * User did logout.
	 *
	 * @param manager the manager
	 */
	public void userDidLogout(ModelManager manager);
}
