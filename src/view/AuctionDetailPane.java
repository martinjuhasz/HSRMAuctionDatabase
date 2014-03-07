package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.AuctionDetailModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;

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
	private boolean direct_buy;
	AuctionDetailModel auctionDetailModel;
	
	public AuctionDetailPane(JFrame parent, ModelManager aModelManager) {
		super(parent, "Auktion", ModalityType.APPLICATION_MODAL);
		this.modelManager = aModelManager;
		this.modelManager.addModelManagerListener(new ModelManagerAdapter() {
			@Override
			public void didUpdateAuction(ModelManager manager) {
				auctionDetailModel.refresh();
				setAuction(auctionDetailModel);
			}
		});
		
		Container pane = getContentPane();
		pane.setLayout(new MigLayout("insets 0","[grow][grow]", ""));
		
		imagePanel = new ImagePanel("", ImagePanel.SIZE_FILL);
		imagePanel.setBackground(Color.red);
		pane.add(imagePanel, "span, growx, h 300, w 640");
		
		titleLabel = new JLabel();
		titleLabel.setFont(new Font(titleLabel.getName(), Font.PLAIN, 20));
		pane.add(titleLabel, "gapleft 20, gaptop 20");
		
		highestBidLabel = new JLabel();
		highestBidLabel.setFont(new Font(highestBidLabel.getName(), Font.PLAIN, 20));
		pane.add(highestBidLabel, "align right, wrap, gapright 20");
		
		highestBidUserLabel = new JLabel();
		pane.add(highestBidUserLabel, "span, align right, wrap, gapright 20");
		
		descriptionLabel = new JLabel();
		pane.add(new JScrollPane(descriptionLabel), "spanx,growx, width ::600, wrap, gapleft 20, gapright 20, gaptop 20, h 100");
		
		startTimeLabel = new JLabel("Start: 01.01.2000 12:10 Uhr");
		pane.add(startTimeLabel, "gapleft 20, gaptop 20");
		
		bidButton = new JButton();
		pane.add(bidButton,  "spany 2, align right, wrap, gapright 20, w 150, h 40");
		final AuctionDetailPane finalThis = this;
		bidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (direct_buy) {
					
				} else {
					final BidDialog bidDialog = new BidDialog(finalThis, modelManager);
					bidDialog.setBidCallback(new Callback() {
						
						@Override
						public void callback(int status) {
							bidDialog.dispose();
						}
					});
					bidDialog.setAuctionId((int)auctionDetailModel.getFirst()[AuctionDetailModel.COLUMN_ID]);
					bidDialog.setStartBid((int)auctionDetailModel.getFirst()[AuctionDetailModel.COLUMN_MAX_BID] + 1);
					bidDialog.setVisible(true);
				}
			}
		});
		
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
		this.auctionDetailModel = auctionModel;
		Object[] auctionData = auctionModel.getFirst();
		BufferedImage img = null;
		try {
			byte[] imageBytes = (byte[])auctionData[AuctionDetailModel.COLUMN_IMAGE];
			if(imageBytes != null) {
				img = ImageIO.read(new ByteArrayInputStream(imageBytes));
				imagePanel.setImage(img);
			} else {
				imagePanel.setImagePath("default_detail.jpg");
			}
		} catch (IOException e) {
			imagePanel.setImagePath("default_detail.jpg");
		}
		
		titleLabel.setText((String)auctionData[AuctionDetailModel.COLUMN_TITLE]);
		highestBidLabel.setText((int) auctionData[AuctionDetailModel.COLUMN_MAX_BID] + " €");
		String maxBidder = (String)auctionData[AuctionDetailModel.COLUMN_MAX_BIDDER];
		if (maxBidder != null) {
			highestBidUserLabel.setText("von " + maxBidder);
		} else {
			highestBidUserLabel.setText("keine Gebote");
		}
		descriptionLabel.setText("<html>" + auctionData[AuctionDetailModel.COLUMN_DESCRIPTION] + "</html>");
		startTimeLabel.setText("Start: " + auctionData[AuctionDetailModel.COLUMN_START_TIME]);
		endTimeLabel.setText("Ende: " + auctionData[AuctionDetailModel.COLUMN_END_TIME]);
		categoryLabel.setText("Kategorie: " + (String)auctionData[AuctionDetailModel.COLUMN_CATEGORY]);
		offererLabel.setText("Anbieter: : " + (String)auctionData[AuctionDetailModel.COLUMN_OFFERER]);
		commentTable.setModel(auctionModel.getCommentModel().getTableModel());
		direct_buy = (boolean)auctionData[AuctionDetailModel.COLUMN_DIRECT_BUY];
		bidButton.setText(direct_buy ? "Kaufen" : "Bieten");
	}

}
