package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerListener;

public class CategoriesPane extends JPanel implements ModelManagerListener {
	
	private ModelManager modelManager;
	private JTable categoriesTable;

	public CategoriesPane(ModelManager manager) {
		this.modelManager = manager;
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		categoriesTable = new JTable();
		categoriesTable.setModel(modelManager.getCategoriesList());
		categoriesTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(categoriesTable), "grow, pushx");
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
