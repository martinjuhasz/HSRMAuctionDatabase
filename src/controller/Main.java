/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package controller;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import view.MainFrame;

import com.alee.laf.WebLookAndFeel;

public class Main {
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new WebLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
		}

		MainFrame mf = new MainFrame(new ModelManager());
		mf.setVisible(true);
	}

}
