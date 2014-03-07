/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package view;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;

public class ReportPane extends JPanel {

	private JTabbedPane reportTabPane;
	
	
	/**
	 * Instantiates a new report pane.
	 *
	 * @param modelManager the model manager
	 */
	public ReportPane(ModelManager modelManager) {
		
		this.setLayout(new MigLayout("fill"));
		
		reportTabPane = new JTabbedPane();
		add(reportTabPane, "grow");
		
		reportTabPane.addTab("Abgelaufene Auktionen", new ClosedAuctionsPane(modelManager));
		reportTabPane.addTab("Laufende Auktionen", new ActiveAuctionsPane(modelManager));
		
	}


}
