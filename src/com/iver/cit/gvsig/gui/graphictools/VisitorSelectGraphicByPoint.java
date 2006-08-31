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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.vividsolutions.jts.index.ItemVisitor;

/**
 * @author fjp
 *
 * @deprecated Use queryByRect
 */
public class VisitorSelectGraphicByPoint implements ItemVisitor{

	private Point2D mapPoint;
	private double tol;
	private FBitSet selection = new FBitSet();
	private int numReg;
	Rectangle2D recPoint;
	
	public VisitorSelectGraphicByPoint(Point2D mapPoint, double tolerance)
	{
		this.mapPoint = mapPoint;
		this.tol = tolerance;
		this.numReg = 0;
		
        recPoint = new Rectangle2D.Double(mapPoint.getX() - (tolerance / 2),
        		mapPoint.getY() - (tolerance / 2), tolerance, tolerance);

	}
	
	/* (non-Javadoc)
	 * @see com.vividsolutions.jts.index.ItemVisitor#visitItem(java.lang.Object)
	 * TODO: VENDRIA BIEN SABER EL NUMERO DE REGISTRO PARA PODER MARCARLO COMO SELECCIONADO
	 */
	public void visitItem(Object item) {
		FGraphic graf = (FGraphic) item;
		IGeometry geom = graf.getGeom();
		if (geom.intersects(recPoint))
		{
			selection.set(numReg);
		}
		numReg++;
			
	}

	public FBitSet getSelection() {
		return selection;
	}

}


