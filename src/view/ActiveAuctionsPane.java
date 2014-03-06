package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.ModelManager;
import controller.ModelManagerAdapter;
import model.CategoryList;
import net.miginfocom.swing.MigLayout;

public class ActiveAuctionsPane extends JPanel implements ListSelectionListener {
	
	private ModelManager modelManager;
	private JTable categoryTable;
	private JTable currentAuctionTable;
	
	
	public ActiveAuctionsPane(ModelManager manager) {
		
		this.modelManager = manager;
		
		setLayout(new MigLayout("fill"));
		
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateCategory(ModelManager manager) {
				categoryTable.setModel(manager.getCategoriesList());
			}
		});
		
		categoryTable = new JTable();
		categoryTable.setModel(modelManager.getCategoriesList());
		categoryTable.getSelectionModel().addListSelectionListener(this);
		categoryTable.setAutoCreateRowSorter(true);
		categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(categoryTable), "growy, w 150!");
		
		currentAuctionTable = new JTable();
		currentAuctionTable.setAutoCreateRowSorter(true);
		currentAuctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(currentAuctionTable), "grow, pushx");

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		// only fire on mouse released
		if(e.getValueIsAdjusting() || currentAuctionTable.getSelectedRow() < 0) return;
		
		int category = (int)categoryTable.getModel().getValueAt(categoryTable.getSelectedRow(), CategoryList.COLUMN_CATEGORY_ID);
		currentAuctionTable.setModel(modelManager.getAuctionList(category));
	}
}
