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

import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.EllipseCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.EllipseCADToolContext.EllipseCADToolState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class EllipseCADTool extends DefaultCADTool {
    private EllipseCADToolContext _fsm;
    private Point2D startAxis;
    private Point2D endAxis;

    /**
     * Crea un nuevo LineCADTool.
     */
    public EllipseCADTool() {
        _fsm = new EllipseCADToolContext(this);
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#end()
     */
    public void end() {
        _fsm = new EllipseCADToolContext(this);
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
        _fsm.addValue(sel, d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(FBitSet sel, String s) {
        //_fsm.addOption(sel,s);
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(FBitSet sel, double x, double y) {
        EllipseCADToolState actualState = (EllipseCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.Initial")) {
            startAxis = new Point2D.Double(x, y);
        } else if (status.equals("ExecuteMap.First")) {
            endAxis = new Point2D.Double(x, y);
        } else if (status.equals("ExecuteMap.Second")) {
            Point2D middle = new Point2D.Double((startAxis.getX() +
                    endAxis.getX()) / 2, (startAxis.getY() + endAxis.getY()) / 2);
            Point2D third = new Point2D.Double(x, y);
            double distance = middle.distance(third);
            addGeometry(ShapeFactory.createEllipse(startAxis, endAxis, distance));
        } else if (status.equals("ExecuteMap.Third")) {
            Point2D middle = new Point2D.Double((startAxis.getX() +
                    endAxis.getX()) / 2, (startAxis.getY() + endAxis.getY()) / 2);
            Point2D third = new Point2D.Double(x, y);
            double distance = middle.distance(third);
            addGeometry(ShapeFactory.createEllipse(startAxis, endAxis, distance));
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
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
        EllipseCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.First")) {
            drawLine((Graphics2D) g, startAxis, new Point2D.Double(x, y));
        } else if (status.equals("ExecuteMap.Second")) {
            Point2D middle = new Point2D.Double((startAxis.getX() +
                    endAxis.getX()) / 2, (startAxis.getY() + endAxis.getY()) / 2);

            Point2D third = new Point2D.Double(x, y);

            double distance = middle.distance(third);

            ShapeFactory.createEllipse(startAxis, endAxis, distance).draw((Graphics2D) g,
                getCadToolAdapter().getMapControl().getViewPort(),
                CADTool.modifySymbol);

            Point2D mediop = new Point2D.Double((startAxis.getX() +
                    endAxis.getX()) / 2, (startAxis.getY() + endAxis.getY()) / 2);
            drawLine((Graphics2D) g, mediop, third);
        }
    }

    /**
     * Add a diferent option.
     *
     * @param sel DOCUMENT ME!
     * @param s Diferent option.
     */
    public void addOption(FBitSet sel, String s) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(FBitSet sel, double d) {
        EllipseCADToolState actualState = (EllipseCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.Fourth")) {
            double distance = d;
            addGeometry(ShapeFactory.createEllipse(startAxis, endAxis, distance));
        }
    }
}
