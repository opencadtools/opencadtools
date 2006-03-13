package com.iver.cit.gvsig.gui.cad.panels;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * @author fjp
 * 
 * Panel para que el usuario seleccione el driver que va a utilizar para
 * crear un tema desde cero.
 *
 */
public class ChooseWriteDriver extends JWizardPanel {

	private JLabel lblSelecDriver = null;
	private JComboBox jCmbBoxDrivers = null;
	private String[] driverNames;

	public ChooseWriteDriver(JWizardComponents wizardComponents, String title, String[] driverNames) {
		super(wizardComponents, title);
		this.driverNames = driverNames;
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	public String getSelectedDriver()
	{
		return (String) jCmbBoxDrivers.getSelectedItem();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        lblSelecDriver = new JLabel();
        lblSelecDriver.setText("please_select_driver");
        this.setSize(new java.awt.Dimension(434,232));
        this.add(lblSelecDriver, null);
        this.add(getJCmbBoxDrivers(), null);
			
	}

	/**
	 * This method initializes jCmbBoxDrivers	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJCmbBoxDrivers() {
		if (jCmbBoxDrivers == null) {
			jCmbBoxDrivers = new JComboBox(driverNames);
		}
		return jCmbBoxDrivers;
	}

	/* (non-Javadoc)
	 * @see jwizardcomponent.JWizardPanel#next()
	 */
	public void next() {		
		super.next();	
		try {
			JWizardPanel nextPanel =  getWizardComponents().getCurrentPanel();
			if (nextPanel instanceof ChooseGeometryType)
			{
				ChooseGeometryType panel = (ChooseGeometryType) nextPanel;
				ISpatialWriter writer = (ISpatialWriter) LayerFactory.getWM().getWriter(getSelectedDriver());
				panel.setDriver(writer);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
