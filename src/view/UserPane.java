package view;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

import net.miginfocom.swing.MigLayout;
import model.ModelManager;
import model.ModelManagerAdapter;
import model.ModelManagerListener;
import model.UserList;

public class UserPane extends JPanel {
	
	private ModelManager modelManager;
	private JTable userTable;
	private UserInputPane userInputPane;

	public UserPane(ModelManager modelManager) {
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateUser(ModelManager manager) {
				userTable.setModel(manager.getUserList());
			}
		});
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		
		userTable = new JTable();
		userTable.setModel(modelManager.getUserList());
		userTable.setAutoCreateRowSorter(true);
		userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) return;
				userInputPane.setUser((UserList)userTable.getModel(), userTable.getSelectedRow());
			}
		});
		add(new JScrollPane(userTable), "grow, pushx");
		
		userInputPane = new UserInputPane(modelManager);
		userInputPane.setResetCallback(new Callback() {
			
			@Override
			public void callback() {
				userTable.clearSelection();
			}
		});
		add(userInputPane, "");
		
	}
}
