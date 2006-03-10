package com.iver.cit.gvsig.gui.cad.panels;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import com.iver.cit.gvsig.fmap.edition.IWriter;

/**
 * @author fjp
 * 
 * Panel para que el usuario seleccione el driver que va a utilizar para
 * crear un tema desde cero.
 *
 */
public class JPanelFieldDefinition extends JWizardPanel {


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
        this.setSize(new java.awt.Dimension(434,232));
	}



}  //  @jve:decl-index=0:visual-constraint="10,10"
