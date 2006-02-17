package com.iver.cit.gvsig.layers;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;

public class VectorialLayerEdited implements ILayerEdited {
	private FLayer lyr;
	private CADToolAdapter cadToolAdapter;
	
	public VectorialLayerEdited(FLayer lyr)
	{
		this.lyr = lyr;
		cadToolAdapter = new CADToolAdapter();
	}
	
	/**
	 * @return Returns the cadToolAdapter.
	 */
	public CADToolAdapter getCadToolAdapter() {
		return cadToolAdapter;
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.ILayerEdited#getLayer()
	 */
	public FLayer getLayer() {
		return lyr;
	}
}
