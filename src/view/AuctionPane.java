package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.CategoryList;
import model.DatabaseModel;
import model.DatabaseTableModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class AuctionPane extends JPanel implements ListSelectionListener {

	private ModelManager modelManager;
	private JTable auctionTable;
	private JTable categoryTable;
	private JButton newAuctionButton;
	
	
	public AuctionPane(ModelManager manager) {
		this.modelManager = manager;
		setLayout(new MigLayout("fill", "", "[top]"));
		
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateCategory(ModelManager manager) {
				categoryTable.setModel(modelManager.getCategoriesList().getTableModel());
				auctionTable.setModel(modelManager.getAuctionList().getTableModel());
			}
		});
		
		categoryTable = new JTable();
		categoryTable.setModel(modelManager.getCategoriesList().getTableModel());
		categoryTable.getSelectionModel().addListSelectionListener(this);
		categoryTable.setAutoCreateRowSorter(true);
		categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(categoryTable), "growy, w 150!");
		
		auctionTable = new JTable();
		auctionTable.setModel(modelManager.getAuctionList().getTableModel());
		auctionTable.setAutoCreateRowSorter(true);
		auctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(auctionTable), "grow, pushx, wrap");
		
		newAuctionButton = new JButton("neue Auktion erstellen");
		add(newAuctionButton, "al right, span");
		
		final AuctionPane auctionThis = this;
		newAuctionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = (JFrame) SwingUtilities.getRoot(auctionThis);
				CreateAuctionDialog auctionDialog = new CreateAuctionDialog(frame, modelManager);
				auctionDialog.setVisible(true);
			}
		});
		
		
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// only fire on mouse released
		if(e.getValueIsAdjusting() || categoryTable.getSelectedRow() < 0) return;
		
		DatabaseModel model = ((DatabaseTableModel)categoryTable.getModel()).getDatabaseModel();
		int category = (int) model.getRow(categoryTable.getSelectedRow())[CategoryList.COLUMN_CATEGORY_ID];
		auctionTable.setModel(modelManager.getAuctionList(category).getTableModel());
	}
}
