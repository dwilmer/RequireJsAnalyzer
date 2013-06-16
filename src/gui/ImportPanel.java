package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.RequireJsModule;
import extractor.Extractor;

public class ImportPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private List<ImportPanelListener> listeners;
	private File importFile;
	
	public ImportPanel() {
		this.listeners = new LinkedList<ImportPanelListener>();
		
		this.initGui();
	}
	
	public void addListener(ImportPanelListener listener) {
		this.listeners.add(listener);
	}
	
	private void initGui() {
		this.setLayout(new GridLayout(2, 1));
		
		final JLabel lblStatus = new JLabel("No file selected");
		this.add(lblStatus);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(1, 2));
		
		JButton btnBrowse = new JButton("Browse...");
		pnlButtons.add(btnBrowse, BorderLayout.EAST);
		
		final JButton btnImport = new JButton("Import!");
		btnImport.setEnabled(false);
		pnlButtons.add(btnImport);
		
		this.add(pnlButtons);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int result = chooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					importFile = chooser.getSelectedFile();
					lblStatus.setText("Selected file: " + importFile.getName());
					btnImport.setEnabled(true);
				}
			}
		});
		
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(importFile == null) {
					lblStatus.setText("No file selected! Nothing to import!");
					return;
				}
				lblStatus.setText("Importing modules...");
				
				String path = importFile.getParentFile().getAbsolutePath() + "/";
				String filename = importFile.getName();
				String moduleName = filename.substring(0, filename.length() - 3);
				
				Extractor x = new Extractor(path);
				RequireJsModule module = x.extractModules(moduleName);
				
				lblStatus.setText("Done!");
				for(ImportPanelListener listener : listeners) {
					listener.moduleImported(module);
				}
			}
		});
	}
}
