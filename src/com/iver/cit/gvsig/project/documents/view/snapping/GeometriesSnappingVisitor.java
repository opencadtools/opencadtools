package com.iver.cit.gvsig.project.documents.view.snapping;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.ItemVisitor;

/**
 * SnappingVisitor with geometries.
 * 
 * @author Vicente Caballero Navarro
 */
public class GeometriesSnappingVisitor extends SnappingVisitor implements
	ItemVisitor {
    private ArrayList geometries = new ArrayList();
    private GeometryFactory geometryFactory = new GeometryFactory();

    public GeometriesSnappingVisitor(ISnapperVectorial snapper, Point2D point,
	    double mapTolerance, Point2D lastPointEntered) {
	super(snapper, point, mapTolerance, lastPointEntered);
    }

    @Override
    public void visitItem(Object item) {
	try {
	    IGeometry geom = (IGeometry) item;
	    Geometry geometry = geom.toJTSGeometry();
	    double distance = geometry.distance(geometryFactory
		    .createPoint(new Coordinate(queryPoint.getX(), queryPoint
			    .getY())));

	    if (distance < tolerance) {
		geometries.add(geom);
	    }
	} catch (Exception e) {
	}
    }

    @Override
    public Point2D getSnapPoint() {
	if (geometries.isEmpty()) {
	    return null;
	}

	IGeometry[] geoms = (IGeometry[]) geometries.toArray(new IGeometry[0]);
	// ((ISnapperGeometriesVectorial) snapper).setGeometries(geoms);

	Point2D result = null;

	for (int i = 0; i < geoms.length; i++) {
	    result = snapper.getSnapPoint(queryPoint, geoms[i], tolerance,
		    lastPointEntered);

	    if (result != null) {
		return result;
	    }
	}

	return result;
    }
}
