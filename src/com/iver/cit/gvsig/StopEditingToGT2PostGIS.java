package com.iver.cit.gvsig;

import java.io.IOException;
import java.util.HashMap;

import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.postgis.PostgisDataStore;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.factory.FactoryConfigurationError;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SchemaException;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.jdbc_spatial.DlgConnection;
import com.iver.cit.gvsig.jdbc_spatial.gui.jdbcwizard.ConnectionSettings;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.writers.WriterGT2;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToGT2PostGIS extends Extension {
	static PostgisDataStoreFactory postGisFactory = new PostgisDataStoreFactory();
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
    		DlgConnection dlg;
			try {
        	    dlg = new DlgConnection();
	            dlg.setModal(true);
	            dlg.setVisible(true);
	            ConnectionSettings cs = dlg.getConnSettings();
	            if (cs == null)
	                return;

	    	    PostgisDataStore dataStore;
	            HashMap params = new HashMap();
	            // Param[] dbParams = postGisFactory.getParametersInfo();
	            params.put("dbtype", "postgis"); //$NON-NLS-1$
	            params.put("host", cs.getHost());
	            params.put("port", new Integer(cs.getPort()));

	            params.put("database", cs.getDb());

	            params.put("user", cs.getUser());
	            params.put("passwd", cs.getPassw());

	            params.put("wkb enabled", Boolean.TRUE);
	            params.put("loose bbox", Boolean.TRUE);

	            params.put("namespace", ""); //$NON-NLS-1$

				dataStore = (PostgisDataStore) postGisFactory.createDataStore(params);

     		    FeatureType featType = WriterGT2.getFeatureType(layer, "the_geom",
     		    		"autopist2");
				// dataStore.createSchema(featType);

				String featureName = "autopist2"; // dataStore.getTypeNames()["autopist2"];
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EditionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (com.iver.cit.gvsig.fmap.DriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SchemaException e) {
				// TODO Auto-generated catch block
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

