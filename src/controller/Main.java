package controller;

import view.MainFrame;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String []args) {
		MainFrame mf = new MainFrame(new ModelManager());
		mf.setVisible(true);
	}

}
