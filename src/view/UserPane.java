package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ModelManager;
import model.ModelManagerAdapter;
import model.UserList;
import net.miginfocom.swing.MigLayout;

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
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting() ||  userTable.getSelectedRow() < 0) return;
				UserList model = (UserList)userTable.getModel();
				userInputPane.setUser(model.getRow(userTable.getSelectedRow()));
			}
		});
		add(new JScrollPane(userTable), "grow, pushx");
		
		userInputPane = new UserInputPane(modelManager);
		userInputPane.setResetCallback(new Callback() {
			
			@Override
			public void callback(int status) {
				userTable.clearSelection();
			}
		});
		add(userInputPane, "");
		
	}
}
