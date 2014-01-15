package com.iver.cit.gvsig.gui.cad.createTable;

import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import jwizardcomponent.DefaultJWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.edition.writers.dbf.DbfWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.panels.FileBasedPanel;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;

public class NewDBFTableWizard implements NewTableWizard {
    private static Preferences prefs = Preferences.userRoot().node(
	    "gvSIG.encoding.dbf");
    private JPanelFieldDefinition fieldDefinition;
    private FileBasedPanel filePanel;

    @Override
    public JWizardPanel[] getPanels(WizardAndami wizard)
	    throws DriverLoadException {
	DefaultJWizardComponents components = wizard.getWizardComponents();
	fieldDefinition = new JPanelFieldDefinition(components, getWriter());

	filePanel = new FileBasedPanel(wizard.getWizardComponents(), "dbf");

	return new JWizardPanel[] { fieldDefinition, filePanel };
    }

    private DbfWriter getWriter() throws DriverLoadException {
	return new DbfWriter();
    }

    @Override
    public DataSource createTable() throws Exception {
	String path = filePanel.getPath();
	if (!path.toLowerCase().endsWith(".dbf")) {
	    path += ".dbf";
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
	JPanelFieldDefinition fieldDefinitionPanel = fieldDefinition;
	String layerName = newFile.getName();
	FieldDescription[] fieldsDesc = fieldDefinitionPanel
		.getFieldsDescription();

	ITableDefinition tableDefinition = new TableDefinition();
	tableDefinition.setFieldsDesc(fieldsDesc);
	tableDefinition.setName(layerName);

	DbfWriter writer = getWriter();
	String charSetName = prefs.get("dbf_encoding", DbaseFile
		.getDefaultCharset().toString());
	writer.setCharset(Charset.forName(charSetName));
	writer.setFile(newFile);
	writer.initialize(tableDefinition);
	writer.preProcess();
	writer.postProcess();

	LayerFactory.getDataSourceFactory().addFileDataSource(
		"gdbms dbf driver", layerName, newFile.getAbsolutePath());

	return LayerFactory.getDataSourceFactory().createRandomDataSource(
		layerName, DataSourceFactory.AUTOMATIC_OPENING);
    }
}
