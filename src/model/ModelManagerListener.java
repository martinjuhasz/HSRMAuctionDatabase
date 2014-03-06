package model;

public interface ModelManagerListener {
	
	public void didUpdate(ModelManager manager);
	public void didUpdateUser(ModelManager manager);
	public void didUpdateCategory(ModelManager manager);
	public void userDidLogin(ModelManager manager);
	public void userDidLogout(ModelManager manager);
}
