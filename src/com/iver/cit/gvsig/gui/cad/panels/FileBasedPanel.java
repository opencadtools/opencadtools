package com.iver.cit.gvsig.gui.cad.panels;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.iver.andami.PluginServices;
import com.iver.utiles.SimpleFileFilter;

public class FileBasedPanel extends JWizardPanel {
    private static final long serialVersionUID = -1431370928697152515L;
    private JTextField fieldPath;
    private JButton browse;
    private String fileExt;

    public FileBasedPanel(JWizardComponents wizardComponents) {
	super(wizardComponents);
	initialize();
    }

    public FileBasedPanel(JWizardComponents wizardComponents, String extension) {
	this(wizardComponents);
	setFileExtension(extension);
    }

    protected void initialize() {
	JLabel label = new JLabel();
	label.setText(PluginServices.getText(this, "enter_path_to_file"));
	label.setBounds(new java.awt.Rectangle(12, 17, 319, 15));
	this.setLayout(null);
	this.setSize(new java.awt.Dimension(380, 214));
	this.add(label, null);
	this.add(getJTextFieldPath(), null);
	this.add(getJButtonSelectPath(), null);

	setFinishButtonEnabled(false);
    }

    private JTextField getJTextFieldPath() {
	if (fieldPath == null) {
	    fieldPath = new JTextField();
	    fieldPath.setPreferredSize(new java.awt.Dimension(210, 20));
	    fieldPath.setBounds(new java.awt.Rectangle(12, 38, 319, 23));
	    fieldPath.addKeyListener(new java.awt.event.KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent arg0) {
		    if (!fieldPath.getText().equals("")) {
			setFinishButtonEnabled(true);
		    } else {
			setFinishButtonEnabled(false);
		    }
		}

	    });
	}
	return fieldPath;
    }

    private JButton getJButtonSelectPath() {
	if (browse == null) {
	    browse = new JButton();
	    browse.setText("...");
	    browse.setBounds(new java.awt.Rectangle(332, 38, 32, 22));
	    browse.addActionListener(new java.awt.event.ActionListener() {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    JFileChooser jfc = new JFileChooser();
		    SimpleFileFilter filterShp = new SimpleFileFilter(fileExt,
			    PluginServices.getText(this, "file") + " "
				    + fileExt);
		    jfc.setFileFilter(filterShp);
		    if (jfc.showSaveDialog((Component) PluginServices
			    .getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File newFile = jfc.getSelectedFile();
			String path = newFile.getAbsolutePath();
			if (!(path.toLowerCase().endsWith("." + fileExt))) {
			    path = path + "." + fileExt;
			}
			fieldPath.setText(path);
			setFinishButtonEnabled(true);
		    } else {
			setFinishButtonEnabled(false);
		    }

		}
	    });
	}
	return browse;
    }

    public String getPath() {
	return fieldPath.getText();
    }

    /**
     * Use it to set the extension of the file you want to receive. (Without . :
     * Example: for shps: shp for dxfs: dxf)
     * 
     * @param extension
     */
    public void setFileExtension(String extension) {
	this.fileExt = extension;
    }
}
