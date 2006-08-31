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

import javax.swing.ImageIcon;

import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.operations.Cancel;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;

public class ToolSelectGraphic implements PointListener{
	
	private final Image img = new ImageIcon(MapControl.class.getResource(
	"images/PointSelectCursor.gif")).getImage();
private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,
new Point(16, 16), "");
protected MapControl mapCtrl;

/**
* Crea un nuevo AreaListenerImpl.
*
* @param mc MapControl.
*/
public ToolSelectGraphic(MapControl mc) {
this.mapCtrl = mc;
}


	public void point(PointEvent event) throws BehaviorException {
        Point2D p = event.getPoint();
        Point2D mapPoint = mapCtrl.getViewPort().toMapPoint((int) p.getX(), (int) p.getY());

        // Tolerancia de 3 pixels
        double tol = mapCtrl.getViewPort().toMapDistance(3);
        GraphicLayer gLyr = mapCtrl.getMapContext().getGraphicsLayer();
        Rectangle2D recPoint = new Rectangle2D.Double(mapPoint.getX() - (tol / 2),
        		mapPoint.getY() - (tol / 2), tol, tol);

        FBitSet oldBitSet = gLyr.getSelection();
        
        FBitSet newBitSet = gLyr.queryByRect(recPoint);
        if (event.getEvent().isControlDown())
            newBitSet.xor(oldBitSet);
        gLyr.setSelection(newBitSet);

		mapCtrl.drawGraphics();
	}

	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		// TODO Auto-generated method stub
		
	}

	public Cursor getCursor() {
		return cur;
	}

	public boolean cancelDrawing() {
		return false;
	}

}


