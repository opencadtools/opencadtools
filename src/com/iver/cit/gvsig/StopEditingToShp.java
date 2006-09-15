package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.SimpleFileFilter;




/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToShp extends Extension {
    private  View vista;
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

        vista = (View) f;

        ProjectView model = vista.getModel();
        MapContext mapa = model.getMapContext();
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
            			MapControl mapControl = vista.getMapControl();
            			stopEditing(lv,mapControl);
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
    public void stopEditing(FLyrVect layer,MapControl mapControl) {
        try {
            JFileChooser jfc = new JFileChooser();
            SimpleFileFilter filterShp = new SimpleFileFilter("shp", PluginServices.getText(this,"shp_files"));
            jfc.setFileFilter(filterShp);
            if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
        		    File newFile = jfc.getSelectedFile();
        		    String path = newFile.getAbsolutePath();
        		    if (!(path.toLowerCase().endsWith(".shp")))
        		    {
        		    	path = path + ".shp";
        		    }
        		    newFile = new File(path);
        			VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
        			// File newFile = vea.getDriver().

        			ShpWriter writer= (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
        			writer.initialize(layer);

                    vea.stopEdition(writer,EditionEvent.GRAPHIC);
                    vea.getCommandRecord().removeCommandListener(mapControl);

                    layer.setEditing(false);
                    vista.hideConsole();
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
