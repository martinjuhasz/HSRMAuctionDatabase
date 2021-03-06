/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;

public class LoginDialog extends JDialog implements ActionListener {

	private ModelManager modelManager;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private JLabel errorLabel;
	private JButton loginButton;
	private JButton registerButton;
	private Callback loginCallback;
	
	public static final int CALLBACK_STATUS_LOGIN = 0;
	public static final int CALLBACK_STATUS_REGISTER = 1;

	/**
	 * Instantiates a new login dialog.
	 *
	 * @param parent the parent
	 * @param modelManager the model manager
	 */
	public LoginDialog(Frame parent, ModelManager modelManager) {
		super(parent, "Login", ModalityType.APPLICATION_MODAL);
		
		this.modelManager = modelManager;

		getContentPane().setLayout(new MigLayout("", "[][150!]", ""));

		JLabel infoLabel = new JLabel("<html>Nutzer: 'arrayridge', Admin: 'choise'<br /> "
				+ "Passwort (beide): 'password'</html>");
		getContentPane().add(infoLabel, "span");
		
		JLabel userNameTitle = new JLabel("Benutzername:");
		getContentPane().add(userNameTitle);

		userNameField = new JTextField("choise");
		userNameField.addActionListener(this);
		getContentPane().add(userNameField, "growx, wrap");

		JLabel passwordTitle = new JLabel("Passwort:");
		getContentPane().add(passwordTitle);

		passwordField = new JPasswordField("password");
		passwordField.addActionListener(this);
		getContentPane().add(passwordField, "growx, wrap");

		errorLabel = new JLabel();
		errorLabel.setForeground(new Color(0xE02626));
		errorLabel.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(errorLabel, "growx, span, wrap");
		
		loginButton = new JButton("Einloggen");
		loginButton.addActionListener(this);
		getContentPane().add(loginButton, "growx, span, wrap");
		
		registerButton = new JButton("Registrieren");
		registerButton.addActionListener(this);
		getContentPane().add(registerButton, "growx, span, wrap");

		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (loginCallback != null) {
					loginCallback.callback(CALLBACK_STATUS_LOGIN);
				}
			}
		});
		setLocationRelativeTo(parent);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == registerButton) {
			loginCallback.callback(CALLBACK_STATUS_REGISTER);
		} else {
			boolean success = false;
			try {
				success = modelManager.login(userNameField.getText(), passwordField.getText());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1);
			}
			
			if (success) {
				if (loginCallback != null) {
					loginCallback.callback(CALLBACK_STATUS_LOGIN);
				}
			} else {
				errorLabel.setText("Benutzername oder Passwort falsch");
			}	
		}
	}
	
	/**
	 * Sets the login callback.
	 *
	 * @param loginCallback the new login callback
	 */
	public void setLoginCallback(Callback loginCallback) {
		this.loginCallback = loginCallback;
	}

}
