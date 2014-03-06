package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.ModelManager;
import model.ModelManagerException;
import model.UserList;
import net.miginfocom.swing.MigLayout;

public class UserInputPane extends RegisterPane implements ActionListener {

	private JButton newButton;
	private JButton deleteButton;
	private int uid;
	private Callback resetCallback;

	public UserInputPane(ModelManager modelManager) {
		super(modelManager);
		uid = -1;

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
				modelManager.updateUser(userNameField.getText(),
						passwordField.getText(), firstNameField.getText(),
						surNameField.getText(), emailField.getText(),
						streetField.getText(), streetNumberField.getText(),
						postalField.getText(), cityField.getText(), uid);
				cleanUser();
			} else if (e.getSource() == newButton) {
				if (resetCallback != null) {
					resetCallback.callback(0);
				}
				cleanUser();
			} else if (e.getSource() == deleteButton) {
				modelManager.deleteUser(uid);
			}
		} catch (SQLException | ModelManagerException e1) {
			JFrame frame = (JFrame) SwingUtilities.getRoot(this);
			JOptionPane.showMessageDialog(frame, e1);
		}
	}

	public void setUser(UserList userList, int row) {
		uid = (int) userList.getValueAt(row, UserList.COLUMN_UID);
		userNameField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_USER_NAME));
		firstNameField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_FIRST_NAME));
		surNameField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_SUR_NAME));
		emailField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_EMAIL));
		streetField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_STREET));
		streetNumberField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_STREET_NUMBER));
		postalField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_POSTAL_CODE));
		cityField.setText((String) userList.getValueAt(row,
				UserList.COLUMN_CITY));
	}

	public void cleanUser() {
		uid = -1;
		userNameField.setText("");
		passwordField.setText("");
		firstNameField.setText("");
		surNameField.setText("");
		emailField.setText("");
		streetField.setText("");
		streetNumberField.setText("");
		postalField.setText("");
		cityField.setText("");
	}

	public void setResetCallback(Callback resetCallback) {
		this.resetCallback = resetCallback;
	}
}
