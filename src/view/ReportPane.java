package view;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.ModelManager;

public class ReportPane extends JPanel {

	private JTabbedPane reportTabPane;
	
	
	public ReportPane(ModelManager modelManager) {
		reportTabPane = new JTabbedPane();
		add(reportTabPane);
		
		reportTabPane.addTab("abgelaufene Auktionen", new closedAuctionsPane(modelManager));
		reportTabPane.addTab("laufende Auktionen", new openAuctionsPane(modelManager));
		
	}


}
