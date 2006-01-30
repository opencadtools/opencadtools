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
import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolygonCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolygonCADToolContext.PolygonCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class PolygonCADTool extends DefaultCADTool {
    private PolygonCADToolContext _fsm;
    private Point2D center;
    private int numLines = 5;
    private boolean isI = true;

    /**
     * Crea un nuevo PolygonCADTool.
     */
    public PolygonCADTool() {
        _fsm = new PolygonCADToolContext(this);
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
        _fsm = new PolygonCADToolContext(this);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y) {
        _fsm.addPoint(x, y);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
     */
    public void transition(double d) {
        //_fsm.addValue(sel,d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(String s) {
        _fsm.addOption(s);
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como parámetro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y) {
        PolygonCADToolState actualState = (PolygonCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.Initial")) {
            center = new Point2D.Double(x, y);
        } else if (status.equals("ExecuteMap.First")) {
            center = new Point2D.Double(x, y);
        } else if (status.equals("ExecuteMap.Second") ||
                status.equals("ExecuteMap.Third")) {
            Point2D point = new Point2D.Double(x, y);

            //Polígono a partir de la circunferencia.
            if (isI) {
                addGeometry(getIPolygon(point, point.distance(center)));
            } else {
                addGeometry(getCPolygon(point, point.distance(center)));
            }
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
        PolygonCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.First") ||
                status.equals("ExecuteMap.Second") ||
                status.equals("ExecuteMap.Third")) {
            Point2D point = new Point2D.Double(x, y);
            drawLine((Graphics2D) g, center, point);

            if (isI) {
                getIPolygon(point, point.distance(center)).draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
            } else {
                getCPolygon(point, point.distance(center)).draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
            }

            ShapeFactory.createCircle(center, point.distance(center)).draw((Graphics2D) g,
                getCadToolAdapter().getMapControl().getViewPort(),
                CADTool.drawingSymbol);
        }
    }

    /**
     * Add a diferent option.
     *
     * @param sel DOCUMENT ME!
     * @param s Diferent option.
     */
    public void addOption(String s) {
        PolygonCADToolState actualState = (PolygonCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.Second")) {
            if (s.equals("C") || s.equals("c")) {
                isI = false;
            } else if (s.equals("I") || s.equals("i")) {
                isI = true;
            }
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
        PolygonCADToolState actualState = (PolygonCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.Initial")) {
            numLines = (int) d;
        } else if (status.equals("ExecuteMap.Second") ||
                status.equals("ExecuteMap.Third")) {
            double radio = d;

            //Polígono a partir de radio.
            Point2D point = UtilFunctions.getPoint(center,
                    new Point2D.Double(center.getX(), center.getY() + 10), radio);

            if (isI) {
                addGeometry(getIPolygon(point, radio));
            } else {
                addGeometry(getCPolygon(point, radio));
            }
        }
    }

    /**
     * Devuelve la geometría con el poligono regular circunscrito a la
     * circunferencia formada por el punto central y el radio que se froma
     * entre este y el puntero del ratón..
     *
     * @param point Puntero del ratón.
     * @param radio Radio
     *
     * @return GeometryCollection con las geometrías del polígono.
     */
    private IGeometry getCPolygon(Point2D point, double radio) {
        IGeometry[] geoms = new IGeometry[numLines];
        Point2D p1 = UtilFunctions.getPoint(center, point, radio);

        double initangle = UtilFunctions.getAngle(center, point);
        Point2D antPoint = p1;
        Point2D antInter = null;
        double an = (Math.PI * 2) / numLines;

        for (int i = 1; i < (numLines + 2); i++) {
            Point2D p2 = UtilFunctions.getPoint(center, (an * i) + initangle,
                    radio);
            Point2D[] ps1 = UtilFunctions.getPerpendicular(antPoint, center,
                    antPoint);
            Point2D[] ps2 = UtilFunctions.getPerpendicular(p2, center, p2);
            Point2D inter = UtilFunctions.getIntersection(ps1[0], ps1[1],
                    ps2[0], ps2[1]);

            if (antInter != null) {
                GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                        2);
                elShape.moveTo(antInter.getX(), antInter.getY());
                elShape.lineTo(inter.getX(), inter.getY());

                geoms[i - 2] = (ShapeFactory.createPolyline2D(elShape));
            }

            antInter = inter;
            antPoint = p2;
        }

        return new FGeometryCollection(geoms);
    }

    /**
     * Devuelve la geometría con el poligono regular inscrito en la
     * circunferencia.
     *
     * @param point Puntero del ratón.
     * @param radio Radio
     *
     * @return GeometryCollection con las geometrías del polígono.
     */
    private IGeometry getIPolygon(Point2D point, double radio) {
        IGeometry[] geoms = new IGeometry[numLines];
        Point2D p1 = UtilFunctions.getPoint(center, point, radio);
        double initangle = UtilFunctions.getAngle(center, point);
        Point2D antPoint = p1;
        double an = (Math.PI * 2) / numLines;

        for (int i = 1; i < (numLines + 1); i++) {
            Point2D p2 = UtilFunctions.getPoint(center, (an * i) + initangle,
                    radio);
            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    2);
            elShape.moveTo(antPoint.getX(), antPoint.getY());
            elShape.lineTo(p2.getX(), p2.getY());

            geoms[i - 1] = (ShapeFactory.createPolyline2D(elShape));
            antPoint = p2;
        }

        return new FGeometryCollection(geoms);
    }
}
