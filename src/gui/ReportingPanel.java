package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.RequireJsModule;

public class ReportingPanel extends JPanel implements ImportPanelListener {
	private static final long serialVersionUID = 1L;
	private RequireJsModule module;
	private JLabel lblStatus;

	public ReportingPanel() {
		this.module = null;
		this.initEmptyGui();
	}
	
	private void initEmptyGui() {
		lblStatus = new JLabel("No module loaded");
		this.add(lblStatus);
	}
	
	private void initFullGui() {
		lblStatus.setText("Module loaded:" + this.module.getId());
	}

	@Override
	public void moduleImported(RequireJsModule module) {
		// set module
		this.module = module;
		
		// init gui in new thread to stop blocking this thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				initFullGui();
			}
		}).start();
	}
}
