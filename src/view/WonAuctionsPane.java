/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import model.DatabaseTableModel;
import model.WonAuctionsListModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class WonAuctionsPane extends JPanel {

	private ModelManager modelManager;
	private JTable auctionTable;
	
	/**
	 * Instantiates a new won auctions pane.
	 *
	 * @param manager the manager
	 */
	public WonAuctionsPane(ModelManager manager) {
		this.modelManager = manager;
		setLayout(new MigLayout("fill", "", "[top]"));
		
		auctionTable = new JTable();
		auctionTable.setAutoCreateRowSorter(true);
		auctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(auctionTable), "grow, pushx, wrap");
		
		final WonAuctionsPane auctionThis = this;
		auctionTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {

					WonAuctionsListModel model = (WonAuctionsListModel)((DatabaseTableModel) auctionTable.getModel()).getDatabaseModel();
					JFrame frame = (JFrame) SwingUtilities.getRoot(auctionThis);
					AuctionDetailPane auctionDetailPane = new AuctionDetailPane(
							frame, modelManager);
					auctionDetailPane.setAuction(model
							.getDetailModelForRow(auctionTable.getSelectedRow()));
					auctionDetailPane.setVisible(true);
				}
			}
		});
		
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void userDidLogin(ModelManager manager) {
				auctionTable.setModel(manager.getWonAuctionsListModel().getTableModel());
			}
		});
		
	}
	
}
