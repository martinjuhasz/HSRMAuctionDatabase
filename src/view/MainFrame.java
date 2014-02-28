package view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import model.ModelManager;

public class MainFrame extends JFrame {
	
	private JTabbedPane tabPane;

	public MainFrame(ModelManager modelManager) {
		tabPane = new JTabbedPane();
		add(tabPane);
		
		tabPane.addTab("Benutzer", new UserPane(modelManager));
		tabPane.addTab("Auktionen", new AuctionPane(modelManager));
		tabPane.addTab("Berichte", new ReportPane());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,500);
	}

}
