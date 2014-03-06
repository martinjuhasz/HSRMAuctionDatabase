package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import model.ModelManager;
import model.ModelManagerListener;
import net.miginfocom.swing.MigLayout;

public class CategoriesPane extends JPanel implements ModelManagerListener, ActionListener {
	
	private ModelManager modelManager;
	private JTable categoriesTable;
	private JTextField categoryField;
	private JButton submitButton;

	public CategoriesPane(ModelManager manager) {
		this.modelManager = manager;
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		categoriesTable = new JTable();
		categoriesTable.setModel(modelManager.getCategoriesList());
		categoriesTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(categoriesTable), "grow, pushx");
		
		JPanel inputPanel = new JPanel(new MigLayout("", "[][150!]",""));
		
		JLabel categoryTitleLabel = new JLabel("Kategorie:");
		inputPanel.add(categoryTitleLabel);
		
		categoryField = new JTextField();
		inputPanel.add(categoryField, "growx, wrap, w 150!");
		
		submitButton = new JButton("Kategorie hinzufügen");
		submitButton.addActionListener(this);
		inputPanel.add(submitButton, "growx, span, wrap");
		
		add(inputPanel);
		
	}
	
	@Override
	public void didUpdate(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didUpdateUser(ModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
