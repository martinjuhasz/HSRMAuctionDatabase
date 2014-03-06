package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.AuctionList;
import model.ModelManager;
import model.ModelManagerListener;
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
		add(new JScrollPane(categoryTable), "grow");
		
		auctionTable = new JTable();
		auctionTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(auctionTable), "grow, pushx");
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// only fire on mouse released
		if(e.getValueIsAdjusting()) return;
				
		String category = (String)categoryTable.getValueAt(categoryTable.getSelectedRow(), 0);
		auctionTable.setModel(modelManager.getAuctionList(category));
	}
}
