package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controller.ModelManager;

public class UserInputPane extends RegisterPane implements ActionListener {

	private JButton newButton;
	private JButton deleteButton;
	private Callback resetCallback;

	public UserInputPane(ModelManager modelManager) {
		super(modelManager);

		newButton = new JButton("Neu");
		newButton.addActionListener(this);
		add(newButton, "growx, span, wrap");

		deleteButton = new JButton("Löschen");
		deleteButton.addActionListener(this);
		add(deleteButton, "growx, span, wrap");
	}

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
			JFrame frame = (JFrame) SwingUtilities.getRoot(this);
			JOptionPane.showMessageDialog(frame, e1);
		}
	}


	public void setResetCallback(Callback resetCallback) {
		this.resetCallback = resetCallback;
	}
}
