/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.gui.graphictools;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;

/**
 * <p>
 * Listener that selects the items of a {@link GraphicLayer GraphicLayer} that
 * their area intersects with the point selected on the associated
 * <code>MapControl</code>.
 * </p>
 * 
 * <p>
 * Listens a single click of any mouse's button.
 * </p>
 */
public class ToolSelectGraphic implements PointListener {

    // private final Image img = new ImageIcon(MapControl.class.getResource(
    // "images/PointSelectCursor.gif")).getImage();
    /**
     * The image to display when the cursor is active.
     */
    private final Image img = PluginServices.getIconTheme()
	    .get("rect-select-cursor").getImage();

    /**
     * The cursor used to work with this tool listener.
     * 
     * @see #getCursor()
     */
    private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,
	    new Point(16, 16), "");

    /**
     * Reference to the <code>MapControl</code> object that uses.
     */
    protected MapControl mapCtrl;

    /**
     * <p>
     * Creates a new <code>ToolSelectGraphic</code> object.
     * </p>
     * 
     * @param mc
     *            the <code>MapControl</code> where will be applied the changes
     */
    public ToolSelectGraphic(MapControl mc) {
	this.mapCtrl = mc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.
     * cit.gvsig.fmap.tools.Events.PointEvent)
     */
    @Override
    public void point(PointEvent event) throws BehaviorException {
	Point2D p = event.getPoint();
	Point2D mapPoint = mapCtrl.getViewPort().toMapPoint((int) p.getX(),
		(int) p.getY());

	// Tolerancia de 3 pixels
	double tol = mapCtrl.getViewPort().toMapDistance(3);
	GraphicLayer gLyr = mapCtrl.getMapContext().getGraphicsLayer();
	Rectangle2D recPoint = new Rectangle2D.Double(mapPoint.getX()
		- (tol / 2), mapPoint.getY() - (tol / 2), tol, tol);

	FBitSet oldBitSet = gLyr.getSelection();

	FBitSet newBitSet = gLyr.queryByRect(recPoint);
	if (event.getEvent().isControlDown()) {
	    newBitSet.xor(oldBitSet);
	}
	gLyr.setSelection(newBitSet);

	mapCtrl.drawGraphics();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#pointDoubleClick
     * (com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
     */
    @Override
    public void pointDoubleClick(PointEvent event) throws BehaviorException {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
     */
    @Override
    public Cursor getCursor() {
	return cur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
     */
    @Override
    public boolean cancelDrawing() {
	return false;
    }
}
