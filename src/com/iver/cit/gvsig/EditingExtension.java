package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;



/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class EditingExtension implements Extension {
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
           if (s.equals("CANCELEDITING")){
            	for (int i = 0; i < layers.getLayersCount(); i++) {
                    if (layers.getLayer(i) instanceof FLyrVect &&
                            layers.getLayer(i).isEditing()) {
                        FLyrVect lv = (FLyrVect) layers.getLayer(i);
                        //stopEditing(lv);
                        VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) layers.getLayer(i)).getSource();
                        lv.setSource(vea.getOriginalAdapter());
                        lv.setEditing(false);
                        return;
                    }
                }
           }else if (s.equals("SHOWGRID")){
                //   vista.getMapControl().getCadToolAdapter().setGrid(true);
           }else if (s.equals("HIDEGRID")){
                //   vista.getMapControl().getCadToolAdapter().setGrid(false);
           }else if (s.equals("SETADJUSTGRID")){
                //   vista.getMapControl().getCadToolAdapter().setAdjustGrid(true);
           }else if (s.equals("NOTADJUSTGRID")){
                //   vista.getMapControl().getCadToolAdapter().setAdjustGrid(false);
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
