package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerListener;

public class openAuctionsPane extends JPanel implements ModelManagerListener {
	
	private ModelManager modelManager;
	private JTable categoryTable;
	private JTable currentAuctionTable;
	
	
	public openAuctionsPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(this);
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		categoryTable = new JTable();
		categoryTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(categoryTable), "grow, pushx");
		
		currentAuctionTable = new JTable();
		currentAuctionTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(currentAuctionTable), "grow, pushx");
		
		
		
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
