package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;




/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToShp implements Extension {
    private  View vista;
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

        vista = (View) f;

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
            JFileChooser jfc = new JFileChooser();
           // if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            /*   FLyrVect layer = (FLyrVect) test.createLayer("prueba",
                        (VectorialFileDriver) driverManager.getDriver(
                            "gvSIG shp driver"), original,
                        ProjectionPool.get("EPSG:23030"));
*/
            if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
        		    File newFile = jfc.getSelectedFile();

                    ShpWriter writer = new ShpWriter(newFile, layer);

                    VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
                    vea.stopEdition(writer);
                    layer.setSource(vea.getOriginalAdapter());
                    layer.setEditing(false);
                    vista.hideConsole();
                }
            //}
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
