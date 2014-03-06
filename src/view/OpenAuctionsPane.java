package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerListener;

public class OpenAuctionsPane extends JPanel implements ModelManagerListener, ListSelectionListener {
	
	private ModelManager modelManager;
	private JTable categoryTable;
	private JTable currentAuctionTable;
	
	
	public OpenAuctionsPane(ModelManager modelManager) {
		
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(this);
		
		setLayout(new MigLayout("fill"));
		
		categoryTable = new JTable();
		categoryTable.setModel(modelManager.getCategoriesList());
		categoryTable.getSelectionModel().addListSelectionListener(this);
		categoryTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(categoryTable), "grow");
		
		currentAuctionTable = new JTable();
		currentAuctionTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(currentAuctionTable), "grow");

	}


	@Override
	public void didUpdate(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void didUpdateUser(ModelManager manager) {
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		// only fire on mouse released
		if(e.getValueIsAdjusting()) return;
		
		String category = (String)categoryTable.getValueAt(categoryTable.getSelectedRow(), 0);
		currentAuctionTable.setModel(modelManager.getActiveAuctionsList(category));
	}
}
