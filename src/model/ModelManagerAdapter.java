package model;

public abstract class ModelManagerAdapter implements ModelManagerListener {

	@Override
	public void didUpdate(ModelManager manager) {}

	@Override
	public void didUpdateUser(ModelManager manager) {}

	@Override
	public void userDidLogin(ModelManager manager) {}

	@Override
	public void userDidLogout(ModelManager manager) {}

	
}