package view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class UserInputPane extends JPanel {
	
	private JTextField userNameField;
	private JTextField firstNameField;
	private JTextField surNameField;
	private JTextField emailField;
	private JTextField streetField;
	private JTextField streetNumberField;
	private JTextField postalField;
	private JTextField cityField;
	
	public UserInputPane() {
		
		this.setLayout(new MigLayout("", "[][150!]",""));
		
		JLabel userNameTitle = new JLabel("Benutzername:");
		add(userNameTitle);
		
		userNameField = new JTextField();
		add(userNameField, "growx, wrap");
		
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
		
		JButton submitButton = new JButton("Benutzer hinzufügen");
		add(submitButton, "growx, span, wrap");
		
	}
	
}
