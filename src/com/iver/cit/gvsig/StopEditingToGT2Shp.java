package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;

import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureType;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.writers.WriterGT2;
import com.iver.utiles.SimpleFileFilter;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToGT2Shp extends Extension {
    /**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
    public void initialize() {
    }

    /**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
    public void execute(String s) {
        com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
                                                             .getActiveWindow();

        View vista = (View) f;
        ProjectView model = vista.getModel();
        MapContext mapa = model.getMapContext();
            FLayers layers = mapa.getLayers();
            if (s.equals("STOPEDITING")){
            for (int i = 0; i < layers.getLayersCount(); i++) {
                if (layers.getLayer(i) instanceof FLyrVect &&
                        layers.getLayer(i).isEditing()) {
                    FLyrVect lv = (FLyrVect) layers.getLayer(i);
                    stopEditing(lv);

                    return;
                }
            }
            }
            PluginServices.getMainFrame().enableControls();
    }

    /**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
    public boolean isEnabled() {
        return true;
    }




    /**
	 * DOCUMENT ME!
	 */
    public void stopEditing(FLyrVect layer) {
        try {
            // WriterGT2Shp writer = new WriterGT2Shp(layer);


            JFileChooser jfc = new JFileChooser();
            // if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
             /*
				 * FLyrVect layer = (FLyrVect) test.createLayer("prueba",
				 * (VectorialFileDriver) driverManager.getDriver( "gvSIG shp
				 * driver"), original, ProjectionPool.get("EPSG:23030"));
				 */
            SimpleFileFilter filterShp = new SimpleFileFilter(".shp", "Ficheros .shp");
            jfc.setFileFilter(filterShp);
             if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION)
             {
         		    File newFile = jfc.getSelectedFile();
         		    FeatureType featType = WriterGT2.getFeatureType(layer, "the_geom",
         		    		newFile.getName());
					URL theUrl = newFile.toURL();
					ShapefileDataStore dataStore = new ShapefileDataStore(theUrl);
					dataStore.createSchema(featType);

					String featureName = dataStore.getTypeNames()[0];
					FeatureStore featStore = (FeatureStore) dataStore.getFeatureSource(featureName);

					// Necesitamos crear de verdad los ficheros antes de usarlos
					// para meter las features
					FeatureWriter featWriter = dataStore.getFeatureWriterAppend(featureName, featStore.getTransaction());
					featWriter.close();
					// Aquí ya tenemos un fichero vacío, listo para usar.


					WriterGT2 writer = new WriterGT2(featStore, true);

		            VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
		            vea.stopEdition(writer,EditionEvent.GRAPHIC);
		            layer.setSource(vea.getOriginalAdapter());
		            layer.setEditing(false);
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
    public boolean isVisible() {
        if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
        	return true;
      	return false;

    }
}

