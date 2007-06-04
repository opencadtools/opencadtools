package com.iver.cit.gvsig.gui.cad.panels;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverManager;
import com.iver.cit.gvsig.fmap.layers.LayerChangeSupport;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.vectorialdb.ConnectionPanel;
import com.iver.cit.gvsig.vectorialdb.ConnectionSettings;
public class PostGISpanel extends JWizardPanel {

	public PostGISpanel(JWizardComponents wizardComponents) {
		super(wizardComponents);
		initialize();

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private ConnectionPanel jPanelConex = null;

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
        this.setSize(new java.awt.Dimension(408,284));
        this.add(getJPanelConex(), null);

	}

	/**
	 * This method initializes jPanelConex
	 *
	 * @return javax.swing.JPanel
	 */
	private ConnectionPanel getJPanelConex() {
		if (jPanelConex == null) {
			jPanelConex = new ConnectionPanel();
			String[] drvAux = new String[1];
			drvAux[0] = "PostGIS JDBC Driver";
			jPanelConex.setDrivers(drvAux);
			jPanelConex.setPreferredSize(new java.awt.Dimension(400,300));
		}
		return jPanelConex;
	}

	public ConnectionSettings getConnSettings() {
		return getJPanelConex().getConnectionSettings();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
