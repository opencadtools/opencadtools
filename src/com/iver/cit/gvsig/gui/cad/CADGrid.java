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
package com.iver.cit.gvsig.gui.cad;

import com.iver.cit.gvsig.fmap.ViewPort;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * Clase encargada de gestionar las diferentes operaciones que se realizan
 * sobre el grid.
 *
 * @author Vicente Caballero Navarro
 */
public class CADGrid {
	private boolean grid = false;
	private double gridSize = 1000;
	private ViewPort viewport;
	private boolean adjustGrid;

	/**
	 * Inserta el viewPort.
	 *
	 * @param vp
	 */
	public void setViewPort(ViewPort vp) {
		viewport = vp;

//		if (gridSize == 0) {
//			gridSize = viewport.toMapDistance(25);
//		}
	}

	/**
	 * Ajusta un punto de la imagen que se pasa como  parámetro al handler más
	 * cercano si se encuentra lo suficientemente  cerca y devuelve la
	 * distancia del punto original al punto ajustado
	 *
	 * @param point
	 *
	 * @return Distancia del punto que se pasa como parámetro al punto ajustado
	 */
	public double adjustToGrid(Point2D point) {
		if (adjustGrid) {
			Point2D auxp = new Point2D.Double(0, 0);
			double x = ((point.getX() + gridSize) % gridSize) -
				((auxp.getX()) % gridSize);
			double y = ((point.getY() + gridSize) % gridSize) -
				((auxp.getY()) % gridSize);
			Point2D p = (Point2D) point.clone();
			if (x>gridSize/2){
				x=x-gridSize;
			}
			if (y>gridSize/2){
				y=y-gridSize;
			}
			point.setLocation((point.getX() - x), (point.getY() - y));
			return p.distance(point);
		}
		return Double.MAX_VALUE;
	}

	/**
	 * Dibuja el grid sobre el graphics que se pasa como parámetro
	 *
	 * @param g Graphics sobre el que dibujar el grid.
	 */
	public void drawGrid(Graphics g) {
		if (!grid) {
			return;
		}

		g.setColor(Color.lightGray);

		Rectangle2D extent = viewport.getAdjustedExtent();
		Point2D auxp = new Point2D.Double(0, 0);

		for (double i = extent.getMinX(); i < (extent.getMaxX() + gridSize);
				i += gridSize) {
			for (double j = extent.getMinY();
					j < (extent.getMaxY() + gridSize); j += gridSize) {
				Point2D po = new Point2D.Double(i, j);
				Point2D point = viewport.fromMapPoint(po);
				double x = ((po.getX() + gridSize) % gridSize) -
					((auxp.getX()) % gridSize);
				double y = ((po.getY() + gridSize) % gridSize) -
					((auxp.getY()) % gridSize);
				x = (point.getX() - viewport.fromMapDistance(x));
				y = (point.getY() + viewport.fromMapDistance(y));

				if (viewport.fromMapDistance(gridSize) > 3) {
					g.drawRect((int) x, (int) y, 1, 1);
				}
			}
		}
	}

	/**
	 * Inserta un boolean que indica si se utiliza o no el grid y de esta forma
	 * dibujarse.
	 *
	 * @param b boolean
	 */
	public void setUseGrid(boolean b) {
		grid = b;
	}

	/**
	 * Devuelve true si se usa el grid.
	 *
	 * @return True si se usa el grid.
	 */
	public boolean getUseGrid() {
		return grid;
	}

	/**
	 * Inserta un boolean que indica si se ajusta o no al grid y de esta forma
	 * dibujarse.
	 *
	 * @param b boolean
	 */
	public void setAdjustGrid(boolean b) {
		adjustGrid = b;
	}

	/**
	 * Devuelve true si se ha de ajustar al grid.
	 *
	 * @return True si se está ajustando al grid.
	 */
	public boolean getAdjustGrig() {
		return adjustGrid;
	}

	public double getGridSize() {
		return gridSize;
	}

	public void setGridSize(double gridSize) {
		this.gridSize = gridSize;
	}
}
