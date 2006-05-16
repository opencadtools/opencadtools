package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.vividsolutions.jts.index.ItemVisitor;

/**
 * @author fjp
 *
 * Visitor adecuado para recorrer el índice espacial de JTS y no obligar
 * a dar 2 pasadas. En la misma pasada que se visita, se calcula la distancia
 * mínima.
 */
public class SnappingVisitor implements ItemVisitor {

	ISnapper snapper;
	Point2D snapPoint = null;
	Point2D queryPoint = null;
	double minDist = Double.MAX_VALUE;
	double distActual;
	double tolerance;
	
	public SnappingVisitor(ISnapper snapper, Point2D queryPoint, double tolerance)
	{
		this.snapper = snapper;
		this.tolerance = tolerance;
		this.queryPoint = queryPoint;
		distActual = tolerance;
	}
	
	public void visitItem(Object item) {
		IGeometry geom = (IGeometry) item;
		Point2D aux  = snapper.getSnapPoint(queryPoint, geom, distActual);
		if (aux != null)
		{
			snapPoint = aux;
			minDist = snapPoint.distance(queryPoint);
			distActual = minDist;
		}
		
	}
	
	
	public Point2D getSnapPoint()
	{
		
		return snapPoint;
	}

	public double getMinDist() {
		return minDist;
	}

}
