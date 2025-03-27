package com.aj.app;

import javax.swing.JFrame;

public class App {
	
	public App(){
		JFrame frame = new JFrame("Sorting Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Sorting());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}

}
