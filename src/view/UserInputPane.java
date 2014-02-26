package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.ModelManager;
import model.ModelManagerException;
import model.User;
import net.miginfocom.swing.MigLayout;

public class UserInputPane extends JPanel implements ActionListener {
	
	private ModelManager modelManager;
	
	private JTextField userNameField;
	private JTextField firstNameField;
	private JTextField surNameField;
	private JTextField emailField;
	private JTextField streetField;
	private JTextField streetNumberField;
	private JTextField postalField;
	private JTextField cityField;
	private JButton submitButton;
	
	public UserInputPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		
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
		
		submitButton = new JButton("Benutzer hinzufügen");
		submitButton.addActionListener(this);
		add(submitButton, "growx, span, wrap");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submitButton) {
			User user = new User(userNameField.getText(), firstNameField.getText(), surNameField.getText(), emailField.getText(), streetField.getText(), streetNumberField.getText(), postalField.getText(), cityField.getText());
			try {
				modelManager.insertObject(user);
			} catch (Exception e1) {
				JFrame frame = (JFrame)SwingUtilities.getRoot(this);
				JOptionPane.showMessageDialog(frame, e1);
			}
		}
	}
	
}
