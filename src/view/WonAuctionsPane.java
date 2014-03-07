package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;

public class WonAuctionsPane extends JPanel {

	private ModelManager modelManager;
	private JTable auctionTable;
	
	public WonAuctionsPane(ModelManager manager) {
		this.modelManager = manager;
		setLayout(new MigLayout("fill", "", "[top]"));
		
		auctionTable = new JTable();
		auctionTable.setModel(modelManager.getAuctionList().getTableModel());
		auctionTable.setAutoCreateRowSorter(true);
		auctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(auctionTable), "grow, pushx, wrap");
		
	}
	
}
