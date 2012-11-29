package com.iver.cit.gvsig.layers;

import com.iver.cit.gvsig.fmap.layers.FLayer;

/**
 * @author fjp
 * 
 *         Clase padre de las capas en edición, por si alguna vez necesitamos
 *         editar Raster
 */
public class DefaultLayerEdited implements ILayerEdited {
    private FLayer lyr;

    public DefaultLayerEdited(FLayer lyr) {
	this.lyr = lyr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.ILayerEdited#getLayer()
     */
    @Override
    public FLayer getLayer() {
	return lyr;
    }
}
