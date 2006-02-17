package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
//import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.utiles.SimpleFileFilter;




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
            if (s.equals("STOPEDITING"))
            {
            	FLayer[] actives = layers.getActives();
            	// TODO: Comprobar que solo hay una activa, o al menos
            	// que solo hay una en edición que esté activa, etc, etc
            	for (int i = 0; i < actives.length; i++)
            	{
            		if (actives[i] instanceof FLyrVect &&
                        actives[i].isEditing())
            		{
            			FLyrVect lv = (FLyrVect) actives[i];
            			MapControl mapControl = (MapControl) vista.getMapControl();
            			stopEditing(lv,mapControl);

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
    public void stopEditing(FLyrVect layer,MapControl mapControl) {
        try {
            JFileChooser jfc = new JFileChooser();
            SimpleFileFilter filterShp = new SimpleFileFilter("shp", "Ficheros .shp");
            jfc.setFileFilter(filterShp);
            if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
        		    File newFile = jfc.getSelectedFile();
        			VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
        			// File newFile = vea.getDriver().

        			ShpWriter writer= (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
        			writer.initialize(newFile,layer);

                    vea.stopEdition(writer);
                    vea.getCommandRecord().removeExecuteCommand(mapControl);
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
        if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
        	return true;
        else
        	return false;

    }
}
