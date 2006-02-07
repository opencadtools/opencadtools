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
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolylineCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolylineCADToolContext.PolylineCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class PolylineCADTool extends DefaultCADTool {
    private PolylineCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D antPoint;
    private Point2D antantPoint;
    private Point2D antCenter;
    private Point2D antInter;
    private ArrayList list = new ArrayList();

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public PolylineCADTool() {

    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    	_fsm = new PolylineCADToolContext(this);
    }

    public void endGeometry() {
    	IGeometry[] geoms = (IGeometry[]) list.toArray(new IGeometry[0]);
        FGeometryCollection fgc = new FGeometryCollection(geoms);
        addGeometry(fgc);
        _fsm = new PolylineCADToolContext(this);
        list.clear();
        antantPoint=antCenter=antInter=antPoint=firstPoint=null;
    }
    public void closeGeometry(){
    	GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
				2);
		elShape.moveTo(antPoint.getX(), antPoint.getY());
		elShape.lineTo(firstPoint.getX(), firstPoint.getY());

		list.add(ShapeFactory.createPolyline2D(elShape));
		list.add(ShapeFactory.createPolyline2D(elShape));

    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y) {
        ((PolylineCADToolContext)_fsm).addPoint(x, y);
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
        ((PolylineCADToolContext)_fsm).addOption(s);
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
    	PolylineCADToolState actualState = (PolylineCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Polyline.FirstPoint")) {
            antPoint = new Point2D.Double(x, y);

            if (firstPoint == null) {
                firstPoint = (Point2D) antPoint.clone();
            }
        } else if (status.equals("Polyline.NextPointOrArcOrClose")) {
            Point2D point = new Point2D.Double(x, y);

            if (antPoint != null) {
                GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                        2);
                elShape.moveTo(antPoint.getX(), antPoint.getY());
                elShape.lineTo(point.getX(), point.getY());
				list.add(ShapeFactory.createPolyline2D(elShape));

            }

            if (antPoint != null) {
                antantPoint = antPoint;
            }

            antPoint = point;
        } else if (status.equals("Polyline.NextPointOrLineOrClose")) {
            Point2D point = new Point2D.Double(x, y);
            Point2D lastp = antPoint; //(Point2D)points.get(i-1);

            if (antantPoint == null) {
                antantPoint = new Point2D.Double(lastp.getX() +
                        (point.getX() / 2), lastp.getY() + (point.getY() / 2));
            }

            if (((point.getY() == lastp.getY()) &&
                    (point.getX() < lastp.getX())) ||
                    ((point.getX() == lastp.getX()) &&
                    (point.getY() < lastp.getY()))) {
            } else {
                if (point.getX() == lastp.getX()) {
                    point = new Point2D.Double(point.getX() + 0.00000001,
                            point.getY());
                } else if (point.getY() == lastp.getY()) {
                    point = new Point2D.Double(point.getX(),
                            point.getY() + 0.00000001);
                }

                if (point.getX() == antantPoint.getX()) {
                    point = new Point2D.Double(point.getX() + 0.00000001,
                            point.getY());
                } else if (point.getY() == antantPoint.getY()) {
                    point = new Point2D.Double(point.getX(),
                            point.getY() + 0.00000001);
                }

                if (!(list.size() > 0) ||
                        (((IGeometry) list.get(list.size() - 1)).getGeometryType() == FShape.LINE)) {
                    Point2D[] ps1 = UtilFunctions.getPerpendicular(antantPoint,
                            lastp, lastp);
                    Point2D mediop = new Point2D.Double((point.getX() +
                            lastp.getX()) / 2, (point.getY() + lastp.getY()) / 2);
                    Point2D[] ps2 = UtilFunctions.getPerpendicular(lastp,
                            point, mediop);
                    Point2D interp = UtilFunctions.getIntersection(ps1[0],
                            ps1[1], ps2[0], ps2[1]);
                    antInter = interp;

                    double radio = interp.distance(lastp);

                    if (UtilFunctions.isLowAngle(antantPoint, lastp, interp,
                                point)) {
                        radio = -radio;
                    }

                    Point2D centerp = UtilFunctions.getPoint(interp, mediop,
                            radio);
                    antCenter = centerp;

                    IGeometry ig = ShapeFactory.createArc(lastp, centerp, point);

                    if (ig != null) {
                        list.add(ig);
                    }
                } else {
                    Point2D[] ps1 = UtilFunctions.getPerpendicular(lastp,
                            antInter, lastp);
                    double a1 = UtilFunctions.getAngle(ps1[0], ps1[1]);
                    double a2 = UtilFunctions.getAngle(ps1[1], ps1[0]);
                    double angle = UtilFunctions.getAngle(antCenter, lastp);
                    Point2D ini1 = null;
                    Point2D ini2 = null;

                    if (UtilFunctions.absoluteAngleDistance(angle, a1) > UtilFunctions.absoluteAngleDistance(
                                angle, a2)) {
                        ini1 = ps1[0];
                        ini2 = ps1[1];
                    } else {
                        ini1 = ps1[1];
                        ini2 = ps1[0];
                    }

                    Point2D unit = UtilFunctions.getUnitVector(ini1, ini2);
                    Point2D correct = new Point2D.Double(lastp.getX() +
                            unit.getX(), lastp.getY() + unit.getY());
                    Point2D[] ps = UtilFunctions.getPerpendicular(lastp,
                            correct, lastp);
                    Point2D mediop = new Point2D.Double((point.getX() +
                            lastp.getX()) / 2, (point.getY() + lastp.getY()) / 2);
                    Point2D[] ps2 = UtilFunctions.getPerpendicular(lastp,
                            point, mediop);
                    Point2D interp = UtilFunctions.getIntersection(ps[0],
                            ps[1], ps2[0], ps2[1]);
                    antInter = interp;

                    double radio = interp.distance(lastp);

                    if (UtilFunctions.isLowAngle(correct, lastp, interp, point)) {
                        radio = -radio;
                    }

                    Point2D centerp = UtilFunctions.getPoint(interp, mediop,
                            radio);
                    antCenter = centerp;
                    list.add(ShapeFactory.createArc(lastp, centerp, point));
                }

                antantPoint = antPoint;
                antPoint = point;
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
        PolylineCADToolState actualState = ((PolylineCADToolContext)_fsm).getState();
        String status = actualState.getName();

        if (status.equals("Polyline.NextPointOrArcOrClose")) {
            for (int i = 0; i < list.size(); i++) {
                ((IGeometry) list.get(i)).cloneGeometry().draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
            }

            drawLine((Graphics2D) g, antPoint, new Point2D.Double(x, y));
        } else if ((status.equals("Polyline.NextPointOrLineOrClose"))) {
            for (int i = 0; i < list.size(); i++) {
                ((IGeometry) list.get(i)).cloneGeometry().draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
            }

            Point2D point = new Point2D.Double(x, y);
            Point2D lastp = antPoint;

            if (!(list.size() > 0) ||
                    (((IGeometry) list.get(list.size() - 1)).getGeometryType() == FShape.LINE)) {
                if (antantPoint == null) {
                    drawArc(point, lastp,
                        new Point2D.Double(lastp.getX() + (point.getX() / 2),
                            lastp.getY() + (point.getY() / 2)), g);
                } else {
                    drawArc(point, lastp, antantPoint, g);
                }
            } else {
                if (antInter != null) {
                    Point2D[] ps1 = UtilFunctions.getPerpendicular(lastp,
                            antInter, lastp);
                    double a1 = UtilFunctions.getAngle(ps1[0], ps1[1]);
                    double a2 = UtilFunctions.getAngle(ps1[1], ps1[0]);
                    double angle = UtilFunctions.getAngle(antCenter, lastp);
                    Point2D ini1 = null;
                    Point2D ini2 = null;

                    if (UtilFunctions.absoluteAngleDistance(angle, a1) > UtilFunctions.absoluteAngleDistance(
                                angle, a2)) {
                        ini1 = ps1[0];
                        ini2 = ps1[1];
                    } else {
                        ini1 = ps1[1];
                        ini2 = ps1[0];
                    }

                    Point2D unit = UtilFunctions.getUnitVector(ini1, ini2);
                    Point2D correct = new Point2D.Double(lastp.getX() +
                            unit.getX(), lastp.getY() + unit.getY());
                    drawArc(point, lastp, correct, g);
                }
            }
        }
    }

    /**
     * Dibuja el arco sobre el graphics.
     *
     * @param point Puntero del ratón.
     * @param lastp Último punto de la polilinea.
     * @param antp Punto antepenultimo.
     * @param g Graphics sobre el que se dibuja.
     */
    private void drawArc(Point2D point, Point2D lastp, Point2D antp, Graphics g) {
        if (((point.getY() == lastp.getY()) && (point.getX() < lastp.getX())) ||
                ((point.getX() == lastp.getX()) &&
                (point.getY() < lastp.getY()))) {
        } else {
            if (point.getX() == lastp.getX()) {
                point = new Point2D.Double(point.getX() + 0.00000001,
                        point.getY());
            } else if (point.getY() == lastp.getY()) {
                point = new Point2D.Double(point.getX(),
                        point.getY() + 0.00000001);
            }

            if (point.getX() == antp.getX()) {
                point = new Point2D.Double(point.getX() + 0.00000001,
                        point.getY());
            } else if (point.getY() == antp.getY()) {
                point = new Point2D.Double(point.getX(),
                        point.getY() + 0.00000001);
            }

            Point2D[] ps1 = UtilFunctions.getPerpendicular(lastp, antp, lastp);
            Point2D mediop = new Point2D.Double((point.getX() + lastp.getX()) / 2,
                    (point.getY() + lastp.getY()) / 2);
            Point2D[] ps2 = UtilFunctions.getPerpendicular(lastp, point, mediop);
            Point2D interp = UtilFunctions.getIntersection(ps1[0], ps1[1],
                    ps2[0], ps2[1]);

            double radio = interp.distance(lastp);

            if (UtilFunctions.isLowAngle(antp, lastp, interp, point)) {
                radio = -radio;
            }

            Point2D centerp = UtilFunctions.getPoint(interp, mediop, radio);

            drawLine((Graphics2D) g, lastp, point);

            IGeometry ig = ShapeFactory.createArc(lastp, centerp, point);

            if (ig != null) {
                ig.draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
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
        PolylineCADToolState actualState = (PolylineCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Polyline.NextPointOrArcOrClose")) {
            if (s.equals("A") || s.equals("a")) {
                //Arco
            } else if (s.equals("C") || s.equals("c")) {
            	GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, 2);
                elShape.moveTo(antPoint.getX(), antPoint.getY());
                elShape.lineTo(firstPoint.getX(), firstPoint.getY());
                list.add(ShapeFactory.createPolyline2D(elShape));
            	//closeGeometry();
            }
        } else if (status.equals("Polyline.NextPointOrLineOrClose")) {
            if (s.equals("N") || s.equals("n")) {
                //Línea
            } else if (s.equals("C") || s.equals("c")) {
            	GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, 2);
                elShape.moveTo(antPoint.getX(), antPoint.getY());
                elShape.lineTo(firstPoint.getX(), firstPoint.getY());
                list.add(ShapeFactory.createPolyline2D(elShape));
                //closeGeometry();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }

    public void cancel(){
    	list.clear();
    	antantPoint=antCenter=antInter=antPoint=firstPoint=null;
    }
}
