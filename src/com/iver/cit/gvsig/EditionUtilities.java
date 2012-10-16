package com.iver.cit.gvsig;

import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

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
        com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
        .getActiveWindow();
        if (f == null)
        	return status;

        if (f instanceof View) {
        	View vista = (View) f;
        	IProjectView model = vista.getModel();
        	if (model == null) {
        	    return status;
        	}
        	MapContext mapa = model.getMapContext();

        	FLayers capas = mapa.getLayers();

        	int numActiveVectorial = 0;
        	int numActiveVectorialEditable = 0;

        	LayersIterator iter = new LayersIterator(capas);

        	FLayer capa;
        	while (iter.hasNext()) {
        		capa = iter.nextLayer();
        		if (capa instanceof FLyrVect &&
        				capa.isActive() && capa.isAvailable()) {
        			numActiveVectorial++;
        			if (capa.isEditing())
        				numActiveVectorialEditable++;
        		}

        	}

        	if (numActiveVectorialEditable == 1 && numActiveVectorial == 1)
        		return EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE;
        	if (numActiveVectorialEditable > 1 && numActiveVectorial == numActiveVectorialEditable)
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
        com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
        .getActiveWindow();
        if (f == null)
        	return null;

        if (f instanceof View) {
        	View vista = (View) f;
        	IProjectView model = vista.getModel();
        	MapContext mapa = model.getMapContext();

        	ArrayList resul = new ArrayList();

        	FLayers capas = mapa.getLayers();

        	int numActiveVectorial = 0;
        	int numActiveVectorialEditable = 0;

        	LayersIterator iter = new LayersIterator(capas);
        	FLayer capa;
        	while (iter.hasNext()) {
        		capa = iter.nextLayer();
        		if (capa instanceof FLyrVect &&
        				capa.isActive()) {
        			numActiveVectorial++;
        			if (capa.isEditing())
        			{
        				numActiveVectorialEditable++;
        				resul.add(capa);
        			}
        		}

        	}
        	if (resul.isEmpty())
        		return null;

       		return (FLayer[]) resul.toArray(new FLayer[0]);

        }

		return null;
	}

	public static ILayerDefinition createLayerDefinition(FLyrVect layer) throws ReadDriverException {
		LayerDefinition lyrDef;
		if (layer.getSource().getDriver() instanceof IVectorialDatabaseDriver)
		{
			VectorialEditableAdapter vea = (VectorialEditableAdapter)layer.getSource();
			IVectorialDatabaseDriver dbDriver = (IVectorialDatabaseDriver) vea.getDriver();

			DBLayerDefinition dbldef=dbDriver.getLyrDef();
			dbldef.setFieldsDesc(vea.getFieldsDescription());
			return dbldef;
		}
		lyrDef = new LayerDefinition();
		lyrDef.setShapeType(layer.getShapeType());
		lyrDef.setProjection(layer.getProjection());
		lyrDef.setName(layer.getName());
			lyrDef.setFieldsDesc(layer.getRecordset().getFieldsDescription());
		return lyrDef;
	}

}
