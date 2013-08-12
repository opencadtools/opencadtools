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
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;
import com.iver.utiles.SimpleFileFilter;

public class FileBasedPanel extends JWizardPanel {

    private static final long serialVersionUID = -1431370928697152515L;
    private JLabel jLabel = null;
    private JTextField jTextFieldPath = null;
    private JButton jButtonSelectPath = null;
    private CRSSelectPanel crsSelectPanel = null;
    private String fileExt;

    // private class MyInputEventListener implements CaretListener
    // {
    // public void caretUpdate(CaretEvent arg0) {
    // if (jTextFieldPath.getText().length() > 0)
    // setFinishButtonEnabled(true);
    // else
    // setFinishButtonEnabled(false);
    //
    // }
    //
    // }

    public FileBasedPanel(JWizardComponents wizardComponents) {
	super(wizardComponents);
	initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
	jLabel = new JLabel();
	jLabel.setText(PluginServices.getText(this, "enter_path_to_file"));
	jLabel.setBounds(new java.awt.Rectangle(12, 17, 319, 15));
	this.setLayout(null);
	this.setSize(new java.awt.Dimension(380, 214));
	this.add(jLabel, null);
	this.add(getJTextFieldPath(), null);
	this.add(getJButtonSelectPath(), null);

	this.add(getChooserPanel(), null);
	setFinishButtonEnabled(false);
    }

    /**
     * This method initializes jTextFieldPath
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldPath() {
	if (jTextFieldPath == null) {
	    jTextFieldPath = new JTextField();
	    jTextFieldPath.setPreferredSize(new java.awt.Dimension(210, 20));
	    jTextFieldPath.setBounds(new java.awt.Rectangle(12, 38, 319, 23));
	    jTextFieldPath.addKeyListener(new java.awt.event.KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent arg0) {
		    if (!jTextFieldPath.getText().equals("")) {
			setFinishButtonEnabled(true);
		    } else {
			setFinishButtonEnabled(false);
		    }
		}

	    });
	}
	return jTextFieldPath;
    }

    /**
     * This method initializes jButtonSelectPath
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButtonSelectPath() {
	if (jButtonSelectPath == null) {
	    jButtonSelectPath = new JButton();
	    jButtonSelectPath.setText("...");
	    jButtonSelectPath
		    .setBounds(new java.awt.Rectangle(332, 38, 32, 22));
	    jButtonSelectPath
		    .addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    JFileChooser jfc = new JFileChooser();
			    SimpleFileFilter filterShp = new SimpleFileFilter(
				    fileExt, PluginServices.getText(this,
					    "file") + " " + fileExt);
			    jfc.setFileFilter(filterShp);
			    if (jfc.showSaveDialog((Component) PluginServices
				    .getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File newFile = jfc.getSelectedFile();
				String path = newFile.getAbsolutePath();
				if (!(path.toLowerCase()
					.endsWith("." + fileExt))) {
				    path = path + "." + fileExt;
				}
				jTextFieldPath.setText(path);
				setFinishButtonEnabled(true);
			    } else {
				setFinishButtonEnabled(false);
			    }

			}
		    });
	}
	return jButtonSelectPath;
    }

    public String getPath() {
	return jTextFieldPath.getText();
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

    /**
     * This method initializes chooserPanel
     * 
     * @return javax.swing.JPanel
     */
    private CRSSelectPanel getChooserPanel() {
	if (crsSelectPanel == null) {
	    crsSelectPanel = CRSSelectPanel.getPanel(AddLayerDialog
		    .getLastProjection());
	    crsSelectPanel.setBounds(new java.awt.Rectangle(16, 98, 348, 44));
	    IWindow view = PluginServices.getMDIManager().getActiveWindow();
	    if (view instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
		if (((com.iver.cit.gvsig.project.documents.view.gui.View) view)
			.getMapControl().getMapContext().getLayers()
			.getLayersCount() != 0) {
		    crsSelectPanel.getJBtnChangeProj().setEnabled(false);
		}
	    }
	    crsSelectPanel
		    .addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    if (crsSelectPanel.isOkPressed()) {
				AddLayerDialog.setLastProjection(crsSelectPanel
					.getCurProj());
			    }
			}
		    });
	}
	return crsSelectPanel;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
