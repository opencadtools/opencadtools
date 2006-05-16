package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.IGeometry;

public interface ISnapper {
	
	Point2D getSnapPoint(Point2D queryPoint, IGeometry geomToSnap, double tolerance);

}
