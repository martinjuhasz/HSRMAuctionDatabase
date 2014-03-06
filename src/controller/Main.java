package controller;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.alee.laf.WebLookAndFeel;

import view.MainFrame;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new WebLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
		}

		MainFrame mf = new MainFrame(new ModelManager());
		mf.setVisible(true);
	}

}
