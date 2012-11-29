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
import com.iver.cit.gvsig.gui.cad.tools.smc.CircleCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.CircleCADToolContext.CircleCADToolState;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class CircleCADTool extends InsertionCADTool {
    protected CircleCADToolContext _fsm;
    protected Point2D center;
    protected Point2D firstPoint;
    protected Point2D secondPoint;
    protected Point2D thirdPoint;

    /**
     * Crea un nuevo LineCADTool.
     */
    public CircleCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    @Override
    public void init() {
	_fsm = new CircleCADToolContext(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double, double)
     */
    @Override
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
    @Override
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
    @Override
    public void transition(String s) throws CommandException {
	if (!super.changeCommand(s)) {
	    _fsm.addOption(s);
	}
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     * 
     * @param sel
     *            Bitset con las geometrías que estén seleccionadas.
     * @param x
     *            parámetro x del punto que se pase en esta transición.
     * @param y
     *            parámetro y del punto que se pase en esta transición.
     */
    @Override
    public void addPoint(double x, double y, InputEvent event) {
	CircleCADToolState actualState = (CircleCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();

	if (status.equals("Circle.CenterPointOr3p")) {
	    center = new Point2D.Double(x, y);
	} else if (status == "Circle.PointOrRadius") {
	    addGeometry(ShapeFactory.createCircle(center, new Point2D.Double(x,
		    y)));
	} else if (status == "Circle.FirstPoint") {
	    firstPoint = new Point2D.Double(x, y);
	} else if (status == "Circle.SecondPoint") {
	    secondPoint = new Point2D.Double(x, y);
	} else if (status == "Circle.ThirdPoint") {
	    thirdPoint = new Point2D.Double(x, y);
	    addGeometry(ShapeFactory.createCircle(firstPoint, secondPoint,
		    thirdPoint));
	}
    }

    /**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param selectedGeometries
     *            BitSet con las geometrías seleccionadas.
     * @param x
     *            parámetro x del punto que se pase para dibujar.
     * @param y
     *            parámetro x del punto que se pase para dibujar.
     */
    @Override
    public void drawOperation(Graphics g, double x, double y) {
	CircleCADToolState actualState = _fsm.getState();
	String status = actualState.getName();

	if ((status == "Circle.CenterPointOr3p")) { // || (status == "5")) {

	    if (firstPoint != null) {
		drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y),
			DefaultCADTool.geometrySelectSymbol);
	    }
	}

	if (status == "Circle.PointOrRadius") {
	    Point2D currentPoint = new Point2D.Double(x, y);
	    ShapeFactory.createCircle(center, currentPoint).draw(
		    (Graphics2D) g,
		    getCadToolAdapter().getMapControl().getViewPort(),
		    DefaultCADTool.axisReferencesSymbol);
	} else if (status == "Circle.SecondPoint") {
	    drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y),
		    DefaultCADTool.geometrySelectSymbol);
	} else if (status == "Circle.ThirdPoint") {
	    Point2D currentPoint = new Point2D.Double(x, y);
	    IGeometry geom = ShapeFactory.createCircle(firstPoint, secondPoint,
		    currentPoint);

	    if (geom != null) {
		geom.draw((Graphics2D) g, getCadToolAdapter().getMapControl()
			.getViewPort(), DefaultCADTool.axisReferencesSymbol);
	    }
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
    @Override
    public void addOption(String s) {
	CircleCADToolState actualState = (CircleCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();

	if (status == "Circle.CenterPointOr3p") {
	    if (s.equalsIgnoreCase(PluginServices.getText(this,
		    "CircleCADTool.3p"))) {
		// Opción correcta.
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    @Override
    public void addValue(double d) {
	CircleCADToolState actualState = (CircleCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();

	if (status == "Circle.PointOrRadius") {
	    addGeometry(ShapeFactory.createCircle(center, d));
	}
    }

    @Override
    public String getName() {
	return PluginServices.getText(this, "circle_");
    }

    @Override
    public String toString() {
	return "_circle";
    }

    @Override
    public boolean isApplicable(int shapeType) {
	switch (shapeType) {
	case FShape.POINT:
	case FShape.MULTIPOINT:
	    return false;
	}
	return true;
    }

    @Override
    public void drawOperation(Graphics g, ArrayList pointList) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isMultiTransition() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void transition(InputEvent event) {
	// TODO Auto-generated method stub

    }

}
