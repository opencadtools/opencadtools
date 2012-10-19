package com.iver.cit.gvsig.project.documents.view.snapping.snappers;

import java.awt.Graphics;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.project.documents.view.snapping.AbstractSnapper;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapperGeometriesVectorial;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;

/**
 * Intersection point snapper.
 * 
 * @author Vicente Caballero Navarro
 */
public class IntersectionPointSnapper extends AbstractSnapper implements
	ISnapperGeometriesVectorial {
    private static int maxPointsGeom = 200;
    private List geometries = new ArrayList<IGeometry>();

    public IntersectionPointSnapper() {
	System.err.println("Construido IntersectionPoinSnapper");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getSnapPoint(Point2D
     * point, IGeometry geom,double tolerance, Point2D lastPointEntered)
     */
    public Point2D getSnapPoint(Point2D point, IGeometry geom,
	    double tolerance, Point2D lastPointEntered) {
	// FJP: Seleccionamos con el punto y la tolerancia los segmentos que
	// tocan ese rectángulo.
	// Si hay más de dos, entonces calculamos su intersección y comprobamos
	// si algún punto de esa intersección cae
	// dentro de ese rectángulo.
	// OJO: No convertimos todas las geometrías ni calculamos la
	// intersección de todas las geometrías. Si no se quiere que se haga
	// snapping a los puntos contiguos, habría que meter un if para no
	// incluirlos en la lista de candidatos (si comparten un punto, no
	// añadirlo a la lista).
	// if (!(geom.getInternalShape() instanceof FPolyline2D)) {
	// return null;
	// }
	Point2D result = null;
	if (geometries == null) {
	    return null;
	}
	Coordinate c = new Coordinate(point.getX(), point.getY());
	Rectangle2D.Double r = new Rectangle2D.Double(point.getX(),
		point.getY(), tolerance, tolerance);
	// Lista con los segmentos candidatos
	ArrayList candidates = new ArrayList();
	for (int i = 0; i < geometries.size(); i++) {
	    IGeometry auxGeom = (IGeometry) geometries.get(i);
	    ArrayList lineSegments = getLineIntersection(auxGeom, c, tolerance);
	    candidates.addAll(lineSegments);
	}
	if (candidates.size() < 2) {
	    return null;
	}
	Geometry ant = null;
	double minDist = tolerance;
	for (int i = 0; i < candidates.size(); i++) {
	    LineSegment l1 = (LineSegment) candidates.get(i);
	    for (int j = i; j < candidates.size(); j++) {
		LineSegment l2 = (LineSegment) candidates.get(j);
		Coordinate cI = l1.intersection(l2);
		if (cI == null) {
		    continue;
		}
		double dist = cI.distance(c);
		if ((dist < minDist)) {
		    result = new Point2D.Double(cI.x, cI.y);
		    minDist = dist;
		}

	    }
	}
	return result;
    }

    public ArrayList getLineIntersection(IGeometry g, Coordinate c, double tol) {
	// return gp.intersects(r);
	// Más exacto
	boolean bool = false;
	ArrayList lineSegments = new ArrayList();
	int theType;
	// Use this array to store segment coordinate data
	double[] theData = new double[6];
	PathIterator theIterator;

	theIterator = g.getPathIterator(null, FConverter.FLATNESS);
	ArrayList arrayCoords = new ArrayList();
	while (!theIterator.isDone()) {
	    theType = theIterator.currentSegment(theData);
	    if (theType == PathIterator.SEG_MOVETO) {
		arrayCoords.add(new Point2D.Double(theData[0], theData[1]));
	    } else if (theType == PathIterator.SEG_LINETO) {
		arrayCoords.add(new Point2D.Double(theData[0], theData[1]));
		Point2D pAnt = (Point2D) arrayCoords
			.get(arrayCoords.size() - 2);
		LineSegment l = new LineSegment(pAnt.getX(), pAnt.getY(),
			theData[0], theData[1]);
		if (l.distancePerpendicular(c) < tol) {
		    bool = true;
		    lineSegments.add(l);
		}
	    } else if (theType == PathIterator.SEG_CLOSE) {
		Point2D firstPoint = (Point2D) arrayCoords.get(0);
		Point2D pAnt = (Point2D) arrayCoords
			.get(arrayCoords.size() - 1);
		LineSegment l = new LineSegment(pAnt.getX(), pAnt.getY(),
			firstPoint.getX(), firstPoint.getY());
		if (l.distancePerpendicular(c) < tol) {
		    bool = true;
		    lineSegments.add(l);
		}
	    }
	    theIterator.next();
	}
	return lineSegments;
    }

    // private Point2D intersects(IGeometry g1, IGeometry g2, Point2D point,
    // double tolerance) {
    // Geometry g1JTS = g1.toJTSGeometry();
    // Geometry g2JTS = g2.toJTSGeometry();
    // // if (g1JTS.getNumPoints()>maxPointsGeom ||
    // // g2JTS.getNumPoints()>maxPointsGeom){
    // // return null;
    // // }
    // Geometry intersection = g1JTS.intersection(g2JTS);
    // IGeometry g = FConverter.jts_to_igeometry(intersection);
    //
    // if (g != null && g.getGeometryType() == FShape.POINT) {
    // return g.getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();
    // }
    // return null;
    // }

    /**
     * DOCUMENT ME!
     * 
     * @param g1
     *            DOCUMENT ME!
     * @param g2
     *            DOCUMENT ME!
     * @param point
     *            DOCUMENT ME!
     * @param tolerance
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    // private Point2D intersects(IGeometry g1, IGeometry g2, Point2D point,
    // double tolerance) {
    // Point2D resul = null;
    // Coordinate c = new Coordinate(point.getX(), point.getY());
    // PathIterator theIterator = g1.getPathIterator(null, FConverter.FLATNESS);
    // double[] theData = new double[6];
    // Coordinate from = null;
    // Coordinate first = null;
    // LineSegment[] lines = getLines(g2);
    // while (!theIterator.isDone()) {
    // int theType = theIterator.currentSegment(theData);
    //
    // switch (theType) {
    // case PathIterator.SEG_MOVETO:
    // from = new Coordinate(theData[0], theData[1]);
    // first = from;
    //
    // break;
    //
    // case PathIterator.SEG_LINETO:
    //
    // Coordinate to = new Coordinate(theData[0], theData[1]);
    // LineSegment segmentLine = new LineSegment(from,to);
    // for (int i = 0; i < lines.length; i++) {
    // // if (lines[i].equals(segmentLine)) {
    // // continue;
    // // }
    // Coordinate intersects = segmentLine.intersection(lines[i]);
    // if (intersects == null || lines[i].equals(segmentLine)) {
    // continue;
    // }
    //
    // double dist = c.distance(intersects);
    //
    // if ((dist < tolerance)) {
    // resul = new Point2D.Double(intersects.x, intersects.y);
    // return resul;
    // }
    // }
    //
    // from = to;
    //
    // break;
    //
    // case PathIterator.SEG_CLOSE:
    // LineSegment segment = new LineSegment(from,first);
    //
    // for (int i = 0; i < lines.length; i++) {
    // // if (lines[i].equals(segment)) {
    // // continue;
    // // }
    //
    // Coordinate intersects = segment.intersection(lines[i]);
    //
    // if (intersects == null) {
    // continue;
    // }
    //
    // double dist = c.distance(intersects);
    //
    // if ((dist < tolerance)) {
    // resul = new Point2D.Double(intersects.x, intersects.y);
    // return resul;
    // }
    // }
    //
    // from = first;
    //
    // break;
    // } //end switch
    //
    // theIterator.next();
    // }
    // return resul;
    // }
    /**
     * DOCUMENT ME!
     * 
     * @param g
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    // private LineSegment[] getLines(IGeometry g) {
    // ArrayList lines = new ArrayList();
    // PathIterator theIterator = g.getPathIterator(null, FConverter.FLATNESS);
    // double[] theData = new double[6];
    // Coordinate from = null;
    // Coordinate first = null;
    //
    // while (!theIterator.isDone()) {
    // //while not done
    // int theType = theIterator.currentSegment(theData);
    //
    // switch (theType) {
    // case PathIterator.SEG_MOVETO:
    // from = new Coordinate(theData[0], theData[1]);
    // first = from;
    //
    // break;
    //
    // case PathIterator.SEG_LINETO:
    //
    // Coordinate to = new Coordinate(theData[0], theData[1]);
    // LineSegment line = new LineSegment(from, to);
    // lines.add(line);
    // from = to;
    //
    // break;
    //
    // case PathIterator.SEG_CLOSE:
    // line = new LineSegment(from, first);
    // lines.add(line);
    // from = first;
    //
    // break;
    // } //end switch
    //
    // theIterator.next();
    // }
    //
    // return (LineSegment[]) lines.toArray(new LineSegment[0]);
    // }
    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics,
     * java.awt.geom.Point2D)
     */
    public void draw(Graphics g, Point2D pPixels) {
	g.setColor(getColor());

	int half = getSizePixels() / 2;
	int x1 = (int) (pPixels.getX() - half);
	int x2 = (int) (pPixels.getX() + half);
	int y1 = (int) (pPixels.getY() - half);
	int y2 = (int) (pPixels.getY() + half);

	g.drawLine(x1, y1, x2, y2);
	g.drawLine(x1, y2, x2, y1);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param geoms
     *            DOCUMENT ME!
     */
    public void setGeometries(List geoms) {
	this.geometries = geoms;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getToolTipText()
     */
    public String getToolTipText() {
	return PluginServices.getText(this, "intersection_point");
    }
}
