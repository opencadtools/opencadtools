package com.iver.cit.gvsig.gui.cad.createLayer;

import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import jwizardcomponent.DefaultJWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.SpatialFileBasedPanel;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;

public class NewSHPLayerWizard implements NewLayerWizard {
    public static final String TYPE = "SHP";
    
    private static Preferences prefs = Preferences.userRoot().node(
	    "gvSIG.encoding.dbf");

    private ChooseGeometryType geometryType;
    private JPanelFieldDefinition fieldDefinition;
    private SpatialFileBasedPanel filePanel;

    @Override
    public JWizardPanel[] getPanels(WizardAndami wizard)
	    throws DriverLoadException {
	DefaultJWizardComponents components = wizard.getWizardComponents();
	geometryType = new ChooseGeometryType(components);
	geometryType.setDriver(getDriver());

	fieldDefinition = new JPanelFieldDefinition(components);
	fieldDefinition.setWriter(getWriter());

	filePanel = new SpatialFileBasedPanel(wizard.getWizardComponents(), "shp");

	return new JWizardPanel[] { geometryType, fieldDefinition, filePanel };
    }

    private VectorialFileDriver getDriver() throws DriverLoadException {
	return (VectorialFileDriver) LayerFactory.getDM().getDriver(
		"gvSIG shp driver");
    }

    private ShpWriter getWriter() throws DriverLoadException {
	return (ShpWriter) LayerFactory.getWM().getWriter("Shape Writer");
    }

    @Override
    public FLyrVect createLayer(IProjection projection) throws BaseException {
	String path = filePanel.getPath();
	if (!path.toLowerCase().endsWith(".shp")) {
	    path += ".shp";
	}

	File newFile = new File(path);
	if (newFile.exists()) {
	    int resp = JOptionPane.showConfirmDialog((Component) PluginServices
		    .getMainFrame(), PluginServices.getText(this,
		    "fichero_ya_existe_seguro_desea_guardarlo"), PluginServices
		    .getText(this, "guardar"), JOptionPane.YES_NO_OPTION);
	    if (resp != JOptionPane.YES_OPTION) {
		return null;
	    }
	}

	ChooseGeometryType geometryTypePanel = geometryType;
	JPanelFieldDefinition fieldDefinitionPanel = fieldDefinition;

	String layerName = geometryTypePanel.getLayerName();
	int geometryType = geometryTypePanel.getSelectedGeometryType();
	FieldDescription[] fieldsDesc = fieldDefinitionPanel
		.getFieldsDescription();

	SHPLayerDefinition lyrDef = new SHPLayerDefinition();
	lyrDef.setFieldsDesc(fieldsDesc);
	lyrDef.setFile(newFile);
	lyrDef.setName(layerName);
	lyrDef.setShapeType(geometryType);

	ShpWriter writer = getWriter();
	String charSetName = prefs.get("dbf_encoding", DbaseFile
		.getDefaultCharset().toString());
	writer.loadDbfEncoding(newFile.getAbsolutePath(),
		Charset.forName(charSetName));
	writer.setCharset(Charset.forName(charSetName));
	writer.setFile(newFile);
	writer.initialize(lyrDef);
	writer.preProcess();
	writer.postProcess();

	return (FLyrVect) LayerFactory.createLayer(layerName, getDriver(),
		newFile, projection);
    }
}
