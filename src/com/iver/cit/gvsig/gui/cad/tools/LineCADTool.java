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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.ShapeMFactory;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext.LineCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class LineCADTool extends DefaultCADTool {
    protected LineCADToolContext _fsm;
    protected Point2D firstPoint;
    protected Point2D lastPoint;
    protected double angle;
    protected double length;

    /**
     * Crea un nuevo LineCADTool.
     */
    public LineCADTool() {

    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    	 _fsm = new LineCADToolContext(this);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y, InputEvent event) {
        _fsm.addPoint(x, y, event);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
     */
    public void transition(double d) {
        _fsm.addValue(d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(String s) throws CommandException {
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
        LineCADToolState actualState = (LineCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Line.FirstPoint")) {
            firstPoint = new Point2D.Double(x, y);
        } else if (status == "Line.SecondPointOrAngle") {
            lastPoint = new Point2D.Double(x, y);

            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    2);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), lastPoint.getY());
            //Mcoord
            IGeometry geom = ShapeFactory.createPolyline2D(elShape);
            try {
            	if (((FLyrVect)getVLE().getLayer()).getShapeType() == (FShape.LINE|FShape.M)){
            		geom = ShapeMFactory.createPolyline2DM(elShape,
            				new double[((FPolyline2D)geom.getInternalShape()).getSelectHandlers().length]);
            	}
            } catch (ReadDriverException e) {
            	NotificationManager.addError(e);
            }
            addGeometry(geom);
            firstPoint = (Point2D) lastPoint.clone();
        } else if (status == "Line.LenghtOrPoint") {
            length = firstPoint.distance(x, y);

            double w = (Math.cos(Math.toRadians(angle))) * length;
            double h = (Math.sin(Math.toRadians(angle))) * length;
            lastPoint = new Point2D.Double(firstPoint.getX() + w,
                    firstPoint.getY() + h);

            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    2);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), lastPoint.getY());
            //Mcoord
            IGeometry geom = ShapeFactory.createPolyline2D(elShape);
            try {
            	if (((FLyrVect)getVLE().getLayer()).getShapeType() == (FShape.LINE|FShape.M)){
            		geom = ShapeMFactory.createPolyline2DM(elShape,
            				new double[((FPolyline2D)geom.getInternalShape()).getSelectHandlers().length]);
            	}
            } catch (ReadDriverException e) {
            	NotificationManager.addError(e);
            }
            addGeometry(geom);

            firstPoint = (Point2D) lastPoint.clone();
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
    public void drawOperation(Graphics g,double x,
        double y) {
        LineCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if ((status != "Line.FirstPoint")) { // || (status == "5")) {

            if (firstPoint != null) {
                drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y),DefaultCADTool.geometrySelectSymbol);
            }
        }
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
        LineCADToolState actualState = (LineCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status == "Line.SecondPointOrAngle") {
            angle = d;
        } else if (status == "Line.LenghtOrPoint") {
            length = d;

            double w = Math.cos(Math.toRadians(angle)) * length;
            double h = Math.sin(Math.toRadians(angle)) * length;
            lastPoint = new Point2D.Double(firstPoint.getX() + w,
                    firstPoint.getY() + h);

            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    2);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), lastPoint.getY());
            addGeometry(ShapeFactory.createPolyline2D(elShape));
            firstPoint = (Point2D) lastPoint.clone();
        }
    }

	public String getName() {
		return PluginServices.getText(this,"line_");
	}

	public String toString() {
		return "_line";
	}
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

	public boolean isMultiTransition() {
		// TODO Auto-generated method stub
		return false;
	}

	public void transition(InputEvent event) {
		// TODO Auto-generated method stub

	}
}
