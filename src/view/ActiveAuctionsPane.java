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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.CategoryList;
import model.DatabaseModel;
import model.DatabaseTableModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class ActiveAuctionsPane extends JPanel implements ListSelectionListener {
	
	private ModelManager modelManager;
	private JTable categoryTable;
	private JTable currentAuctionTable;
	
	
	/**
	 * Instantiates a new active auctions pane.
	 *
	 * @param manager the manager
	 */
	public ActiveAuctionsPane(ModelManager manager) {
		
		this.modelManager = manager;
		
		setLayout(new MigLayout("fill"));
		
		// Update view if needed
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateCategory(ModelManager manager) {
				categoryTable.setModel(manager.getCategoriesList().getTableModel());
				currentAuctionTable.setModel(modelManager.getAuctionList().getTableModel(true));
			}
			@Override
			public void didUpdateAuction(ModelManager manager) {
				categoryTable.setModel(manager.getCategoriesList().getTableModel());
				currentAuctionTable.setModel(modelManager.getAuctionList().getTableModel(true));
			}
		});
		
		categoryTable = new JTable();
		categoryTable.setModel(modelManager.getCategoriesList().getTableModel());
		categoryTable.getSelectionModel().addListSelectionListener(this);
		categoryTable.setAutoCreateRowSorter(true);
		categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(categoryTable), "growy, w 150!");
		
		currentAuctionTable = new JTable();
		currentAuctionTable.setAutoCreateRowSorter(true);
		currentAuctionTable.setModel(modelManager.getAuctionList().getTableModel(true));
		currentAuctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(currentAuctionTable), "grow, pushx");

	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		// only fire on mouse released
		if(e.getValueIsAdjusting() || categoryTable.getSelectedRow() < 0) return;
		
		// Update list of auctions that are in the choosen category
		DatabaseModel model = ((DatabaseTableModel)categoryTable.getModel()).getDatabaseModel();
		int category = (int) model.getRow(categoryTable.getSelectedRow())[CategoryList.COLUMN_CATEGORY_ID];
		currentAuctionTable.setModel(modelManager.getAuctionList(category).getTableModel(true));
	}
}
