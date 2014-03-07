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

public class Tab {

	private String title;
	private JPanel panel;
	private boolean requireAdmin;

	/**
	 * Instantiates a new tab.
	 *
	 * @param title the title
	 * @param panel the panel
	 * @param requireAdmin the require admin
	 */
	public Tab(String title, JPanel panel, boolean requireAdmin) {
		super();
		this.title = title;
		this.panel = panel;
		this.requireAdmin = requireAdmin;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Checks if is require admin.
	 *
	 * @return true, if is require admin
	 */
	public boolean isRequireAdmin() {
		return requireAdmin;
	}

}
