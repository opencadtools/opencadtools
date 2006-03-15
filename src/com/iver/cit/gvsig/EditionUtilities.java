package com.iver.cit.gvsig;

import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;

/**
 * @author fjp
 * 
 * Clase con métodos muy útiles a la hora de hacer nuevas extensiones, y otras
 * cosas que puedan ser genéricas para este plugin.
 *
 */
public class EditionUtilities {
	
	public static final int EDITION_STATUS_NO_EDITION = 0;
	public static final int EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE = 1;
	public static final int EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE = 2;
	public static final int EDITION_STATUS_MULTIPLE_VECTORIAL_LAYER_ACTIVE = 3;
	public static final int EDITION_STATUS_MULTIPLE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE = 4;
	public static int getEditionStatus()
	{
		int status = EDITION_STATUS_NO_EDITION;
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
        .getActiveView();
        if (f == null)
        	return status;

        if (f.getClass() == View.class) {
        	View vista = (View) f;
        	ProjectView model = vista.getModel();
        	FMap mapa = model.getMapContext();

        	FLayers capas = mapa.getLayers();

        	int numActiveVectorial = 0;
        	int numActiveVectorialEditable = 0;
        	for (int i = 0; i < capas.getLayersCount(); i++) {
        		if (capas.getLayer(i) instanceof FLyrVect &&
        				capas.getLayer(i).isActive()) {
        			numActiveVectorial++;
        			if (capas.getLayer(i).isEditing())
        				numActiveVectorialEditable++;
        		}
        	}
        	if (numActiveVectorialEditable == 1)
        		return EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE;
        	if (numActiveVectorialEditable > 1)
        		return EDITION_STATUS_MULTIPLE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE;
        	if (numActiveVectorial == 1)
        		return EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE;
        	if (numActiveVectorial > 1)
        		return EDITION_STATUS_MULTIPLE_VECTORIAL_LAYER_ACTIVE;
        	
        }
		
		return status;
	}

	public static FLayer[] getActiveAndEditedLayers()
	{
		int status = EDITION_STATUS_NO_EDITION;
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
        .getActiveView();
        if (f == null)
        	return null;

        if (f.getClass() == View.class) {
        	View vista = (View) f;
        	ProjectView model = vista.getModel();
        	FMap mapa = model.getMapContext();
        	
        	ArrayList resul = new ArrayList();

        	FLayers capas = mapa.getLayers();

        	int numActiveVectorial = 0;
        	int numActiveVectorialEditable = 0;
        	for (int i = 0; i < capas.getLayersCount(); i++) {
        		if (capas.getLayer(i) instanceof FLyrVect &&
        				capas.getLayer(i).isActive()) {
        			numActiveVectorial++;
        			if (capas.getLayer(i).isEditing())
        			{
        				numActiveVectorialEditable++;
        				resul.add(capas.getLayer(i));
        			}
        		}
        	}
       		return (FLayer[]) resul.toArray(new FLayer[0]);
        	
        }
		
		return null;
	}
	
}
