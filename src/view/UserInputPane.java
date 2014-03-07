/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controller.ModelManager;

public class UserInputPane extends RegisterPane implements ActionListener {

	private JButton newButton;
	private JButton deleteButton;
	private Callback resetCallback;

	/**
	 * Instantiates a new user input pane.
	 *
	 * @param modelManager the model manager
	 */
	public UserInputPane(ModelManager modelManager) {
		super(modelManager);

		newButton = new JButton("Neu");
		newButton.addActionListener(this);
		add(newButton, "growx, span, wrap");

		deleteButton = new JButton("Löschen");
		deleteButton.addActionListener(this);
		add(deleteButton, "growx, span, wrap");
	}

	/* (non-Javadoc)
	 * @see view.RegisterPane#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == submitButton) {
				super.actionPerformed(e);
				cleanUser();
			} else if (e.getSource() == newButton) {
				if (resetCallback != null) {
					resetCallback.callback(0);
				}
				cleanUser();
			} else if (e.getSource() == deleteButton) {
				modelManager.deleteUser(uid);
			}
		} catch (SQLException e1) {
			Window frame = (Window) SwingUtilities.getRoot(this);
			JOptionPane.showMessageDialog(frame, e1);
		}
	}


	/**
	 * Sets the reset callback.
	 *
	 * @param resetCallback the new reset callback
	 */
	public void setResetCallback(Callback resetCallback) {
		this.resetCallback = resetCallback;
	}
}
