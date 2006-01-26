package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.cit.gvsig.writers.WriterGT2Shp;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToGT2PostGIS implements Extension {
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
            WriterGT2Shp writer = new WriterGT2Shp(layer);
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
