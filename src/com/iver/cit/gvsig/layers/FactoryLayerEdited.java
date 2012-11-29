package com.iver.cit.gvsig.layers;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class FactoryLayerEdited {

    public static ILayerEdited createLayerEdited(FLayer lyr) {
	if (lyr instanceof FLyrVect)
	    return new VectorialLayerEdited(lyr);
	return null;
    }

}
