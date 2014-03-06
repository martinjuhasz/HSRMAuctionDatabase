package view;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.ModelManager;
import net.miginfocom.swing.MigLayout;

public class LoginDialog extends JDialog implements ActionListener {

	private ModelManager modelManager;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private JLabel errorLabel;
	private Callback loginCallback;

	public LoginDialog(Frame parent, ModelManager modelManager) {
		super(parent, "Login", ModalityType.APPLICATION_MODAL);
		
		this.modelManager = modelManager;

		getContentPane().setLayout(new MigLayout("", "[][150!]", ""));

		JLabel userNameTitle = new JLabel("Benutzername:");
		getContentPane().add(userNameTitle);

		userNameField = new JTextField();
		userNameField.addActionListener(this);
		getContentPane().add(userNameField, "growx, wrap");

		JLabel passwordTitle = new JLabel("Passwort:");
		getContentPane().add(passwordTitle);

		passwordField = new JPasswordField();
		passwordField.addActionListener(this);
		getContentPane().add(passwordField, "growx, wrap");

		errorLabel = new JLabel();
		errorLabel.setForeground(new Color(0xE02626));
		errorLabel.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(errorLabel, "growx, span, wrap");
		
		JButton loginButton = new JButton("Einloggen");
		loginButton.addActionListener(this);
		getContentPane().add(loginButton, "growx, span, wrap");

		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (loginCallback != null) {
					loginCallback.callback();
				}
			}
		});
		setLocationRelativeTo(parent);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean success = false;
		try {
			success = modelManager.login(userNameField.getText(), passwordField.getText());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(this, e1);
		}
		
		if (success) {
			if (loginCallback != null) {
				loginCallback.callback();
			}
		} else {
			errorLabel.setText("Benutzername oder Passwort falsch");
		}	
	}
	
	public void setLoginCallback(Callback loginCallback) {
		this.loginCallback = loginCallback;
	}

}
