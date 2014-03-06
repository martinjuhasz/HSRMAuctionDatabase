package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerListener;

public class ClosedAuctionsPane extends JPanel {

	private ModelManager modelManager;
	private JTable closedAuctionTable;
	
	public ClosedAuctionsPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		closedAuctionTable = new JTable();
		closedAuctionTable.setModel(modelManager.getClosedAuctionsList());
		closedAuctionTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(closedAuctionTable), "grow, pushx");
		
	}
}
