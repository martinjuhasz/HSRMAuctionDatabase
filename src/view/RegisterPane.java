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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.UserModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerException;

public class RegisterPane extends JPanel implements ActionListener {

	protected ModelManager modelManager;

	protected JTextField userNameField;
	protected JPasswordField passwordField;
	protected JTextField firstNameField;
	protected JTextField surNameField;
	protected JTextField emailField;
	protected JTextField streetField;
	protected JTextField streetNumberField;
	protected JTextField postalField;
	protected JTextField cityField;
	protected JButton submitButton;
	protected int uid;
	private Callback registerCallback;

	/**
	 * Instantiates a new register pane.
	 *
	 * @param modelManager the model manager
	 */
	public RegisterPane(ModelManager modelManager) {
		this.modelManager = modelManager;
		uid = -1;

		this.setLayout(new MigLayout("", "[][150!]", ""));

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
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == submitButton) {
				modelManager.updateUser(userNameField.getText(),
						passwordField.getText(), firstNameField.getText(),
						surNameField.getText(), emailField.getText(),
						streetField.getText(), streetNumberField.getText(),
						postalField.getText(), cityField.getText(), uid);
				if (registerCallback != null) {
					registerCallback.callback(0);
				}
			}
		} catch (SQLException | ModelManagerException e1) {
			Window frame = (Window) SwingUtilities.getRoot(this);
			JOptionPane.showMessageDialog(frame, e1);
		}
	}

	/**
	 * Sets the user.
	 *
	 * @param userData the new user
	 */
	public void setUser(Object[] userData) {
		System.out.println(userData);
		uid = (int) userData[UserModel.COLUMN_UID];
		userNameField.setText((String) userData[UserModel.COLUMN_USER_NAME]);
		firstNameField.setText((String) userData[UserModel.COLUMN_FIRST_NAME]);
		surNameField.setText((String) userData[UserModel.COLUMN_SUR_NAME]);
		emailField.setText((String) userData[UserModel.COLUMN_EMAIL]);
		streetField.setText((String) userData[UserModel.COLUMN_STREET]);
		streetNumberField
				.setText((String) userData[UserModel.COLUMN_STREET_NUMBER]);
		postalField.setText((String) userData[UserModel.COLUMN_POSTAL_CODE]);
		cityField.setText((String) userData[UserModel.COLUMN_CITY]);
	}

	/**
	 * Clean user.
	 */
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

	/**
	 * Sets the register callback.
	 *
	 * @param registerCallback the new register callback
	 */
	public void setRegisterCallback(Callback registerCallback) {
		this.registerCallback = registerCallback;
	}

}
