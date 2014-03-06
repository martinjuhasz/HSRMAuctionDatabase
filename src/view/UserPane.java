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
import model.ModelManagerListener;

public class UserPane extends JPanel implements ModelManagerListener {
	
	private ModelManager modelManager;
	private JTable userTable;
	private UserInputPane userInputPane;

	public UserPane(ModelManager modelManager) {
		this.modelManager = modelManager;
		this.modelManager.addModelManagerListener(this);
		
		setLayout(new MigLayout("fill", "", "[top]"));
		
		
		userTable = new JTable();
		userTable.setModel(modelManager.getUserList());
		userTable.setAutoCreateRowSorter(true);
		userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) return;
				userInputPane.setUser(e.getFirstIndex());
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

	@Override
	public void didUpdate(ModelManager manager) {
		
	}

	@Override
	public void didUpdateUser(ModelManager manager) {
		userTable.setModel(modelManager.getUserList());
	}
	
}
