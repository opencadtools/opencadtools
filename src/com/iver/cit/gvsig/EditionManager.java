package com.iver.cit.gvsig;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.gui.View;
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
 * Nuevo: Llevar aquí el control de las tablas en edición también
 * y centralizar los listeners interesados en los eventos de edición.
 *
 * TODO: Poner todo lo referente al EditionManager dentro de una vista.
 * para permitir tener varias vistas con temas en edición
 *
 */
public class EditionManager implements LayerListener,LayerCollectionListener {
	private ArrayList editedLayers = new ArrayList();
	private ArrayList editedTables = new ArrayList();
	//private ArrayList activeLayerEdited = new ArrayList();
	private MapControl mapCtrl = null;
	private ILayerEdited ile=null;
	//private int idActiveLayer=0;


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
		if (e.getSource().isActive()){
			ile=getLayerEdited(e.getSource());
			if (PluginServices.getMDIManager().getActiveWindow() instanceof View)
			{
				View view=(View)PluginServices.getMDIManager().getActiveWindow();
				if (e.getSource().isEditing()){
					view.showConsole();
				}else{
					view.hideConsole();
				}
			}

		}
		if (ile==null || ile.getLayer().equals(e.getSource())){

			if (ile!=null && !ile.getLayer().isActive()) {
			VectorialLayerEdited lastVLE = (VectorialLayerEdited)ile;
			lastVLE.activationLost(e);
		}
		if (e.getSource() instanceof FLyrVect) {
			VectorialLayerEdited vle = null;
			vle=(VectorialLayerEdited)getLayerEdited(e.getSource());
			// for (int i = 0; i < editedLayers.size(); i++) {
			// vle = (VectorialLayerEdited) editedLayers.get(i);
			// if (vle.getLayer().equals(e.getSource())) {
					// idActiveLayer = i;
			ile=vle;
			if (getMapControl()!=null && vle!=null && vle.getLayer().isActive()){
				getMapControl().setTool("cadtooladapter");
				vle.activationGained(e);
				return;
			}
		}
		// }
		// idActiveLayer=-1;
		//ile=null;
		if (getMapControl()!=null){
			getMapControl().setTool("zoomIn");
			PluginServices.getMainFrame().setSelectedTool("ZOOM_IN");
		}
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
			if (getMapControl()!=null){
				getMapControl().setTool("cadtooladapter");
				CADExtension.setCADTool("_selection",true);
			}
			PluginServices.getMainFrame().setSelectedTool("_selection");
			//idActiveLayer = editedLayers.size() - 1;
			ile=getLayerEdited(e.getSource());
			System.out.println("NUEVA CAPA EN EDICION: " + lyrEdit.getLayer().getName());
			//activationChanged(e);

			// Ponemos el resto de temas desactivados
			if (mapCtrl != null)
				mapCtrl.getMapContext().getLayers().setActive(false);
			// y activamos el nuevo.
			e.getSource().setActive(true);

			if (e.getSource() instanceof FLyrVect){
				FLyrVect fLyrVect = (FLyrVect)e.getSource();
				VectorialEditableAdapter vea =
					(VectorialEditableAdapter)fLyrVect.getSource();
				vea.addEditionListener(new EditionChangeManager(fLyrVect));
			}
		}else{
			for (int i = 0; i < editedLayers.size(); i++) {
				VectorialLayerEdited vle = (VectorialLayerEdited) editedLayers.get(i);
				if (vle.equals(lyrEdit)) {
					editedLayers.remove(i);
					ile=null;
					//idActiveLayer=-1;
					return;
				}
			}
		}



	}

	/**
	 * @return Returns the activeLayerEdited. Null if there isn't any active AND edited
	 */
	public ILayerEdited getActiveLayerEdited() {
		return ile;
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
			mapCtrl.getMapContext().getLayers().addLayerCollectionListener(this);
		}
	}

	public void layerAdded(LayerCollectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void layerMoved(LayerPositionEvent e) {
		// TODO Auto-generated method stub

	}

	public void layerRemoved(LayerCollectionEvent e) {
		VectorialLayerEdited vle=(VectorialLayerEdited)getActiveLayerEdited();
		if (vle!=null){
			//FLayers layers=getMapControl().getMapContext().getLayers();
			//if (layers.getLayersCount()>0)
			//	layers.getLayer(0).setActive(true);
			vle.clearSelection();
			editedLayers.remove(vle);
			getMapControl().setTool("zoomIn");
			FLyrVect lv=(FLyrVect)vle.getLayer();
			if (e.getAffectedLayer().equals(lv)){
				View view=(View)PluginServices.getMDIManager().getActiveWindow();
				view.hideConsole();
				view.validate();
				view.repaint();
			}
			PluginServices.getMainFrame().enableControls();
		}

	}

	public void layerAdding(LayerCollectionEvent e) throws CancelationException {
		// TODO Auto-generated method stub

	}

	public void layerMoving(LayerPositionEvent e) throws CancelationException {
		// TODO Auto-generated method stub

	}

	public void layerRemoving(LayerCollectionEvent e) throws CancelationException {
		// TODO Auto-generated method stub

	}

	public void activationChanged(LayerCollectionEvent e) throws CancelationException {
		// TODO Auto-generated method stub

	}

	public void visibilityChanged(LayerCollectionEvent e) throws CancelationException {
		// TODO Auto-generated method stub

	}



}
