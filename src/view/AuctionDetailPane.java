package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.AuctionDetailModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;

public class AuctionDetailPane extends JDialog {

	private ModelManager modelManager;
	
	private ImagePanel imagePanel;
	private JLabel titleLabel;
	private JLabel descriptionLabel;
	private JLabel highestBidLabel;
	private JLabel highestBidUserLabel;
	private JButton bidButton;
	private JLabel startTimeLabel;
	private JLabel endTimeLabel;
	private JLabel categoryLabel;
	private JLabel offererLabel;
	private JTable commentTable;
	
	public AuctionDetailPane(JFrame parent, ModelManager modelManager) {
		super(parent, "Auktion", ModalityType.APPLICATION_MODAL);
		this.modelManager = modelManager;
		
		Container pane = getContentPane();
		pane.setLayout(new MigLayout("insets 0","[grow][grow]", ""));
		
		imagePanel = new ImagePanel("", ImagePanel.SIZE_FILL);
		imagePanel.setBackground(Color.red);
		pane.add(imagePanel, "span, growx, h 300");
		
		titleLabel = new JLabel("Testtitel");
		titleLabel.setFont(new Font(titleLabel.getName(), Font.PLAIN, 20));
		pane.add(titleLabel, "gapleft 20, gaptop 20");
		
		highestBidLabel = new JLabel("20 €");
		highestBidLabel.setFont(new Font(highestBidLabel.getName(), Font.PLAIN, 20));
		pane.add(highestBidLabel, "align right, wrap, gapright 20");
		
		highestBidUserLabel = new JLabel("von Eldorado");
		pane.add(highestBidUserLabel, "span, align right, wrap, gapright 20");
		
		descriptionLabel = new JLabel("<html>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. </html>");
		pane.add(descriptionLabel, "span, width ::600, wrap, gapleft 20, gapright 20, gaptop 20");
		
		startTimeLabel = new JLabel("Start: 01.01.2000 12:10 Uhr");
		pane.add(startTimeLabel, "wrap, gapleft 20, gaptop 20");
		
		endTimeLabel = new JLabel("Ende: 01.01.2000 12:10 Uhr");
		pane.add(endTimeLabel, "wrap, gapleft 20");
		
		categoryLabel = new JLabel("Kategorie: Spielzeug");
		pane.add(categoryLabel, "wrap, gapleft 20");
		
		offererLabel = new JLabel("Anbieter: Eldorado");
		pane.add(offererLabel, "wrap, gapleft 20");
		
		commentTable = new JTable();
		pane.add(new JScrollPane(commentTable), "spanx, growx, wrap, gapleft 20, gapright 20, gaptop 10, h 150");
		
		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
	}
	
	public void setAuction(AuctionDetailModel auctionModel) {
		Object[] auctionData = auctionModel.getFirst();
		titleLabel.setText((String)auctionData[AuctionDetailModel.COLUMN_TITLE]);
		highestBidLabel.setText(auctionData[AuctionDetailModel.COLUMN_MAX_BID] + " €");
		String maxBidder = (String)auctionData[AuctionDetailModel.COLUMN_MAX_BIDDER];
		if (maxBidder != null) {
			highestBidUserLabel.setText("von " + maxBidder);
		} else {
			highestBidUserLabel.setText("keine Gebote");
		}
		descriptionLabel.setText((String)auctionData[AuctionDetailModel.COLUMN_DESCRIPTION]);
		startTimeLabel.setText("Start: " + auctionData[AuctionDetailModel.COLUMN_START_TIME]);
		endTimeLabel.setText("Ende: " + auctionData[AuctionDetailModel.COLUMN_END_TIME]);
		categoryLabel.setText("Kategorie: " + (String)auctionData[AuctionDetailModel.COLUMN_CATEGORY]);
		offererLabel.setText("Anbieter: : " + (String)auctionData[AuctionDetailModel.COLUMN_OFFERER]);
		commentTable.setModel(auctionModel.getCommentModel().getTableModel());
	}

}
