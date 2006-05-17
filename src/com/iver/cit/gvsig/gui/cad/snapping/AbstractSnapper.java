package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public abstract class AbstractSnapper implements ISnapper {

	protected Point2D snapPoint = null; 
	public void setSnapPoint(Point2D snapPoint) {
		this.snapPoint = snapPoint;

	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics, java.awt.geom.Point2D)
	 */
	public void draw(Graphics g, Point2D pPixels) {
		g.setColor(Color.ORANGE);
		g.drawRect((int) (pPixels.getX() - 6),
				(int) (pPixels.getY() - 6), 12, 12);
		g.drawRect((int) (pPixels.getX() - 3),
				(int) (pPixels.getY() - 3), 6, 6);
		g.setColor(Color.MAGENTA);
		g.drawRect((int) (pPixels.getX() - 4),
				(int) (pPixels.getY() - 4), 8, 8);
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getPriority()
	 */
	public int getPriority()
	{
		return 0;
	}

}
