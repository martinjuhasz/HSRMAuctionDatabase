/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.CategoryList;
import model.DatabaseTableModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;
import controller.ModelManagerException;

public class CategoriesPane extends JPanel implements ActionListener, ListSelectionListener {
	
	private ModelManager modelManager;
	private JTable categoriesTable;
	private JTextField categoryField;
	private JButton submitButton;
	private JButton deleteButton;
	private JButton newButton;
	private int cid;

	/**
	 * Instantiates a new categories pane.
	 *
	 * @param manager the manager
	 */
	public CategoriesPane(ModelManager manager) {
		this.modelManager = manager;
		cid = -1;
		
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateCategory(ModelManager manager) {
				categoriesTable.setModel(modelManager.getCategoriesList().getTableModel());
			}
		});
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		categoriesTable = new JTable();
		categoriesTable.setModel(modelManager.getCategoriesList().getTableModel());
		categoriesTable.setAutoCreateRowSorter(true);
		categoriesTable.getSelectionModel().addListSelectionListener(this);
		categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(categoriesTable), "grow, pushx");
		
		JPanel inputPanel = new JPanel(new MigLayout("", "[][150!]",""));
		
		JLabel categoryTitleLabel = new JLabel("Kategorie:");
		inputPanel.add(categoryTitleLabel);
		
		categoryField = new JTextField();
		inputPanel.add(categoryField, "growx, wrap, w 150!");
		
		submitButton = new JButton("Speichern");
		submitButton.addActionListener(this);
		inputPanel.add(submitButton, "growx, span, wrap");
		
		newButton = new JButton("Neu");
		newButton.addActionListener(this);
		inputPanel.add(newButton, "growx, span, wrap");
		
		deleteButton = new JButton("Löschen");
		deleteButton.addActionListener(this);
		inputPanel.add(deleteButton, "growx, span, wrap");
		
		add(inputPanel);
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submitButton) {
			// Save the category
			try {
				modelManager.updateCategory(categoryField.getText(), cid);
				cleanCategory();
			} catch (SQLException | ModelManagerException e1) {
				JFrame frame = (JFrame) SwingUtilities.getRoot(this);
				JOptionPane.showMessageDialog(frame, e1);
			}
		} else if(e.getSource() == newButton) {
			// Clean fields to create a new category
			cleanCategory();
		} else if (e.getSource() == deleteButton) {
			// Delete a category
			try {
				modelManager.deleteCategory(cid);
			} catch (SQLException e1) {
				Window frame = (Window) SwingUtilities.getRoot(this);
				JOptionPane.showMessageDialog(frame, e1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting() || categoriesTable.getSelectedRow() < 0) return;
		setCategory(categoriesTable.getSelectedRow());
		
	}
	
	/**
	 * Sets the category.
	 *
	 * @param row the new category
	 */
	private void setCategory(int row) {
		Object[] rowData = ((DatabaseTableModel)categoriesTable.getModel()).getDatabaseModel().getRow(row);
		cid = (int)rowData[CategoryList.COLUMN_CATEGORY_ID];
		categoryField.setText((String)rowData[CategoryList.COLUMN_CATEGORY_NAME]);
	}
	
	/**
	 * Clean category.
	 */
	public void cleanCategory() {
		cid = -1;
		categoryField.setText("");
		categoriesTable.clearSelection();
	}
	

}
