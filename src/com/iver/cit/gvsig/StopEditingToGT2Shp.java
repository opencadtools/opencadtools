package com.iver.cit.gvsig;

import java.io.File;
import java.net.URL;
import java.sql.Types;

import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.factory.FactoryConfigurationError;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.SchemaException;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.NullValue;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.cit.gvsig.writers.WriterGT2;
import com.vividsolutions.jts.geom.LineString;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToGT2Shp implements Extension {
    /**
     * @see com.iver.andami.plugins.Extension#inicializar()
     */
    public void inicializar() {
    }

    /**
     * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
     */
    public void execute(String s) {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        View vista = (View) f;
        ProjectView model = vista.getModel();
        FMap mapa = model.getMapContext();
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
     * @see com.iver.andami.plugins.Extension#isEnabled()
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
        	FeatureType featType = WriterGT2.getFeatureType(layer, LineString.class, "the_geom", "prueba");
        	
        	File file = new File("c:/prueba.shp");
			URL theUrl = file.toURL();
			ShapefileDataStore dataStore = new ShapefileDataStore(theUrl);
			dataStore.createSchema(featType);
			
			String featureName = dataStore.getTypeNames()[0];
			FeatureStore featStore = (FeatureStore) dataStore.getFeatureSource(featureName);
			
			// Necesitamos crear de verdad los ficheros antes de usarlos para meter las features
			FeatureWriter featWriter = dataStore.getFeatureWriterAppend(featureName, featStore.getTransaction());
			featWriter.close();
			// Aquí ya tenemos un fichero vacío, listo para usar.
			
			
			WriterGT2 writer = new WriterGT2(featStore);
			
            VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
            vea.stopEdition(writer);
            layer.setSource(vea.getOriginalAdapter());
            layer.setEditing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @see com.iver.andami.plugins.Extension#isVisible()
     */
    public boolean isVisible() {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        if (f == null) {
            return false;
        }

        if (f.getClass() == View.class) {
            View vista = (View) f;
            ProjectView model = vista.getModel();
            FMap mapa = model.getMapContext();

            FLayers capas = mapa.getLayers();

            for (int i = 0; i < capas.getLayersCount(); i++) {
                if (capas.getLayer(i) instanceof FLyrVect &&
                        capas.getLayer(i).isEditing()) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }
}
