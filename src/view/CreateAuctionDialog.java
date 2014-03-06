package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import controller.ModelManager;

public class CreateAuctionDialog extends JDialog {
	
	private JButton addImageButton;
	private JButton resetImageButton;
	private JTextField titleTextField;
	private JComboBox<String> categoryComboBox;
	private JTextArea descriptionTextField;
	private JCheckBox directBuyCheckBox;
	private JTextField priceTextField;
	private ImagePanel imagePanel;
	private JLabel priceLabel;
	private JButton addButton;
	private JButton closeButton;
	private ModelManager modelManager;
	
	private BufferedImage image;
	
	public CreateAuctionDialog(Frame parent, ModelManager manager) {
		
		super(parent, "Auktion erstellen", ModalityType.APPLICATION_MODAL);
		
		this.modelManager = manager;
		
		setLayout(new MigLayout("fill", "[80!][300][10!]", "[]10[]"));
		
		JLabel headerLabel = new JLabel("Neue Auktion erstellen");
		add(headerLabel, "wrap");
		
		imagePanel = new ImagePanel("default.jpg", ImagePanel.SIZE_FILL);
		imagePanel.setBackground(Color.DARK_GRAY);
		add(imagePanel, "grow, h 150!, w 200!");
		
		JPanel buttonPanel = new JPanel(new MigLayout("fill", "", "[]5[]"));
		add(buttonPanel, "wrap, grow, w 200!, align right, spanx 2");
		
		addImageButton = new JButton("Bild hinzufügen");
		final CreateAuctionDialog weakThis = this;
		addImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileFilter imageFilter = new FileNameExtensionFilter("Bilder", ImageIO.getReaderFileSuffixes());
				fileChooser.addChoosableFileFilter(imageFilter);
				fileChooser.setAcceptAllFileFilterUsed(false);
				
				int didChooseFile = fileChooser.showOpenDialog(weakThis);
				if(didChooseFile == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						image = ImageIO.read(file);
						imagePanel.setImage(image);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		buttonPanel.add(addImageButton, "wrap, growx");
		
		resetImageButton = new JButton("Bild löschen");
		resetImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				imagePanel.setImagePath("default.jpg");
				image = null;
			}
		});
		buttonPanel.add(resetImageButton, "wrap, growx");
		
		JLabel titleLabel = new JLabel("Titel:");
		add(titleLabel, "gapy 30");
		
		titleTextField = new JTextField();
		add(titleTextField, "wrap, growx,spanx 2");
		
		JLabel categoryLabel = new JLabel("Kategorie:");
		add(categoryLabel);
		
		categoryComboBox = new JComboBox<>(manager.getCategoryComboModel());
		categoryComboBox.setSelectedIndex(0);
		add(categoryComboBox, "wrap, growx, span 2");
		
		JLabel descriptionTitleLabel = new JLabel("Beschreibung:");
		add(descriptionTitleLabel, "spanx 3, wrap, gapy 20");
		
		descriptionTextField = new JTextArea();
		add(descriptionTextField, "spanx 3, wrap, growx, h 100!");
		
		JLabel directBuyLabel = new JLabel("Direktkauf:");
		add(directBuyLabel, "gapy 20");
		
		directBuyCheckBox = new JCheckBox();
		directBuyCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					priceLabel.setText("Preis:");
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					priceLabel.setText("Start-Preis:");
				}
			}
		});
		add(directBuyCheckBox, "wrap, gapy 20");
		
		priceLabel = new JLabel("Start-Preis:");
		add(priceLabel);
		
		priceTextField = new JTextField();
		add(priceTextField, "growx");
		
		JLabel priceEuroLabel = new JLabel("€");
		add(priceEuroLabel, "wrap, w 10!");
		
		closeButton = new JButton("abbrechen");
		add(closeButton, "gapy 40");
		
		addButton = new JButton("erstellen");
		add(addButton, "wrap, spanx 2, al right");
		
		
		
		
		pack();
		setPreferredSize(new Dimension(300, 300));
		//setResizable(false);
		setLocationRelativeTo(parent);
	}

}
