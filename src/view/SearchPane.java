package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import model.AuctionList;
import model.CategoryList;
import model.DatabaseModel;
import model.DatabaseTableModel;
import model.SearchListModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;
import controller.ModelManagerException;

public class SearchPane extends JPanel implements ListSelectionListener  {
	private ModelManager modelManager;
	private JTable auctionTable;
	private JTable searchTable;
	private JTextField searchField;
	private JButton addSearchButton;
	private JButton searchButton;
	private JButton deleteButton;
	
	
	public SearchPane(ModelManager manager) {
		this.modelManager = manager;
		setLayout(new MigLayout("fill", "", "[top]"));
		
		manager.addModelManagerListener(new ModelManagerAdapter() {

			@Override
			public void didUpdateAuction(ModelManager manager) {
				if(manager.isLoggedIn()) {
					updateTables();
				}
			}
			
			@Override
			public void userDidLogin(ModelManager manager) {
				updateTables();
			}
			
			@Override
			public void didUpdateSearchTerms(ModelManager manager) {
				if(manager.isLoggedIn()) {
					updateTables();
				}
			}
			
			private void updateTables() {
				searchTable.setModel(modelManager.getSearchList(modelManager.getLoginUserID()).getTableModel());
				auctionTable.setModel(new DefaultTableModel());
			}
		});
		
		JPanel searchPanel = new JPanel(new MigLayout("insets 0"));
		add(searchPanel, "wrap, spanx 2");
		
		searchField = new JTextField();
		searchPanel.add(searchField, "w 170!");
		
		searchButton = new JButton("suchen");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSearchTermToAuctionTable(searchField.getText());
			}
		});
		searchPanel.add(searchButton);
		
		addSearchButton = new JButton("+");
		final SearchPane weakThis = this;
		addSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					modelManager.addSearchTerm(searchField.getText());
					searchField.setText("");
				} catch (SQLException e1) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(weakThis);
					JOptionPane.showMessageDialog(frame, e1);
				} catch (ModelManagerException e1) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(weakThis);
					JOptionPane.showMessageDialog(frame, e1);
				}
			}
		});
		searchPanel.add(addSearchButton);
		
		searchTable = new JTable();
		searchTable.getSelectionModel().addListSelectionListener(this);
		searchTable.setAutoCreateRowSorter(true);
		searchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(searchTable), "growy, w 250!");
		
		auctionTable = new JTable();
		auctionTable.setAutoCreateRowSorter(true);
		auctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(auctionTable), "grow, wrap, pushx");
		
		deleteButton = new JButton("löschen");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(searchTable.getSelectedRow() < 0) return;
				DatabaseModel model = ((DatabaseTableModel)searchTable.getModel()).getDatabaseModel();
				String searchTerm = (String) model.getRow(searchTable.getSelectedRow())[SearchListModel.COLUMN_CATEGORY_TERM];
				try {
					modelManager.deleteSearchTerm(searchTerm);
				} catch (SQLException e1) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(weakThis);
					JOptionPane.showMessageDialog(frame, e1);
				}
			}
		});
		add(deleteButton, "spanx 2, wrap");

		
		final SearchPane auctionThis = this;
		auctionTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {

					AuctionList model = (AuctionList) ((DatabaseTableModel) auctionTable
							.getModel()).getDatabaseModel();
					JFrame frame = (JFrame) SwingUtilities.getRoot(auctionThis);
					AuctionDetailPane auctionDetailPane = new AuctionDetailPane(
							frame, modelManager);
					auctionDetailPane.setAuction(model
							.getDetailModelForRow(auctionTable.getSelectedRow()));
					auctionDetailPane.setVisible(true);
				}
			}
		});
		
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// only fire on mouse released
		if(e.getValueIsAdjusting() || searchTable.getSelectedRow() < 0) return;
		
		DatabaseModel model = ((DatabaseTableModel)searchTable.getModel()).getDatabaseModel();
		String searchTerm = (String) model.getRow(searchTable.getSelectedRow())[SearchListModel.COLUMN_CATEGORY_TERM];
		setSearchTermToAuctionTable(searchTerm);
	}
	
	private void setSearchTermToAuctionTable(String term) {
		auctionTable.setModel(modelManager.getAuctionListWithSearchTerm(term).getTableModel());
	}
}
