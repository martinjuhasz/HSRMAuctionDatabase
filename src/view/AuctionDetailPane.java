package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import model.AuctionDetailModel;
import net.miginfocom.swing.MigLayout;
import controller.ModelManager;
import controller.ModelManagerAdapter;
import controller.ModelManagerException;

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
	private JTextField commentField;
	private JButton commentButton;
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
				int auctionId = (int)auctionDetailModel.getFirst()[AuctionDetailModel.COLUMN_ID];
				int price = (int)auctionDetailModel.getFirst()[AuctionDetailModel.COLUMN_MAX_BID];
				if (direct_buy) {
					int answer = JOptionPane.showOptionDialog(finalThis, titleLabel.getText() +
							" für " + highestBidLabel.getText() + " kaufen?", "Kaufen", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, new String[]{"Ja", "Nein"}, "Nein");
					if (answer == JOptionPane.YES_OPTION) {
						try {
							modelManager.bid(auctionId, price);
						} catch (SQLException | ModelManagerException e1) {
							JOptionPane.showMessageDialog(finalThis, e1);
						}
					}
				} else {
					final BidDialog bidDialog = new BidDialog(finalThis, modelManager);
					bidDialog.setBidCallback(new Callback() {
						
						@Override
						public void callback(int status) {
							bidDialog.dispose();
						}
					});
					bidDialog.setAuctionId(auctionId);
					bidDialog.setStartBid(price + 1);
					bidDialog.setVisible(true);
				}
			}
		});
		
		endTimeLabel = new JLabel();
		pane.add(endTimeLabel, "wrap, gapleft 20");
		
		categoryLabel = new JLabel();
		pane.add(categoryLabel, "wrap, gapleft 20");
		
		offererLabel = new JLabel();
		pane.add(offererLabel, "wrap, gapleft 20");
		
		commentTable = new JTable();
		commentTable.setDefaultRenderer(Object.class, new MultilineCellRenderer());
		
		pane.add(new JScrollPane(commentTable), "spanx, growx, wrap, gapleft 20, gapright 20, gaptop 10, h 150");
		
		commentField = new JTextField();
		pane.add(commentField, "growx, gapleft 20");
		
		commentButton = new JButton("Kommentieren");
		pane.add(commentButton, "gapright 20");
		commentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int auctionId = (int)auctionDetailModel.getFirst()[AuctionDetailModel.COLUMN_ID];
					modelManager.comment(auctionId, commentField.getText());
					commentField.setText("");
				} catch (SQLException | ModelManagerException e1) {
					JOptionPane.showMessageDialog(finalThis, e1);
				}
			}
		});
		
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
		bidButton.setVisible((boolean)auctionDetailModel.getFirst()[AuctionDetailModel.COLUMN_OPEN]);
	}

}
