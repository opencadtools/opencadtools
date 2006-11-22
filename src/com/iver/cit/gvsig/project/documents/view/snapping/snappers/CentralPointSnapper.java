package com.iver.cit.gvsig.project.documents.view.snapping.snappers;

import com.iver.andami.PluginServices;

import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.ICenterHandler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.project.documents.view.snapping.AbstractSnapper;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapperVectorial;

import java.awt.Graphics;
import java.awt.geom.Point2D;


/**
 * Central point snapper.
 *
 * @author Vicente Caballero Navarro
 */
public class CentralPointSnapper extends AbstractSnapper
    implements ISnapperVectorial {

	/* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getSnapPoint(Point2D point,
     * IGeometry geom,double tolerance, Point2D lastPointEntered)
     */
	public Point2D getSnapPoint(Point2D point, IGeometry geom,
        double tolerance, Point2D lastPointEntered) {
        Point2D resul = null;

        Handler[] handlers = geom.getHandlers(IGeometry.SELECTHANDLER);

        double minDist = tolerance;

        for (int j = 0; j < handlers.length; j++) {
            if (handlers[j] instanceof ICenterHandler) {
                Point2D handlerPoint = handlers[j].getPoint();
                double dist = handlerPoint.distance(point);

                if ((dist < minDist)) {
                    resul = handlerPoint;
                    minDist = dist;
                }
            }
        }

        return resul;
    }
	/* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getToolTipText()
     */
    public String getToolTipText() {
        return PluginServices.getText(this, "central_point");
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics, java.awt.geom.Point2D)
     */
    public void draw(Graphics g, Point2D pPixels) {
        g.setColor(getColor());

        int half = getSizePixels() / 2;
        g.drawOval((int) (pPixels.getX() - half),
            (int) (pPixels.getY() - half), getSizePixels(), getSizePixels());
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getPriority()
     */
    public int getPriority() {
        return 4;
    }
}
