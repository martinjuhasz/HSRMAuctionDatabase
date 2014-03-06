package view;

import java.awt.Frame;

import javax.swing.JDialog;

import controller.ModelManager;

public class RegisterDialog extends JDialog {

	private RegisterPane registerPane;
	private Callback registerCallback;

	public RegisterDialog(Frame parent, ModelManager modelManager) {
		super(parent, "Registrieren", ModalityType.APPLICATION_MODAL);
		
		registerPane = new RegisterPane(modelManager);
		registerPane.setRegisterCallback(registerCallback);
		getContentPane().add(registerPane);
		
		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
	}
	
	public void setRegisterCallback(Callback registerCallback) {
		this.registerCallback = registerCallback;
		registerPane.setRegisterCallback(registerCallback);
	}

}
