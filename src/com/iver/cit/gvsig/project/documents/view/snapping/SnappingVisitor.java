package com.iver.cit.gvsig.project.documents.view.snapping;

import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.vividsolutions.jts.index.ItemVisitor;

/**
 * @author fjp
 * 
 *         Visitor adecuado para recorrer el índice espacial de JTS y no obligar
 *         a dar 2 pasadas. En la misma pasada que se visita, se calcula la
 *         distancia mínima.
 */
public class SnappingVisitor implements ItemVisitor {

    ISnapperVectorial snapper;
    Point2D snapPoint = null;
    Point2D queryPoint = null;
    Point2D lastPointEntered = null;

    double minDist = Double.MAX_VALUE;
    double distActual;
    double tolerance;

    public SnappingVisitor(ISnapperVectorial snapper, Point2D queryPoint,
	    double tolerance, Point2D lastPointEntered) {
	this.snapper = snapper;
	this.tolerance = tolerance;
	this.queryPoint = queryPoint;
	this.lastPointEntered = lastPointEntered;
	distActual = tolerance;
	// snapper.setSnapPoint(null);
    }

    public void visitItem(Object item) {
	IGeometry geom = (IGeometry) item;
	Point2D aux = snapper.getSnapPoint(queryPoint, geom, distActual,
		lastPointEntered);
	if (aux != null) {
	    snapPoint = aux;
	    minDist = snapPoint.distance(queryPoint);
	    distActual = minDist;
	    // snapper.setSnapPoint(snapPoint);
	}

    }

    public Point2D getSnapPoint() {

	return snapPoint;
    }

    // public double getMinDist() {
    // return minDist;
    // }

}
