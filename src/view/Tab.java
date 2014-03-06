package view;

import javax.swing.JPanel;

public class Tab {

	private String title;
	private JPanel panel;
	private boolean requireAdmin;

	public Tab(String title, JPanel panel, boolean requireAdmin) {
		super();
		this.title = title;
		this.panel = panel;
		this.requireAdmin = requireAdmin;
	}

	public String getTitle() {
		return title;
	}

	public JPanel getPanel() {
		return panel;
	}

	public boolean isRequireAdmin() {
		return requireAdmin;
	}

}
