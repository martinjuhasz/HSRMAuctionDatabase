/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class ClosedAuctionsPane extends JPanel {

	private ModelManager modelManager;
	private JTable closedAuctionTable;
	
	/**
	 * Instantiates a new closed auctions pane.
	 *
	 * @param manager the manager
	 */
	public ClosedAuctionsPane(ModelManager manager) {
		
		this.modelManager = manager;
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		closedAuctionTable = new JTable();
		closedAuctionTable.setModel(modelManager.getClosedAuctionsList().getTableModel());
		closedAuctionTable.setAutoCreateRowSorter(true);
		closedAuctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(closedAuctionTable), "grow, pushx");
		
		// Update view if needed
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateCategory(ModelManager manager) {
				closedAuctionTable.setModel(manager.getClosedAuctionsList().getTableModel());
			}
		});
		
	}
}
