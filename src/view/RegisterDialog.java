/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.Frame;

import javax.swing.JDialog;

import controller.ModelManager;

public class RegisterDialog extends JDialog {

	private RegisterPane registerPane;
	private Callback registerCallback;

	/**
	 * Instantiates a new register dialog.
	 *
	 * @param parent the parent
	 * @param modelManager the model manager
	 */
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
	
	/**
	 * Sets the register callback.
	 *
	 * @param registerCallback the new register callback
	 */
	public void setRegisterCallback(Callback registerCallback) {
		this.registerCallback = registerCallback;
		registerPane.setRegisterCallback(registerCallback);
	}

}
