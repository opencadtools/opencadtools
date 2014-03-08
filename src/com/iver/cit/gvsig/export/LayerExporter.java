package com.iver.cit.gvsig.export;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public interface LayerExporter {
    void export(MapContext mapContext, FLyrVect layer);
}
