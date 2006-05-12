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

import java.awt.Graphics;
import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommadException;
import com.iver.cit.gvsig.gui.cad.tools.smc.PointCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.PointCADToolContext.PointCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class PointCADTool extends DefaultCADTool {
    private PointCADToolContext _fsm;

    /**
     * Crea un nuevo PointCADTool.
     */
    public PointCADTool() {

    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    	_fsm = new PointCADToolContext(this);
    }

    /**
     * DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     * @param sel DOCUMENT ME!
     */
    public void transition(double x, double y, InputEvent event) {
        _fsm.addPoint(x, y, event);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
     */
    public void transition(double d) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(String s) throws CommadException {
    	if (!super.changeCommand(s)){
    		_fsm.addOption(s);
    	}
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y,InputEvent event) {
        PointCADToolState actualState = (PointCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Point.FirstPoint")) {
            addGeometry(ShapeFactory.createPoint2D(x, y));
        }
    }

    /**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometrías seleccionadas.
     * @param x parámetro x del punto que se pase para dibujar.
     * @param y parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x,
        double y) {
    }

    /**
     * Add a diferent option.
     *
     * @param sel DOCUMENT ME!
     * @param s Diferent option.
     */
    public void addOption(String s) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
        // TODO Auto-generated method stub
    }

	public String getName() {
		return PluginServices.getText(this,"point_");
	}

	public String toString() {
		return "_point";
	}
	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.POLYGON:
		case FShape.LINE:
			return false;
		}
		return true;
	}
}
