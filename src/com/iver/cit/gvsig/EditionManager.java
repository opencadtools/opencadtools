package com.iver.cit.gvsig;

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.layers.DefaultLayerEdited;
import com.iver.cit.gvsig.layers.ILayerEdited;

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
 */
public class EditionManager implements LayerListener {

	private ArrayList editedLayers = new ArrayList();
	
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
	}

	public void nameChanged(LayerEvent e) {
	}

	public void editionChanged(LayerEvent e) {
		ILayerEdited lyrEdit = getLayerEdited(e.getSource());
		// Si no está en la lista, comprobamos que está en edición
		// y lo añadimos
		if ((lyrEdit == null) && e.getSource().isEditing())
		{
			DefaultLayerEdited newLayerEdited = new DefaultLayerEdited(e.getSource());			
			editedLayers.add(newLayerEdited);
		}
	}
	
}
