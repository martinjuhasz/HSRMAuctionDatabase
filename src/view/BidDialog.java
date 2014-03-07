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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerException;

public class BidDialog extends JDialog {
	
	private ModelManager modelManager;
	private JSpinner bidSpinner;
	private int auctionId;
	private JButton submitButton;
	private Callback bidCallback;
	
	/**
	 * Instantiates a new bid dialog.
	 *
	 * @param parent the parent
	 * @param modelManager the model manager
	 */
	public BidDialog(Window parent, final ModelManager modelManager) {
		super(parent, "Auktion", ModalityType.APPLICATION_MODAL);
		this.modelManager = modelManager;
		
		Container pane = getContentPane();
		pane.setLayout(new MigLayout("", "[][150!]", ""));

		JLabel bidTitle = new JLabel("Gebot:");
		pane.add(bidTitle);

		bidSpinner = new JSpinner();
		pane.add(bidSpinner, "growx, wrap");
		
		submitButton = new JButton("Bieten");
		pane.add(submitButton, "growx, span, wrap");
		final BidDialog finalThis = this;
		submitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bidCallback != null) {
					try {
						modelManager.bid(auctionId, (int) bidSpinner.getValue());
						bidCallback.callback(0);
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
	 * Sets the start bid.
	 *
	 * @param bid the new start bid
	 */
	public void setStartBid(int bid) {
		bidSpinner.setValue(bid);
	}
	
	/**
	 * Sets the auction id.
	 *
	 * @param auctionId the new auction id
	 */
	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}
	
	/**
	 * Sets the bid callback.
	 *
	 * @param bidCallback the new bid callback
	 */
	public void setBidCallback(Callback bidCallback) {
		this.bidCallback = bidCallback;
	}
}
