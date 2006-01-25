package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext.LineCADToolState;


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
public class LineCADTool extends DefaultCADTool {
    private LineCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;
    private double angle;
    private double length;

    /**
     * Crea un nuevo LineCADTool.
     */
    public LineCADTool() {
        _fsm = new LineCADToolContext(this);
    }

    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#end()
     */
    public void end() {
    }

    /* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
	 */
    public void transition(FBitSet sel, double x, double y) {
        _fsm.addPoint(sel, x, y);
    }
    /* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
	 */
	public void transition(FBitSet sel, double d) {
		_fsm.addValue(sel,d);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
	 */
	public void transition(FBitSet sel, String s) {
		//_fsm.addOption(sel,s);
	}
    /**
     * Equivale al transition del prototipo pero sin pasarle como par� metro el
     * editableFeatureSource que ya estar� creado.
     *
     * @param sel Bitset con las geometr�as que est�n seleccionadas.
     * @param x par�metro x del punto que se pase en esta transici�n.
     * @param y par�metro y del punto que se pase en esta transici�n.
     */
    public void addPoint(FBitSet sel, double x, double y) {
        LineCADToolState actualState = (LineCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        if (status.equals("ExecuteMap.Initial")) {
            firstPoint = new Point2D.Double(x, y);
        } else if (status == "ExecuteMap.First") {
            lastPoint = new Point2D.Double(x, y);
            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                      2);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), lastPoint.getY());
            addGeometry(ShapeFactory.createPolyline2D(elShape));
            firstPoint = (Point2D) lastPoint.clone();
        } else if (status == "ExecuteMap.Fourth") {
            length = firstPoint.distance(x, y);

            double w = (Math.cos(Math.toRadians(angle))) * length;
            double h = (Math.sin(Math.toRadians(angle))) * length;
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

    /**
     * M�todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometr�as seleccionadas.
     * @param x par�metro x del punto que se pase para dibujar.
     * @param y par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
        LineCADToolState actualState = _fsm.getState();
        String status = actualState.getName();
        if ((status != "ExecuteMap.Initial")) { // || (status == "5")) {

            if (firstPoint != null) {
                drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y));
            }
        }
    }

    /**
     * Add a diferent option.
     *
     * @param s Diferent option.
     */
    public void addOption(FBitSet sel,String s) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(FBitSet sel,double d) {
        LineCADToolState actualState = (LineCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status == "ExecuteMap.Second") {
            angle = d;
        } else if (status == "ExecuteMap.Third") {
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


}
