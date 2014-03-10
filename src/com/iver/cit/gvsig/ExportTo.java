package com.iver.cit.gvsig;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.export.DXFExporter;
import com.iver.cit.gvsig.export.LayerExporter;
import com.iver.cit.gvsig.export.PostGISExporter;
import com.iver.cit.gvsig.export.SHPExporter;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ExportTo extends Extension {
    private static final Logger logger = Logger.getLogger(ExportTo.class);

    private static HashMap<FLyrVect, EndExportToCommand> exportedLayers = new HashMap<FLyrVect, EndExportToCommand>();

    private static Map<String, LayerExporter> exporters = new HashMap<String, LayerExporter>();

    /**
     * This method is used to add a layer that is exported to other format and
     * its edition has to be finished at the end of this process.
     * 
     * @param layer
     */
    public static void addLayerToStopEdition(FLyrVect layer,
	    EndExportToCommand command) {
	exportedLayers.put(layer, command);
    }

    public static void executeCommand(FLyrVect layer) throws Exception {
	if (exportedLayers.containsKey(layer)) {
	    EndExportToCommand command = exportedLayers.get(layer);
	    command.execute();
	    exportedLayers.remove(layer);
	}
    }

    public static void addExporter(String format, LayerExporter exporter) {
	exporters.put(format, exporter);
    }

    @Override
    public void initialize() {
	addExporter("SHP", SHPExporter.getInstance());
	addExporter("DXF", DXFExporter.getInstance());
	addExporter("POSTGIS", PostGISExporter.getInstance());
    }

    @Override
    public void execute(String actionCommand) {
	export(exporters.get(actionCommand));
    }

    public static void export(LayerExporter exporter) {
	IWindow window = PluginServices.getMDIManager().getActiveWindow();

	if (!(window instanceof View)) {
	    return;
	}

	View view = (View) window;
	MapContext mapContext = view.getModel().getMapContext();
	FLayer[] layers = mapContext.getLayers().getActives();

	// NOTE: IF THERE IS SOME SELECTION, ONLY SELECTED RECORDS ARE
	// SAVED
	for (FLayer layer : layers) {
	    if (!(layer instanceof FLyrVect)) {
		continue;
	    }

	    FLyrVect vectLayer = (FLyrVect) layer;

	    if (confirmExport(vectLayer)) {
		if (exporter != null) {
		    exporter.export(mapContext, vectLayer);
		}
	    }
	}
    }

    protected static boolean confirmExport(FLyrVect layer) {
	// Get number of selected and total features
	int numSelected;
	long total;

	try {
	    numSelected = layer.getRecordset().getSelection().cardinality();
	    total = layer.getRecordset().getRowCount();
	} catch (ReadDriverException e) {
	    logger.error("Cannot export layer", e);
	    JOptionPane.showMessageDialog(null,
		    PluginServices.getText(layer, "cannot_export"),
		    PluginServices.getText(layer, "error"),
		    JOptionPane.ERROR_MESSAGE);
	    return false;
	}

	// Create message
	String layerName = get("LayerName") + ": " + layer.getName() + "\n";
	String numSavedFeatures = get("se_van_a_guardar_") + " ";
	numSavedFeatures += (numSelected > 0) ? numSelected : total;
	numSavedFeatures += "/" + total + " ";
	String message = layerName + numSavedFeatures
		+ get("features_desea_continuar");

	// Show dialog
	int resp = JOptionPane.showConfirmDialog(
		(Component) PluginServices.getMainFrame(), message,
		get("export_to"), JOptionPane.YES_NO_OPTION);
	return resp == JOptionPane.YES_OPTION;
    }

    private static String get(String key) {
	return PluginServices.getText(new ExportTo(), key);
    }

    /**
     * @param layer
     *            FLyrVect to obtain features. If selection, only selected
     *            features will be precessed.
     * @param writer
     *            (Must be already initialized)
     * @throws ReadDriverException
     * @throws ProcessWriterException
     * @throws ExpansionFileReadException
     * @throws EditionException
     * @throws DriverException
     * @throws DriverIOException
     * @throws com.hardcode.gdbms.engine.data.driver.DriverException
     */
    public void writeFeaturesNoThread(FLyrVect layer, IWriter writer)
	    throws ReadDriverException, VisitorException,
	    ExpansionFileReadException {
	ReadableVectorial va = layer.getSource();
	SelectableDataSource sds = layer.getRecordset();

	// Creamos la tabla.
	writer.preProcess();

	int rowCount;
	FBitSet bitSet = layer.getRecordset().getSelection();

	if (bitSet.cardinality() == 0) {
	    rowCount = va.getShapeCount();
	} else {
	    rowCount = bitSet.cardinality();
	}

	ProgressMonitor progress = new ProgressMonitor(
		(JComponent) PluginServices.getMDIManager().getActiveWindow(),
		PluginServices.getText(this, "exportando_features"),
		PluginServices.getText(this, "exportando_features"), 0,
		rowCount);

	progress.setMillisToDecideToPopup(200);
	progress.setMillisToPopup(500);

	if (bitSet.cardinality() == 0) {
	    rowCount = va.getShapeCount();
	    for (int i = 0; i < rowCount; i++) {
		IGeometry geom = va.getShape(i);

		progress.setProgress(i);
		if (progress.isCanceled()) {
		    break;
		}

		if (geom != null) {
		    Value[] values = sds.getRow(i);
		    IFeature feat = new DefaultFeature(geom, values, "" + i);
		    DefaultRowEdited edRow = new DefaultRowEdited(feat,
			    IRowEdited.STATUS_ADDED, i);
		    writer.process(edRow);
		}
	    }
	} else {
	    int counter = 0;
	    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet
		    .nextSetBit(i + 1)) {
		IGeometry geom = va.getShape(i);

		progress.setProgress(counter++);
		if (progress.isCanceled()) {
		    break;
		}

		if (geom != null) {
		    Value[] values = sds.getRow(i);
		    IFeature feat = new DefaultFeature(geom, values, "" + i);
		    DefaultRowEdited edRow = new DefaultRowEdited(feat,
			    IRowEdited.STATUS_ADDED, i);

		    writer.process(edRow);
		}
	    }

	}

	writer.postProcess();
	progress.close();
    }

    @Override
    public boolean isEnabled() {
	int status = EditionUtilities.getEditionStatus();
	if ((status == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE || status == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
		|| (status == EditionUtilities.EDITION_STATUS_MULTIPLE_VECTORIAL_LAYER_ACTIVE)
		|| (status == EditionUtilities.EDITION_STATUS_MULTIPLE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)) {
	    return true;
	}
	return false;
    }

    @Override
    public boolean isVisible() {
	IWindow window = PluginServices.getMDIManager().getActiveWindow();

	if (window instanceof View) {
	    FLayer[] layers = ((View) window).getMapControl().getMapContext()
		    .getLayers().getActives();
	    if (layers.length == 1) {
		return true;
	    }
	}
	return false;
    }

    @Deprecated
    /**
     * Use PostGISExporter.getInstance().export(mapContext, layer) instead
     * We keep this method for compatibility with gvSIG 1.12 and gvSIG CE, but
     * it can be removed in following versions of OpenCADTools, so don't use it
     * deprecated March 10, 2014.
     */
    public void saveToPostGIS(MapContext mapContext, FLyrVect layer) {
	PostGISExporter.getInstance().export(mapContext, layer);
    }
    
    @Deprecated
    /**
     * Use DXFExporter.getInstance().export(mapContext, layer) instead
     * We keep this method for compatibility with gvSIG 1.12 and gvSIG CE, but
     * it can be removed in following versions of OpenCADTools, so don't use it
     * deprecated March 10, 2014.
     */
    public void saveToDxf(MapContext mapContext, FLyrVect layer) {
	// We keep this method for compatibility with gvSIG 1.12 and gvSIG CE
	DXFExporter.getInstance().export(mapContext, layer);
    }

    @Deprecated
    /**
     * Use SHPExporter.getInstance().export(mapContext, layer) instead
     * We keep this method for compatibility with gvSIG 1.12 and gvSIG CE, but
     * it can be removed in following versions of OpenCADTools, so don't use it
     * deprecated March 10, 2014.
     */
    public void saveToShp(MapContext mapContext, FLyrVect layer) {
	// We keep this method for compatibility with gvSIG 1.12 and gvSIG CE
	SHPExporter.getInstance().export(mapContext, layer);
    }
    
    /**
     * This class is used to execute a command at the end of a export process.
     * 
     * @author jpiera
     * 
     */
    public interface EndExportToCommand {
	public void execute() throws Exception;
    }
}
