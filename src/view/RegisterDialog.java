package view;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
