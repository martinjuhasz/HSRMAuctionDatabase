package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.CategoryList;
import model.ModelManager;
import net.miginfocom.swing.MigLayout;

public class AuctionPane extends JPanel implements ListSelectionListener {

	private ModelManager modelManager;
	private JTable auctionTable;
	private JTable categoryTable;
	
	
	public AuctionPane(ModelManager manager) {
		this.modelManager = manager;
		setLayout(new MigLayout("fill", "", "[top]"));
		
		categoryTable = new JTable();
		categoryTable.setModel(modelManager.getCategoriesList());
		categoryTable.getSelectionModel().addListSelectionListener(this);
		categoryTable.setAutoCreateRowSorter(true);
		categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(categoryTable), "growy, w 150!");
		
		auctionTable = new JTable();
		auctionTable.setModel(modelManager.getAuctionList());
		auctionTable.setAutoCreateRowSorter(true);
		auctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(auctionTable), "grow, pushx");
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// only fire on mouse released
		if(e.getValueIsAdjusting()) return;
		
		int category = (int)categoryTable.getModel().getValueAt(categoryTable.getSelectedRow(), CategoryList.COLUMN_CATEGORY_ID);
		auctionTable.setModel(modelManager.getAuctionList(category));
	}
}
