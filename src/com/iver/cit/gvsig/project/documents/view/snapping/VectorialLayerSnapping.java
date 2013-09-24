package com.iver.cit.gvsig.project.documents.view.snapping;

import java.util.ArrayList;
import java.util.Collection;

import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.EditionManager;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * Helper class for define the snapping configuration of a vectorial layer
 * 
 * @author Francisco Puga <fpuga@cartolab.es>
 * 
 */
public class VectorialLayerSnapping {

    private FLyrVect layer;

    public VectorialLayerSnapping(FLyrVect layer) {
	this.layer = layer;
    }

    // fpuga: If FLayers was a real Collection this API will be cleaner. Try to
    // avoid the use of this method as it is here for back compatibility

    public void setSnappinTo(FLayers layers) {
	EditionManager editionManager = CADExtension.getEditionManager();
	VectorialLayerEdited vle = (VectorialLayerEdited) editionManager
		.getLayerEdited(layer);
	setSnappers(vle, layers);
    }

    public void setSnappers(Collection<FLyrVect> layers) {
	EditionManager editionManager = CADExtension.getEditionManager();
	VectorialLayerEdited vle = (VectorialLayerEdited) editionManager
		.getLayerEdited(layer);
	ArrayList<FLyrVect> layersToSnap = new ArrayList<FLyrVect>();
	for (FLyrVect newLayerToSnap : layers) {
	    if (newLayerToSnap == null) {
		continue;
	    }
	    if (newLayerToSnap.isVisible()) {
		layersToSnap.add(newLayerToSnap);
		newLayerToSnap.setSpatialCacheEnabled(true);
		// a layer reload is needed to get snappers working...
		// try {
		// if (vle.getLayer() != lyrVect) {
		// lyrVect.reload();
		// }
		// } catch (ReloadLayerException e) {
		// Logger.getLogger(EditionPreferencePage.class).error("Error reloading layer",
		// e);
		// }
	    }
	}
	vle.setLayersToSnap(layersToSnap);
    }

    private void setSnappers(VectorialLayerEdited vle, FLayers layers) {

	ArrayList<FLyrVect> layersToSnap = new ArrayList<FLyrVect>();
	for (int i = 0; i < layers.getLayersCount(); i++) {
	    FLayer layer = layers.getLayer(i);
	    if (layer instanceof FLayers) {
		setSnappers(vle, (FLayers) layer);
	    } else if ((layer instanceof FLyrVect) && (layer.isVisible())) {
		FLyrVect lyrVect = (FLyrVect) layer;
		layersToSnap.add(lyrVect);
		lyrVect.setSpatialCacheEnabled(true);
		// a layer reload is needed to get snappers working...
		// try {
		// if (vle.getLayer() != lyrVect) {
		// lyrVect.reload();
		// }
		// } catch (ReloadLayerException e) {
		// Logger.getLogger(EditionPreferencePage.class).error("Error reloading layer",
		// e);
		// }
	    }
	}
	vle.setLayersToSnap(layersToSnap);
    }

}
