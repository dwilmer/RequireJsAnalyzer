package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public MainFrame() {
		initGui();
	}
	
	private void initGui() {
		this.setBounds(100, 100, 400, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		ImportPanel importPanel = new ImportPanel();
		ReportingPanel reportPanel = new ReportingPanel();
		importPanel.addListener(reportPanel);
		
		this.setLayout(new BorderLayout(1, 5));
		this.add(importPanel, BorderLayout.NORTH);
		this.add(reportPanel);
	}
	
	public static void main(String[] args) {
		new MainFrame();
	}
}
