package view;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.ModelManager;
import net.miginfocom.swing.MigLayout;

public class ReportPane extends JPanel {

	private JTabbedPane reportTabPane;
	
	
	public ReportPane(ModelManager modelManager) {
		
		this.setLayout(new MigLayout("fill"));
		
		reportTabPane = new JTabbedPane();
		add(reportTabPane, "grow");
		
		reportTabPane.addTab("abgelaufene Auktionen", new ClosedAuctionsPane(modelManager));
		reportTabPane.addTab("laufende Auktionen", new OpenAuctionsPane(modelManager));
		
	}


}
