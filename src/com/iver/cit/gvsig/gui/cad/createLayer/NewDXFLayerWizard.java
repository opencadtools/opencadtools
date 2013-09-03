package com.iver.cit.gvsig.gui.cad.createLayer;

import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;

import jwizardcomponent.JWizardPanel;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.drivers.DXFLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.panels.FileBasedPanel;

public class NewDXFLayerWizard implements NewLayerWizard {
    private FileBasedPanel filePanel;

    @Override
    public JWizardPanel[] getPanels(WizardAndami wizard) {
	filePanel = new FileBasedPanel(wizard.getWizardComponents(), "dxf");
	return new JWizardPanel[] { filePanel };

    }

    @Override
    public FLyrVect createLayer(IProjection projection) throws BaseException {
	String path = filePanel.getPath();
	if (!path.toLowerCase().endsWith(".dxf")) {
	    path += ".dxf";
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

	String layerName = newFile.getName();
	DXFLayerDefinition lyrDef = new DXFLayerDefinition();
	lyrDef.setFile(newFile);
	lyrDef.setName(layerName);

	DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
	fieldsMapping.setLayerField("Layer");
	fieldsMapping.setColorField("Color");
	fieldsMapping.setElevationField("Elevation");
	fieldsMapping.setThicknessField("Thickness");
	fieldsMapping.setTextField("Text");
	fieldsMapping.setHeightText("HeightText");
	fieldsMapping.setRotationText("RotationText");

	DxfWriter writer = (DxfWriter) LayerFactory.getWM().getWriter(
		"DXF Writer");
	writer.setFile(newFile);
	writer.setFieldMapping(fieldsMapping);
	writer.setProjection(projection);
	writer.initialize(lyrDef);
	writer.preProcess();
	writer.postProcess();

	VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM()
		.getDriver("gvSIG DXF Memory Driver");

	return (FLyrVect) LayerFactory.createLayer(layerName, driver, newFile,
		projection);
    }
}
