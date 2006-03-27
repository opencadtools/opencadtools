package com.iver.cit.gvsig.gui.cad.panels;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.utiles.SimpleFileFilter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;

public class ShpPanel extends JWizardPanel {

	private static final long serialVersionUID = -1431370928697152515L;
	private JLabel jLabel = null;
	private JTextField jTextFieldPath = null;
	private JButton jButtonSelectPath = null;

	private class MyInputEventListener implements CaretListener
	{
		public void caretUpdate(CaretEvent arg0) {
			if (jTextFieldPath.getText().length() > 0)
				setFinishButtonEnabled(true);
			else
				setFinishButtonEnabled(false);

		}

	}


	public ShpPanel(JWizardComponents wizardComponents) {
		super(wizardComponents);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
        jLabel = new JLabel();
        jLabel.setText(PluginServices.getText(this,"enter_path_to_file"));
        jLabel.setBounds(new java.awt.Rectangle(12,17,319,15));
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(380,214));
        this.add(jLabel, null);
        this.add(getJTextFieldPath(), null);
        this.add(getJButtonSelectPath(), null);

	}

	/**
	 * This method initializes jTextFieldPath
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldPath() {
		if (jTextFieldPath == null) {
			jTextFieldPath = new JTextField();
			jTextFieldPath.setPreferredSize(new java.awt.Dimension(210,20));
			jTextFieldPath.setBounds(new java.awt.Rectangle(12,38,319,23));
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
			jButtonSelectPath.setBounds(new java.awt.Rectangle(332,38,32,22));
			jButtonSelectPath.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
		            JFileChooser jfc = new JFileChooser();
		            SimpleFileFilter filterShp = new SimpleFileFilter("shp", PluginServices.getText(this,"shp_files"));
		            jfc.setFileFilter(filterShp);
		            if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
		        		    File newFile = jfc.getSelectedFile();
		        		    String path = newFile.getAbsolutePath();
		        		    if (!(path.toLowerCase().endsWith(".shp")))
		        		    {
		        		    	path = path + ".shp";
		        		    }
		        		    jTextFieldPath.setText(path);
		                }

				}
			});
		}
		return jButtonSelectPath;
	}

	public String getPath() {
		return jTextFieldPath.getText();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
