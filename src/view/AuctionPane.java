package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.AuctionList;
import model.ModelManager;
import model.ModelManagerListener;
import net.miginfocom.swing.MigLayout;

public class AuctionPane extends JPanel implements ModelManagerListener {

	private ModelManager modelManager;
	private JTable auctionTable;
	
	
	public AuctionPane(ModelManager manager) {
		this.modelManager = manager;
		setLayout(new MigLayout("fill", "", "[top]"));
		
		auctionTable = new JTable();
		auctionTable.setModel(modelManager.getAuctionList());
		auctionTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(auctionTable), "grow, pushx");
	}


	@Override
	public void didUpdate(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void didUpdateUser(ModelManager manager) {
		auctionTable.setModel(modelManager.getAuctionList());
	}



}
