package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * @author fjp
 *
 */
public interface ISnapper {
	
	Point2D getSnapPoint(Point2D queryPoint, IGeometry geomToSnap, double tolerance, Point2D lastPointEntered);

	void setSnapPoint(Point2D snapPoint);
	
	void draw(Graphics g, Point2D pPixels);

	String getToolTipText();
	
	/**
	 * Implement this if you need a Snapper more important than the others.
	 * Default value is 0 (no prority).
	 * @return
	 */
	int getPriority();

}
