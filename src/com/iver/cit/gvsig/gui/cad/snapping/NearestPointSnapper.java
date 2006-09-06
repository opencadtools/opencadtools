package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.Graphics;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class NearestPointSnapper extends AbstractSnapper implements ISnapperVectorial {

	public Point2D getSnapPoint(Point2D point, IGeometry geom, double tolerance, Point2D lastPointEntered) {
		Point2D resul = null;
		Coordinate c = new Coordinate(point.getX(), point.getY());
		
		PathIterator theIterator = geom.getPathIterator(null, FConverter.FLATNESS); //polyLine.getPathIterator(null, flatness);
		double[] theData = new double[6];
		double minDist = tolerance;
		Coordinate from = null, first = null;
		while (!theIterator.isDone()) {
			//while not done
			int theType = theIterator.currentSegment(theData);

			switch (theType) {
				case PathIterator.SEG_MOVETO:
					from = new Coordinate(theData[0], theData[1]);
					first = from;
					break;

				case PathIterator.SEG_LINETO:

					// System.out.println("SEG_LINETO");
					Coordinate to = new Coordinate(theData[0], theData[1]);
					LineSegment line = new LineSegment(from, to);
					Coordinate closestPoint = line.closestPoint(c);
					double dist = c.distance(closestPoint);
					if ((dist < minDist)) {
						resul = new Point2D.Double(closestPoint.x, closestPoint.y);
						minDist = dist;
					}
					
					from = to;
					break;
				case PathIterator.SEG_CLOSE:
					line = new LineSegment(from, first);
					closestPoint = line.closestPoint(c);
					dist = c.distance(closestPoint);
					if ((dist < minDist)) {
						resul = new Point2D.Double(closestPoint.x, closestPoint.y);
						minDist = dist;
					}
					
					from = first;
					break;
					

			} //end switch

			theIterator.next();
		}

		return resul;
	}

	public String getToolTipText() {
		return PluginServices.getText(this, "nearest_point");
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics, java.awt.geom.Point2D)
	 */
	public void draw(Graphics g, Point2D pPixels) {
		g.setColor(getColor());	
		int half = getSizePixels() / 2;
		int x1 = (int) (pPixels.getX() - half);
		int x2 = (int) (pPixels.getX() + half);
		int y1 = (int) (pPixels.getY() - half);
		int y2 = (int) (pPixels.getY() + half);
		
		g.drawLine(x1, y1, x2, y1); // abajo
		g.drawLine(x1, y2, x2, y2); // arriba
		g.drawLine(x1, y1, x2, y2); // abajo - arriba
		g.drawLine(x1, y2, x2, y1); // arriba - abajo
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getPriority()
	 */
	public int getPriority()
	{
		return 0;
	}

}
