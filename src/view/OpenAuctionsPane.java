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

public class OpenAuctionsPane extends JPanel implements ListSelectionListener {
	
	private ModelManager modelManager;
	private JTable categoryTable;
	private JTable currentAuctionTable;
	
	
	public OpenAuctionsPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		
		setLayout(new MigLayout("fill"));
		
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
		if(e.getValueIsAdjusting()) return;
		
		int category = (int)categoryTable.getModel().getValueAt(categoryTable.getSelectedRow(), CategoryList.COLUMN_CATEGORY_ID);
		currentAuctionTable.setModel(modelManager.getAuctionList(category));
	}
}
