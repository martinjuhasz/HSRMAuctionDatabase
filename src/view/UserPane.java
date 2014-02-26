package view;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;

public class UserPane extends JPanel {
	
	private ModelManager modelManager;
	private JTable userTable;
	private JPanel userInputPane;

	public UserPane(ModelManager modelManager) {
		this.modelManager = modelManager;
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		
		userTable = new JTable();
		userTable.setModel(modelManager.getUserList());
		userTable.setAutoCreateRowSorter(true);
		add(new JScrollPane(userTable), "grow, pushx");
		
		userInputPane = new UserInputPane();
		add(userInputPane, "");
		
	}

}
