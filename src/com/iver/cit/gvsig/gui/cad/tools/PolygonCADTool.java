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
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.exception.ValueException;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolygonCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolygonCADToolContext.PolygonCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class PolygonCADTool extends DefaultCADTool {
    protected PolygonCADToolContext _fsm;
    protected Point2D center;
    protected int numLines = 5;
    protected boolean isI = true;

    /**
     * Crea un nuevo PolygonCADTool.
     */
    public PolygonCADTool() {

    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    	_fsm = new PolygonCADToolContext(this);
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
     * Equivale al transition del prototipo pero sin pasarle como parámetro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y,InputEvent event) {
        PolygonCADToolState actualState = (PolygonCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Polygon.NumberOrCenterPoint")) {
            center = new Point2D.Double(x, y);
        } else if (status.equals("Polygon.CenterPoint")) {
            center = new Point2D.Double(x, y);
        } else if (status.equals("Polygon.OptionOrRadiusOrPoint") ||
                status.equals("Polygon.RadiusOrPoint")) {
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

        if (status.equals("Polygon.OptionOrRadiusOrPoint") ||
                status.equals("Polygon.RadiusOrPoint")) {
            Point2D point = new Point2D.Double(x, y);
            drawLine((Graphics2D) g, center, point,DefaultCADTool.geometrySelectSymbol);

            if (isI) {
                getIPolygon(point, point.distance(center)).draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    DefaultCADTool.geometrySelectSymbol);
            } else {
                getCPolygon(point, point.distance(center)).draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    DefaultCADTool.geometrySelectSymbol);
            }

            ShapeFactory.createCircle(center, point.distance(center)).draw((Graphics2D) g,
                getCadToolAdapter().getMapControl().getViewPort(),
                DefaultCADTool.axisReferencesSymbol);
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

        if (status.equals("Polygon.OptionOrRadiusOrPoint")) {
            if (s.equalsIgnoreCase(PluginServices.getText(this,"PolygonCADTool.circumscribed")) || s.equals(PluginServices.getText(this,"circumscribed"))) {
                isI = false;
            } else if (s.equalsIgnoreCase(PluginServices.getText(this,"PolygonCADTool.into_circle")) || s.equals(PluginServices.getText(this,"into_circle"))) {
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

        if (status.equals("Polygon.NumberOrCenterPoint")) {
        	numLines = (int) d;
        } else if (status.equals("Polygon.OptionOrRadiusOrPoint") ||
                status.equals("Polygon.RadiusOrPoint")) {
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
    protected IGeometry getCPolygon(Point2D point, double radio) {
        Point2D p1 = UtilFunctions.getPoint(center, point, radio);

        double initangle = UtilFunctions.getAngle(center, point);
        Point2D antPoint = p1;
        Point2D antInter = null;
        //Point2D firstPoint= null;
        double an = (Math.PI * 2) / numLines;
        GeneralPathX elShape = new GeneralPathX();
        boolean firstTime=true;
        for (int i = numLines-1; i >= 0 ; i--) {
            Point2D p2 = UtilFunctions.getPoint(center, (an * i) + initangle,
                    radio);
            Point2D[] ps1 = UtilFunctions.getPerpendicular(antPoint, center,
                    antPoint);
            Point2D[] ps2 = UtilFunctions.getPerpendicular(p2, center, p2);
            Point2D inter = UtilFunctions.getIntersection(ps1[0], ps1[1],
                    ps2[0], ps2[1]);

            if (antInter != null) {

                if (firstTime){
                	elShape.moveTo(antInter.getX(), antInter.getY());
                	//firstPoint=new Point2D.Double(antInter.getX(), antInter.getY());
                	firstTime=false;
                }
                elShape.lineTo(inter.getX(), inter.getY());

            }

            antInter = inter;
            antPoint = p2;
        }
        //elShape.lineTo(firstPoint.getX(),firstPoint.getY());
        elShape.closePath();
        int type=getCadToolAdapter().getActiveLayerType();
        FShape shape=null;
        if (type==FShape.POLYGON){
        	shape=new FPolygon2D(elShape);
        }else{
        	shape=new FPolyline2D(elShape);
        }
        return ShapeFactory.createGeometry(shape);
    }

    /**
     * Devuelve la geometría con el poligono regular inscrito en la
     * circunferencia.
     *
     * @param point Puntero del ratón.
     * @param radio Radio
     *
     * @return GeometryCollection con las geometrías del polígono.
     * @throws ValueException
     */
    protected IGeometry getIPolygon(Point2D point, double radio){
    	Point2D p1 = UtilFunctions.getPoint(center, point, radio);
        double initangle = UtilFunctions.getAngle(center, point);
        Point2D antPoint = p1;
        //Point2D firstPoint= null;
        double an = (Math.PI * 2) / numLines;
        GeneralPathX elShape = new GeneralPathX();
        boolean firstTime=true;

        for (int i = numLines-1; i > 0; i--) {
            Point2D p2 = UtilFunctions.getPoint(center, (an * i) + initangle,
                    radio);

            if (firstTime){
            	 elShape.moveTo(antPoint.getX(), antPoint.getY());
            	 //firstPoint=new Point2D.Double(antPoint.getX(), antPoint.getY());
            	 firstTime=false;
            }

            elShape.lineTo(p2.getX(), p2.getY());

            antPoint = p2;
        }
        //elShape.lineTo(firstPoint.getX(),firstPoint.getY());
        elShape.closePath();
        int type=getCadToolAdapter().getActiveLayerType();
        FShape shape=null;
        if (type==FShape.POLYGON){
        	shape=new FPolygon2D(elShape);
        }else{
        	shape=new FPolyline2D(elShape);
        }
        return ShapeFactory.createGeometry(shape);
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
   /* private IGeometry getCPolygonOld(Point2D point, double radio) {
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
*/
    /**
     * Devuelve la geometría con el poligono regular inscrito en la
     * circunferencia.
     *
     * @param point Puntero del ratón.
     * @param radio Radio
     *
     * @return GeometryCollection con las geometrías del polígono.
     */
  /*  private IGeometry getIPolygonOld(Point2D point, double radio) {
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
*/
	public String getName() {
		return PluginServices.getText(this,"polygon_");
	}

	/**
	 * Devuelve la cadena que corresponde al estado en el que nos encontramos.
	 *
	 * @return Cadena para mostrar por consola.
	 */
	public String getQuestion() {
		PolygonCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if (status.equals("Polygon.NumberOrCenterPoint")) {
        	return super.getQuestion()+"<"+numLines+">";
        }
       	return super.getQuestion();

	}
	public String toString() {
		return "_polygon";
	}
	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.POINT:
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
