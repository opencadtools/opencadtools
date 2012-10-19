/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.ArcCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.ArcCADToolContext.ArcCADToolState;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class ArcCADTool extends DefaultCADTool {
    protected ArcCADToolContext _fsm;
    protected Point2D p1;
    protected Point2D p2;
    protected Point2D p3;

    /**
     * Crea un nuevo LineCADTool.
     */
    public ArcCADTool() {
    }

    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    @Override
    public void init() {
	_fsm = new ArcCADToolContext(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double, double)
     */
    public void transition(double x, double y, InputEvent event) {
	_fsm.addPoint(x, y, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double)
     */
    public void transition(double d) {
	_fsm.addValue(d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, java.lang.String)
     */
    public void transition(String s) throws CommandException {
	if (!super.changeCommand(s)) {
	    _fsm.addOption(s);
	}
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como par� metro el
     * editableFeatureSource que ya estar� creado.
     * 
     * @param sel
     *            Bitset con las geometr�as que est�n seleccionadas.
     * @param x
     *            par�metro x del punto que se pase en esta transici�n.
     * @param y
     *            par�metro y del punto que se pase en esta transici�n.
     */
    public void addPoint(double x, double y, InputEvent event) {
	ArcCADToolState actualState = (ArcCADToolState) _fsm.getPreviousState();
	String status = actualState.getName();

	if (status.equals("Arc.FirstPoint")) {
	    p1 = new Point2D.Double(x, y);
	} else if (status.equals("Arc.SecondPoint")) {
	    p2 = new Point2D.Double(x, y);
	} else if (status.equals("Arc.ThirdPoint")) {
	    p3 = new Point2D.Double(x, y);

	    IGeometry ig = ShapeFactory.createArc(p1, p2, p3);

	    if (ig != null) {
		addGeometry(ig);
	    }
	}
    }

    /**
     * M�todo para dibujar lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param selectedGeometries
     *            BitSet con las geometr�as seleccionadas.
     * @param x
     *            par�metro x del punto que se pase para dibujar.
     * @param y
     *            par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {
	ArcCADToolState actualState = _fsm.getState();
	String status = actualState.getName();

	if (status.equals("Arc.SecondPoint")) {
	    drawLine((Graphics2D) g, p1, new Point2D.Double(x, y),
		    geometrySelectSymbol);
	} else if (status.equals("Arc.ThirdPoint")) {
	    Point2D current = new Point2D.Double(x, y);
	    IGeometry ig = ShapeFactory.createArc(p1, p2, current);

	    if (ig != null) {
		ig.draw((Graphics2D) g, getCadToolAdapter().getMapControl()
			.getViewPort(), DefaultCADTool.geometrySelectSymbol);
	    }

	    Point2D p = getCadToolAdapter().getMapControl().getViewPort()
		    .fromMapPoint(p1.getX(), p1.getY());
	    g.drawRect((int) p.getX(), (int) p.getY(), 1, 1);
	    p = getCadToolAdapter().getMapControl().getViewPort()
		    .fromMapPoint(p2.getX(), p2.getY());
	    g.drawRect((int) p.getX(), (int) p.getY(), 1, 1);
	}
    }

    /**
     * Add a diferent option.
     * 
     * @param sel
     *            DOCUMENT ME!
     * @param s
     *            Diferent option.
     */
    public void addOption(String s) {
	// TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }

    public String getName() {
	return PluginServices.getText(this, "arc_");
    }

    @Override
    public String toString() {
	return "_arc";
    }

    @Override
    public boolean isApplicable(int shapeType) {
	switch (shapeType) {
	case FShape.POINT:
	case FShape.POLYGON:
	case FShape.MULTIPOINT:
	    return false;
	}
	return true;
    }

    public void drawOperation(Graphics g, ArrayList pointList) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isMultiTransition() {
	// TODO Auto-generated method stub
	return false;
    }

    public void transition(InputEvent event) {
	// TODO Auto-generated method stub

    }

}
