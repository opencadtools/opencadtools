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
package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;


public abstract class DefaultCADTool implements CADTool {
	private CADToolAdapter cadToolAdapter;
	public void setCadToolAdapter(CADToolAdapter cta){
		cadToolAdapter = cta;
	}

	public CADToolAdapter getCadToolAdapter(){
		return cadToolAdapter;
	}

	public void drawLine(Graphics2D g, Point2D firstPoint, Point2D endPoint){
		GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
				2);
		elShape.moveTo(firstPoint.getX(), firstPoint.getY());
		elShape.lineTo(endPoint.getX(), endPoint.getY());
		ShapeFactory.createPolyline2D(elShape).draw(g,
			getCadToolAdapter().getMapControl().getViewPort(),
			CADTool.drawingSymbol);

	}
}
