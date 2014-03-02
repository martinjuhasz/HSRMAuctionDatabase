package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerListener;

public class closedAuctionsPane extends JPanel implements ModelManagerListener {

	private ModelManager modelManager;
	private JTable closedAuctionTable;
	
	public closedAuctionsPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(this);
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		closedAuctionTable = new JTable();
		closedAuctionTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(closedAuctionTable), "grow, pushx");
		
	}

	
	
	@Override
	public void didUpdate(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didUpdateUser(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}
}
