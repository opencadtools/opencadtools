package com.iver.cit.gvsig.export;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.gvsig.exceptions.BaseException;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.iver.utiles.SimpleFileFilter;

import es.icarto.gvsig.commons.utils.FileNameUtils;

public class SHPExporter extends AbstractLayerExporter {
    private static Preferences prefs = Preferences.userRoot().node(
	    "gvSIG.encoding.dbf");

    private static final SHPExporter instance = new SHPExporter();

    private SHPExporter() {
    }

    public static SHPExporter getInstance() {
	return instance;
    }

    @Override
    public void export(MapContext mapContext, FLyrVect layer) {
	JFileChooser jfc = new JFileChooser();
	SimpleFileFilter filterShp = new SimpleFileFilter("shp",
		PluginServices.getText(this, "shp_files"));
	jfc.setFileFilter(filterShp);
	Component mainFrame = (Component) PluginServices.getMainFrame();
	if (jfc.showSaveDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
	    return;
	}
	File newFile = jfc.getSelectedFile();
	String path = newFile.getAbsolutePath();
	if (newFile.exists()) {
	    int resp = JOptionPane.showConfirmDialog(mainFrame, PluginServices
		    .getText(this, "fichero_ya_existe_seguro_desea_guardarlo"),
		    PluginServices.getText(this, "guardar"),
		    JOptionPane.YES_NO_OPTION);
	    if (resp != JOptionPane.YES_OPTION) {
		return;
	    }
	}

	try {
	    export(mapContext, layer, path);
	} catch (BaseException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
    }

    private void exportMultiTypeGeom(MapContext mapContext, FLyrVect layer,
	    String path) throws BaseException {
	SelectableDataSource sds = layer.getRecordset();
	FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
	ShpWriter writer1 = (ShpWriter) LayerFactory.getWM().getWriter(
		"Shape Writer");
	Driver[] drivers = new Driver[3];
	ShpWriter[] writers = new ShpWriter[3];

	// puntos
	String auxPoint = path.replaceFirst("\\.shp", "_points.shp");

	SHPLayerDefinition lyrDefPoint = new SHPLayerDefinition();
	lyrDefPoint.setFieldsDesc(fieldsDescrip);
	File filePoints = new File(auxPoint);
	lyrDefPoint.setFile(filePoints);
	lyrDefPoint.setName(filePoints.getName());
	lyrDefPoint.setShapeType(FShape.POINT);
	loadEncoding(layer, writer1);
	writer1.setFile(filePoints);
	writer1.initialize(lyrDefPoint);
	writers[0] = writer1;
	drivers[0] = getOpenShpDriver(filePoints);
	// drivers[0]=null;

	ShpWriter writer2 = (ShpWriter) LayerFactory.getWM().getWriter(
		"Shape Writer");
	// Lineas
	String auxLine = path.replaceFirst("\\.shp", "_line.shp");
	SHPLayerDefinition lyrDefLine = new SHPLayerDefinition();
	lyrDefLine.setFieldsDesc(fieldsDescrip);

	File fileLines = new File(auxLine);
	lyrDefLine.setFile(fileLines);
	lyrDefLine.setName(fileLines.getName());
	lyrDefLine.setShapeType(FShape.LINE);
	loadEncoding(layer, writer2);
	writer2.setFile(fileLines);
	writer2.initialize(lyrDefLine);
	writers[1] = writer2;
	drivers[1] = getOpenShpDriver(fileLines);
	// drivers[1]=null;

	ShpWriter writer3 = (ShpWriter) LayerFactory.getWM().getWriter(
		"Shape Writer");
	// Polï¿½gonos
	String auxPolygon = path.replaceFirst("\\.shp", "_polygons.shp");
	SHPLayerDefinition lyrDefPolygon = new SHPLayerDefinition();
	lyrDefPolygon.setFieldsDesc(fieldsDescrip);
	File filePolygons = new File(auxPolygon);
	lyrDefPolygon.setFile(filePolygons);
	lyrDefPolygon.setName(filePolygons.getName());
	lyrDefPolygon.setShapeType(FShape.POLYGON);
	loadEncoding(layer, writer3);
	writer3.setFile(filePolygons);
	writer3.initialize(lyrDefPolygon);
	writers[2] = writer3;
	drivers[2] = getOpenShpDriver(filePolygons);
	// drivers[2]=null;

	writeMultiFeatures(mapContext, layer, writers, drivers);
    }

    private void exportSingleTypeGeom(MapContext mapContext, FLyrVect layer,
	    String path) throws BaseException {
	path = FileNameUtils.ensureExtension(path, ".shp");
	File newFile = new File(path);
	ensureNeededFilesExists(newFile);
	IndexedShpDriver reader = getOpenShpDriver(newFile);
	exportSingleTypeGeom(mapContext, layer, path, reader);
    }

    /**
     * Este método existe únicamente para instanciar un WriterTask que no
     * pregunte para cada capa exportada si hay que añadirla al TOC
     */
    public void exportSingleTypeGeom(MapContext mapContext, FLyrVect layer,
	    String path, Driver reader) throws BaseException {
	path = FileNameUtils.ensureExtension(path, ".shp");
	File newFile = new File(path);
	ensureNeededFilesExists(newFile);
	SelectableDataSource sds = layer.getRecordset();
	FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
	ShpWriter writer = (ShpWriter) LayerFactory.getWM().getWriter(
		"Shape Writer");
	loadEncoding(layer, writer);
	SHPLayerDefinition lyrDef = new SHPLayerDefinition();
	lyrDef.setFieldsDesc(fieldsDescrip);
	lyrDef.setFile(newFile);
	lyrDef.setName(newFile.getName());
	lyrDef.setShapeType(layer.getTypeIntVectorLayer());
	writer.setFile(newFile);
	writer.initialize(lyrDef);
	writeFeatures(mapContext, layer, writer, reader);
    }

    public void export(MapContext mapContext, FLyrVect layer, String path)
	    throws BaseException {
	path = FileNameUtils.ensureExtension(path, ".shp");

	if (layer.getShapeType() == FShape.MULTI) {
	    exportMultiTypeGeom(mapContext, layer, path);
	} else {
	    exportSingleTypeGeom(mapContext, layer, path);
	}
    }

    private void loadEncoding(FLyrVect layer, ShpWriter writer) {
	String charSetName = prefs.get("dbf_encoding", DbaseFile
		.getDefaultCharset().toString());
	if (layer.getSource() instanceof VectorialFileAdapter) {
	    writer.loadDbfEncoding(((VectorialFileAdapter) layer.getSource())
		    .getFile().getAbsolutePath(), Charset.forName(charSetName));
	} else {
	    Object s = layer.getProperty("DBFFile");
	    if (s != null && s instanceof String) {
		writer.loadDbfEncoding((String) s, Charset.forName(charSetName));
	    }
	}
    }

    private void ensureNeededFilesExists(File fileShp)
	    throws FileNotFoundDriverException {
	if (!fileShp.exists()) {
	    fileShp.getParentFile().mkdirs();
	    try {
		fileShp.createNewFile();
		File newFileSHX = new File(fileShp.getAbsolutePath()
			.replaceAll("[.]shp", ".shx"));
		newFileSHX.createNewFile();
		File newFileDBF = new File(fileShp.getAbsolutePath()
			.replaceAll("[.]shp", ".dbf"));
		newFileDBF.createNewFile();
	    } catch (IOException e) {
		throw new FileNotFoundDriverException("SHP", e,
			fileShp.getAbsolutePath());
	    }
	}
    }

    /**
     * Loads the dbf enconding
     *
     * @param layer
     * @param writer
     */
    private IndexedShpDriver getOpenShpDriver(File fileShp)
	    throws OpenDriverException {
	IndexedShpDriver drv = new IndexedShpDriver();
	drv.open(fileShp);
	return drv;
    }

    private void writeMultiFeatures(MapContext mapContext, FLyrVect layers,
	    IWriter[] writers, Driver[] readers) throws ReadDriverException {
	MultiWriterTask mwt = new MultiWriterTask();
	for (int i = 0; i < writers.length; i++) {
	    mwt.addTask(new WriterTask(mapContext, layers, false, writers[i],
		    readers[i]));
	}
	PluginServices.cancelableBackgroundExecution(mwt);
    }

    @Override
    protected void writeFeatures(MapContext mapContext, FLyrVect layer,
	    IWriter writer, Driver reader) throws ReadDriverException {
	PluginServices.cancelableBackgroundExecution(new WriterTask(mapContext,
		layer, false, writer, reader));
    }
}
