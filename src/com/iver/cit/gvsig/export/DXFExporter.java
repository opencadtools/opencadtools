package com.iver.cit.gvsig.export;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.dxf.DXFMemoryDriver;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.utiles.SimpleFileFilter;

public class DXFExporter extends AbstractLayerExporter {
    private static final DXFExporter instance = new DXFExporter();

    private DXFExporter() {
    }

    public static DXFExporter getInstance() {
	return instance;
    }

    @Override
    public void export(MapContext mapContext, FLyrVect layer) {
	try {
	    JFileChooser jfc = new JFileChooser(lastPath);
	    SimpleFileFilter filterShp = new SimpleFileFilter("dxf",
		    PluginServices.getText(this, "dxf_files"));
	    jfc.setFileFilter(filterShp);
	    if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
		File newFile = jfc.getSelectedFile();
		String path = newFile.getAbsolutePath();
		if (!(path.toLowerCase().endsWith(".dxf"))) {
		    path = path + ".dxf";
		}
		newFile = new File(path);

		DxfWriter writer = (DxfWriter) LayerFactory.getWM().getWriter(
			"DXF Writer");
		SHPLayerDefinition lyrDef = new SHPLayerDefinition();
		SelectableDataSource sds = layer.getRecordset();
		FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
		lyrDef.setFieldsDesc(fieldsDescrip);
		lyrDef.setFile(newFile);
		lyrDef.setName(newFile.getName());
		lyrDef.setShapeType(layer.getShapeType());
		writer.setFile(newFile);
		writer.initialize(lyrDef);
		writer.setProjection(layer.getProjection());
		DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
		// TODO: Recuperar aqu� los campos del cuadro de di�logo.
		writer.setFieldMapping(fieldsMapping);
		DXFMemoryDriver dxfDriver = new DXFMemoryDriver();
		dxfDriver.open(newFile);
		writeFeatures(mapContext, layer, writer, dxfDriver);
		String fileName = newFile.getAbsolutePath();
		lastPath = fileName.substring(0,
			fileName.lastIndexOf(File.separatorChar));
	    }

	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	} catch (InitializeWriterException e) {
	    NotificationManager.addError(e.getMessage(), e);
	} catch (DriverLoadException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
    }
}
