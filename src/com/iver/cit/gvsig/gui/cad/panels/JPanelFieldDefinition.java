package com.iver.cit.gvsig.gui.cad.panels;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import com.iver.cit.gvsig.fmap.edition.IWriter;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author fjp
 * 
 * Panel para que el usuario seleccione el driver que va a utilizar para
 * crear un tema desde cero.
 *
 */
public class JPanelFieldDefinition extends JWizardPanel {


	private JLabel jLabel = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;


	public JPanelFieldDefinition(JWizardComponents wizardComponents, String title, String[] driverNames) {
		super(wizardComponents, title);
		initialize();
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        jLabel = new JLabel();
        jLabel.setBounds(new java.awt.Rectangle(34,19,31,15));
        jLabel.setText("JLabel");
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(437,232));
        this.add(jLabel, null);
        this.add(getJScrollPane(), null);
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new java.awt.Rectangle(62,53,198,150));
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
		}
		return jTable;
	}



}  //  @jve:decl-index=0:visual-constraint="10,10"
