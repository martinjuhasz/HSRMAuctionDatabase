package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import model.ModelManager;
import net.miginfocom.swing.MigLayout;

public class ClosedAuctionsPane extends JPanel {

	private ModelManager modelManager;
	private JTable closedAuctionTable;
	
	public ClosedAuctionsPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		closedAuctionTable = new JTable();
		closedAuctionTable.setModel(modelManager.getClosedAuctionsList());
		closedAuctionTable.setAutoCreateRowSorter(true);
		closedAuctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(closedAuctionTable), "grow, pushx");
		
	}
}
