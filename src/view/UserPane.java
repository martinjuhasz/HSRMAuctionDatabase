package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.DatabaseModel;
import model.DatabaseTableModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

public class UserPane extends JPanel {
	
	private ModelManager modelManager;
	private JTable userTable;
	private UserInputPane userInputPane;

	public UserPane(ModelManager modelManager) {
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateUser(ModelManager manager) {
				userTable.setModel(manager.getUserList().getTableModel());
			}
		});
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		
		userTable = new JTable();
		userTable.setModel(modelManager.getUserList().getTableModel());
		userTable.setAutoCreateRowSorter(true);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting() ||  userTable.getSelectedRow() < 0) return;
				DatabaseModel model = ((DatabaseTableModel)userTable.getModel()).getDatabaseModel();
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
