package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.CategoryList;
import model.ModelManager;
import model.ModelManagerAdapter;
import model.ModelManagerListener;
import model.UserList;
import net.miginfocom.swing.MigLayout;

public class CategoriesPane extends JPanel implements ActionListener, ListSelectionListener {
	
	private ModelManager modelManager;
	private JTable categoriesTable;
	private JTextField categoryField;
	private JButton submitButton;
	private JButton newButton;
	private int categoryRow;

	public CategoriesPane(ModelManager manager) {
		this.modelManager = manager;
		categoryRow = -1;
		
		manager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateCategory(ModelManager manager) {
				categoriesTable.setModel(modelManager.getCategoriesList());
			}
		});
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		categoriesTable = new JTable();
		categoriesTable.setModel(modelManager.getCategoriesList());
		categoriesTable.setAutoCreateRowSorter(true);
		categoriesTable.getSelectionModel().addListSelectionListener(this);
		add(new JScrollPane(categoriesTable), "grow, pushx");
		
		JPanel inputPanel = new JPanel(new MigLayout("", "[][150!]",""));
		
		newButton = new JButton("Neu");
		newButton.addActionListener(this);
		inputPanel.add(newButton, "growx, span, wrap");
		
		JLabel categoryTitleLabel = new JLabel("Kategorie:");
		inputPanel.add(categoryTitleLabel);
		
		categoryField = new JTextField();
		inputPanel.add(categoryField, "growx, wrap, w 150!");
		
		submitButton = new JButton("Speichern");
		submitButton.addActionListener(this);
		inputPanel.add(submitButton, "growx, span, wrap");
		
		add(inputPanel);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submitButton) {
			try {
				
				CategoryList categoryList = (CategoryList)categoriesTable.getModel();
				String oldCategory = (String)categoryList.getValueAt(categoriesTable.getSelectedRow(), CategoryList.COLUMN_CATEGORY);
				
				modelManager.updateCategory(categoryField.getText(), oldCategory, categoryRow < 0);
				cleanCategory();
			} catch (Exception e1) {
				JFrame frame = (JFrame)SwingUtilities.getRoot(this);
				JOptionPane.showMessageDialog(frame, e1);
			}
		} else if(e.getSource() == newButton) {
			cleanCategory();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		setCategory(categoriesTable.getSelectedRow());
		
	}
	
	private void setCategory(int row) {
		categoryRow = row;
		CategoryList categoryList = (CategoryList)categoriesTable.getModel();
		categoryField.setText((String)categoryList.getValueAt(row, CategoryList.COLUMN_CATEGORY));
	}
	
	public void cleanCategory() {
		categoryRow = -1;
		categoryField.setText("");
		categoriesTable.clearSelection();
	}
	

}
