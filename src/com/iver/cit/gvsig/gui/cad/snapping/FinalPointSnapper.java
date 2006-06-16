package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;

public class FinalPointSnapper extends AbstractSnapper {

	public Point2D getSnapPoint(Point2D point, IGeometry geom, double tolerance, Point2D lastPointEntered) {
		Point2D resul = null;

		Handler[] handlers = geom.getHandlers(IGeometry.SELECTHANDLER);

		double minDist = tolerance;
		for (int j = 0; j < handlers.length; j++) {
			Point2D handlerPoint = handlers[j].getPoint();
			double dist = handlerPoint.distance(point);
			if ((dist < minDist)) {
				resul = handlerPoint;
				minDist = dist;
			}
		}

		return resul;
	}

	public String getToolTipText() {
		return PluginServices.getText(this, "final_point");
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics, java.awt.geom.Point2D)
	 */
	public void draw(Graphics g, Point2D pPixels) {
		g.setColor(getColor());	
//		g.drawRect((int) (pPixels.getX() - 6),
//				(int) (pPixels.getY() - 6), 12, 12);
//		g.drawRect((int) (pPixels.getX() - 3),
//				(int) (pPixels.getY() - 3), 6, 6);
//		g.setColor(Color.MAGENTA);
//		g.drawRect((int) (pPixels.getX() - 4),
//				(int) (pPixels.getY() - 4), 8, 8);
		int half = getSizePixels() / 2;
		g.drawRect((int) (pPixels.getX() - half),
				(int) (pPixels.getY() - half),
				getSizePixels(), getSizePixels());
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getPriority()
	 */
	public int getPriority()
	{
		return 3;
	}

}
