package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;

public class NearestPointSnapper implements ISnapper {

	public Point2D getSnapPoint(Point2D point, IGeometry geom, double tolerance) {
		Point2D resul = null;

		Handler[] handlers = geom.getHandlers(IGeometry.SELECTHANDLER);

		for (int j = 0; j < handlers.length; j++) {
			Point2D handlerPoint = handlers[j].getPoint();
			double dist = handlerPoint.distance(point);
			if ((dist < tolerance)) {
				resul = handlerPoint;
			}
		}

		return resul;
	}

}
