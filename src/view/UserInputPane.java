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

public class UserInputPane extends JPanel implements ActionListener {

	private ModelManager modelManager;

	private JTextField userNameField;
	private JPasswordField passwordField;
	private JTextField firstNameField;
	private JTextField surNameField;
	private JTextField emailField;
	private JTextField streetField;
	private JTextField streetNumberField;
	private JTextField postalField;
	private JTextField cityField;
	private JButton submitButton;
	private JButton newButton;
	private JButton deleteButton;
	private int uid;
	private Callback resetCallback;

	public UserInputPane(ModelManager modelManager) {

		this.modelManager = modelManager;
		uid = -1;

		this.setLayout(new MigLayout("", "[][150!]", ""));

		newButton = new JButton("Neu");
		newButton.addActionListener(this);
		add(newButton, "growx, span, wrap");

		JLabel userNameTitle = new JLabel("Benutzername:");
		add(userNameTitle);

		userNameField = new JTextField();
		add(userNameField, "growx, wrap");

		JLabel passwordTitle = new JLabel("Passwort:");
		add(passwordTitle);

		passwordField = new JPasswordField();
		add(passwordField, "growx, wrap");

		JLabel firstNameTitle = new JLabel("Vorname:");
		add(firstNameTitle);

		firstNameField = new JTextField();
		add(firstNameField, "growx, wrap");

		JLabel surNameTitle = new JLabel("Nachname:");
		add(surNameTitle);

		surNameField = new JTextField();
		add(surNameField, "growx, wrap");

		JLabel emailTitle = new JLabel("E-Mail:");
		add(emailTitle);

		emailField = new JTextField();
		add(emailField, "growx, wrap");

		JLabel streetTitle = new JLabel("Straße:");
		add(streetTitle);

		streetField = new JTextField();
		add(streetField, "growx, wrap");

		JLabel streetNumberTitle = new JLabel("Nr.:");
		add(streetNumberTitle);

		streetNumberField = new JTextField();
		add(streetNumberField, "growx, wrap");

		JLabel postalTitle = new JLabel("PLZ:");
		add(postalTitle);

		postalField = new JTextField();
		add(postalField, "growx, wrap");

		JLabel cityTitle = new JLabel("Stadt:");
		add(cityTitle);

		cityField = new JTextField();
		add(cityField, "growx, wrap");

		submitButton = new JButton("Speichern");
		submitButton.addActionListener(this);
		add(submitButton, "growx, span, wrap");

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
					resetCallback.callback();
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
