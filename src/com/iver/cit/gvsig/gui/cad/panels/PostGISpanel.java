package com.iver.cit.gvsig.gui.cad.panels;

import java.util.HashMap;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.vectorialdb.ConnectionPanel;
import com.iver.cit.gvsig.vectorialdb.ConnectionSettings;
import com.iver.utiles.XMLEntity;

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
	this.setSize(new java.awt.Dimension(408, 284));
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

	    XMLEntity xml = PluginServices.getPluginServices(
		    "com.iver.cit.gvsig").getPersistentXML();

	    if (xml == null) {
		xml = new XMLEntity();
	    }

	    if (!xml.contains("db-connections")) {
		String[] servers = new String[0];
		xml.putProperty("db-connections", servers);
	    }

	    String[] servers = xml.getStringArrayProperty("db-connections");
	    HashMap settings = new HashMap();
	    for (int i = 0; i < servers.length; i++) {
		ConnectionSettings cs = new ConnectionSettings();
		cs.setFromString(servers[i]);
		if (cs.getDriver().equals(drvAux[0])) {
		    settings.put(cs.getName(), cs);
		}
	    }
	    jPanelConex.setSettings(settings);
	    jPanelConex.setPreferredSize(new java.awt.Dimension(400, 300));
	}
	return jPanelConex;
    }

    public ConnectionSettings getConnSettings() {
	return getJPanelConex().getConnectionSettings();
    }

    public void saveConnectionSettings() {
	getJPanelConex().saveConnectionSettings();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
