package com.iver.cit.gvsig.gui.cad.panels;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * @author fjp
 * 
 *         Panel para que el usuario seleccione el driver que va a utilizar para
 *         crear un tema desde cero.
 * 
 */
public class ChooseWriteDriver extends JWizardPanel {

    private JLabel lblSelecDriver = null;
    private JComboBox jCmbBoxDrivers = null;
    private String[] driverNames;
    private JLabel jLabel = null;
    private JTextField jTextLayerName = null;

    private class MyInputEventListener implements CaretListener {
	@Override
	public void caretUpdate(CaretEvent arg0) {
	    if (jTextLayerName.getText().length() > 0)
		setNextButtonEnabled(true);
	    else
		setNextButtonEnabled(false);

	}

    }

    public ChooseWriteDriver(JWizardComponents wizardComponents, String title,
	    String[] driverNames) {
	super(wizardComponents, title);
	this.driverNames = driverNames;
	initialize();
	// TODO Auto-generated constructor stub

    }

    public String getSelectedDriver() {
	return (String) jCmbBoxDrivers.getSelectedItem();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
	jLabel = new JLabel();
	jLabel.setText(PluginServices.getText(this, "enter_layer_name"));
	jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
	jLabel.setBounds(new java.awt.Rectangle(15, 7, 241, 15));
	lblSelecDriver = new JLabel();
	lblSelecDriver.setText(PluginServices.getText(this, "select_driver"));
	lblSelecDriver.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
	lblSelecDriver.setBounds(new java.awt.Rectangle(15, 68, 245, 15));
	this.setLayout(null);
	this.setSize(new java.awt.Dimension(274, 167));
	this.add(jLabel, null);
	this.add(lblSelecDriver, null);
	this.add(getJCmbBoxDrivers(), null);
	this.add(getJTextLayerName(), null);

    }

    /**
     * This method initializes jCmbBoxDrivers
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getJCmbBoxDrivers() {
	if (jCmbBoxDrivers == null) {
	    jCmbBoxDrivers = new JComboBox(driverNames);
	    jCmbBoxDrivers.setBounds(new java.awt.Rectangle(15, 93, 240, 19));
	}
	return jCmbBoxDrivers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jwizardcomponent.JWizardPanel#next()
     */
    @Override
    public void next() {
	super.next();
	try {
	    JWizardPanel nextPanel = getWizardComponents().getCurrentPanel();
	    if (nextPanel instanceof ChooseGeometryType) {
		ChooseGeometryType panel = (ChooseGeometryType) nextPanel;
		ISpatialWriter writer = (ISpatialWriter) LayerFactory.getDM()
			.getDriver(getSelectedDriver());
		panel.setDriver(writer);
	    }
	} catch (Exception e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
    }

    public String getLayerName() {
	return jTextLayerName.getText();
    }

    /**
     * This method initializes jTextLayerName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextLayerName() {
	if (jTextLayerName == null) {
	    jTextLayerName = new JTextField();
	    jTextLayerName.setBounds(new java.awt.Rectangle(15, 30, 244, 20));
	    jTextLayerName.setText(PluginServices.getText(this, "new_layer"));
	    jTextLayerName.setHorizontalAlignment(SwingConstants.LEFT);
	    jTextLayerName.addCaretListener(new MyInputEventListener());
	}
	return jTextLayerName;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
