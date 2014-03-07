/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerException;

public class RateDialog extends JDialog {
	
	private ModelManager modelManager;
	private JComboBox<String> rateComboBox;
	private int auctionId;
	private JButton submitButton;
	private Callback rateCallback;
	
	/**
	 * Instantiates a new rate dialog.
	 *
	 * @param parent the parent
	 * @param modelManager the model manager
	 */
	public RateDialog(Window parent, final ModelManager modelManager) {
		super(parent, "Bewerten", ModalityType.APPLICATION_MODAL);
		this.modelManager = modelManager;
		
		Container pane = getContentPane();
		pane.setLayout(new MigLayout("", "[][150!]", ""));
		
		JLabel bidTitle = new JLabel("Wertung:");
		pane.add(bidTitle);

		rateComboBox = new JComboBox<>(new String[]{"\u2605","\u2605\u2605", "\u2605\u2605\u2605", 
				"\u2605\u2605\u2605\u2605", "\u2605\u2605\u2605\u2605\u2605"});
		pane.add(rateComboBox, "growx, wrap");
		
		submitButton = new JButton("Bewerten");
		pane.add(submitButton, "growx, span, wrap");
		final RateDialog finalThis = this;
		submitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rateCallback != null) {
					try {
						modelManager.rate(auctionId, rateComboBox.getSelectedIndex() + 1);
						rateCallback.callback(0);
					} catch (SQLException | ModelManagerException e1) {
						JOptionPane.showMessageDialog(finalThis, e1);
					}	
				}
			}
		});
		
		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
	}
	
	/**
	 * Sets the rate callback.
	 *
	 * @param rateCallback the new rate callback
	 */
	public void setRateCallback(Callback rateCallback) {
		this.rateCallback = rateCallback;
	}
	
	/**
	 * Sets the auction id.
	 *
	 * @param auctionId the new auction id
	 */
	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}
	
}
