package com.iver.cit.gvsig;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.iver.cit.gvsig.fmap.AtomicEvent;
import com.iver.cit.gvsig.fmap.AtomicEventListener;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.layers.FactoryLayerEdited;
import com.iver.cit.gvsig.layers.ILayerEdited;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * @author fjp
 *
 * El propósito de esta clase es centralizar el manejo de la
 * edición. Aquí podemos encontrar una lista con todas
 * los temas en edición, y las propiedades que sean globales
 * e interesantes a la hora de ponerse a editar.
 * Por ejemplo, podemos poner aquí el Grid que vamos a usar,
 * el MapControl que tenemos asociado, etc, etc.
 * También será el responsable de mantener una lista de
 * listeners interesados en los eventos de edición, y
 * de lanzar los eventos que necesitemos.
 * Lo principal es una colección de LayerEdited, y cada
 * LayerEdited es un wrapper alrededor de un tema que guarda
 * las propiedades de la edición.
 *
 * TODO: Poner todo lo referente al EditionManager dentro de una vista.
 * para permitir tener varias vistas con temas en edición
 *
 */
public class EditionManager implements LayerListener {
	private ArrayList editedLayers = new ArrayList();
	private ILayerEdited activeLayerEdited = null;
	private MapControl mapCtrl = null;


	/**
	 * @param lyr
	 * @return
	 */
	public ILayerEdited getLayerEdited(FLayer lyr)
	{
		ILayerEdited aux = null;
		for (int i=0; i < editedLayers.size(); i++)
		{
			aux = (ILayerEdited) editedLayers.get(i);
			if (aux.getLayer() == lyr)
				return aux;
		}
		return null;
	}

	public void visibilityChanged(LayerEvent e) {
	}

	public void activationChanged(LayerEvent e) {
		// Aquí controlamos que solo exista un tema activo y en edición
		// a la vez. Recorremos los temas en edición, y dejamos el primero que encontremos
		// activado, mientras el resto los desactivamos.
//		ILayerEdited aux = null;
//		boolean bFirst = true;
//		mapCtrl.getMapContext().beginAtomicEvent();
//		for (int i=0; i < editedLayers.size(); i++)
//		{
//			aux = (ILayerEdited) editedLayers.get(i);
//			if (aux.getLayer().isActive())
//			{
//				if (!bFirst)
//					aux.getLayer().setActive(false);
//				else
//					activeLayerEdited = aux;
//				bFirst = false;
//			}
//		}
//		mapCtrl.getMapContext().endAtomicEvent();
		if (e.getSource() instanceof FLyrVect){
			activeLayerEdited=new VectorialLayerEdited(e.getSource());
		}
	}

	public void nameChanged(LayerEvent e) {
	}

	public void editionChanged(LayerEvent e) {
		Logger.global.info(e.toString());
		ILayerEdited lyrEdit = getLayerEdited(e.getSource());

		// Si no está en la lista, comprobamos que está en edición
		// y lo añadimos
		if ((lyrEdit == null) && e.getSource().isEditing())
		{
			lyrEdit = FactoryLayerEdited.createLayerEdited(e.getSource());
			editedLayers.add(lyrEdit);
			System.out.println("NUEVA CAPA EN EDICION: " + lyrEdit.getLayer().getName());
			activationChanged(e);
			
		}
		
	}

	/**
	 * @return Returns the activeLayerEdited. Null if there isn't any active AND edited
	 */
	public ILayerEdited getActiveLayerEdited() {
		return activeLayerEdited;
	}

	/**
	 * @return Returns the mapCtrl.
	 */
	public MapControl getMapControl() {
		return mapCtrl;
	}

	/**
	 * @param mapCtrl The mapCtrl to set.
	 */
	public void setMapControl(MapControl mapCtrl) {
		if (mapCtrl != null)
		{
			this.mapCtrl = mapCtrl;
			mapCtrl.getMapContext().getLayers().addLayerListener(this);
		}
	}


}
