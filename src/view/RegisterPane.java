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

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerException;

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
	private Callback registerCallback;

	public RegisterPane(ModelManager modelManager) {
		this.modelManager = modelManager;
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == submitButton) {
				modelManager.updateUser(userNameField.getText(),
						passwordField.getText(), firstNameField.getText(),
						surNameField.getText(), emailField.getText(),
						streetField.getText(), streetNumberField.getText(),
						postalField.getText(), cityField.getText(), -1);
				if (registerCallback != null) {
					registerCallback.callback(0);
				}
			}
		} catch (SQLException | ModelManagerException e1) {
			JFrame frame = (JFrame) SwingUtilities.getRoot(this);
			JOptionPane.showMessageDialog(frame, e1);
		}
	}
	
	public void setRegisterCallback(Callback registerCallback) {
		this.registerCallback = registerCallback;
	}

}
