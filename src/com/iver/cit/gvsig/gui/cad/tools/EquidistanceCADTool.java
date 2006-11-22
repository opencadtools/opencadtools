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
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.EquidistanceCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.EquidistanceCADToolContext.EquidistanceCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;


/**
 * Herramienta para crear una geometría equidistante a otra.
 *
 * @author Vicente Caballero Navarro
 */
public class EquidistanceCADTool extends DefaultCADTool {
    private EquidistanceCADToolContext _fsm;
    private Point2D firstPoint=new Point2D.Double(800000,4500000);
    private Point2D secondPoint=new Point2D.Double(810000,4500000);
	private double distance=10;
	private double distancePos=java.lang.Double.MAX_VALUE;
	private LineString distanceLine;
	/**
     * Crea un nuevo EquidistanceCADTool.
     */
    public EquidistanceCADTool() {
    }

    /**
     * Método de inicio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new EquidistanceCADToolContext(this);

    }

    private Coordinate[] getParallel(Point2D[] points,double distance) {
    	Point2D[] pper=new Point2D[2];
    	double angle=Math.toRadians(90)+UtilFunctions.getAngle(points[0],points[1]);
    	pper[0]=UtilFunctions.getPoint(points[0],angle,distance);
    	pper[1]=UtilFunctions.getPoint(points[1],angle,distance);
    	Coordinate[] result=new Coordinate[2];
    	result[0]=new Coordinate(pper[0].getX(),pper[0].getY());
    	result[1]=new Coordinate(pper[1].getX(),pper[1].getY());
    	return result;
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
     * DOCUMENT ME!
     */
    public void selection() {
       ArrayList selectedRows=getSelectedRows();
        if (selectedRows.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "_Equidistance");
        }
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como parámetro el
     * editableFeatureSource que ya estará creado.
     *
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y,InputEvent event) {
        EquidistanceCADToolState actualState = (EquidistanceCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        if (status.equals("Equidistance.Distance")) {
        	firstPoint = new Point2D.Double(x, y);
    	} else if (status.equals("Equidistance.SecondPointDistance")) {
    		secondPoint = new Point2D.Double(x,y);
    		distance=secondPoint.distance(firstPoint);
    	} else if (status.equals("Equidistance.Position")) {
    		ArrayList selectedRow = getSelectedRows();
            ArrayList selectedRowAux=new ArrayList();
            VectorialLayerEdited vle = getVLE();
            VectorialEditableAdapter vea = vle.getVEA();
            PluginServices.getMDIManager().setWaitCursor();
    		try {
    			vea.startComplexRow();
    			for (int i = 0; i < selectedRow.size(); i++) {
    				DefaultRowEdited row = (DefaultRowEdited) selectedRow
    						.get(i);
    				DefaultFeature fea = (DefaultFeature) row.getLinkedRow()
    						.cloneRow();

    				IGeometry geometry=compute(fea,new Point2D.Double(x,y));
    				addGeometry(geometry);
    			}

    			vea.endComplexRow(getName());
    			vle.setSelectionCache(VectorialLayerEdited.SAVEPREVIOUS, selectedRowAux);
    			//clearSelection();
    			//selectedRow.addAll(selectedRowAux);
    		} catch (DriverIOException e) {
    			e.printStackTrace();
    		} catch (IOException e1) {
    			e1.printStackTrace();
    		}
    		PluginServices.getMDIManager().restoreCursor();
    	}
    }

    private IGeometry compute(DefaultFeature fea, Point2D position){
		IGeometry geometry = fea.getGeometry();
		int typeGeometry = geometry.getGeometryType();
		Geometry g = geometry.toJTSGeometry();
		GeometryFactory factory=g.getFactory();
	    Coordinate[] c2 = new Coordinate[2];
		Geometry g2 = null;
		Coordinate coordinatePosition=new Coordinate(position.getX(),position.getY());
		Point pPos=factory.createPoint(coordinatePosition);
		switch (typeGeometry) {
		case FShape.LINE:

			LineString lR =factory.createLineString(g.getCoordinates());
			g = TopologyPreservingSimplifier.simplify(lR, 10d);
			Geometry gLine = g.getGeometryN(0);
			Coordinate[] coordinatesLine = gLine.getCoordinates();
			int numPointsLine = gLine.getNumPoints();
			if (numPointsLine < 1)
				return null;
			LineString[] lineStrings = new LineString[numPointsLine - 1];

			for (int j = 1; j < numPointsLine; j = j + 1) {
				c2[0] = coordinatesLine[j - 1];
				c2[1] = coordinatesLine[j];
				LineString lS=factory.createLineString(c2);
				if (lS.distance(pPos)<distancePos) {
					distancePos=lS.distance(pPos);
					distanceLine=(LineString)factory.createGeometry(lS);
				}
			}
			setDistanceLine(coordinatePosition);

			for (int j = 1; j < numPointsLine; j = j + 1) {
				c2[0] = coordinatesLine[j - 1];
				c2[1] = coordinatesLine[j];
				Point2D[] points = new Point2D[2];
				points[0] = new Point2D.Double(c2[0].x, c2[0].y);
				points[1] = new Point2D.Double(c2[1].x, c2[1].y);
				lineStrings[j - 1] = factory.createLineString(
						getParallel(points, distance));
			}

			for (int i = 0; i < lineStrings.length - 1; i++) {
				Coordinate coord = lineStrings[i].getCoordinateN(0);
				Point2D p1 = new Point2D.Double(coord.x, coord.y);
				coord = lineStrings[i].getCoordinateN(1);
				Point2D p2 = new Point2D.Double(coord.x, coord.y);
				coord = lineStrings[i + 1].getCoordinateN(0);
				Point2D p3 = new Point2D.Double(coord.x, coord.y);
				coord = lineStrings[i + 1].getCoordinateN(1);
				Point2D p4 = new Point2D.Double(coord.x, coord.y);
				Point2D intersection = UtilFunctions.getIntersection(p1, p2,
						p3, p4);
				Coordinate[] coords1 = new Coordinate[2];
				coords1[0] = lineStrings[i].getCoordinateN(0);
				coords1[1] = new Coordinate(intersection.getX(), intersection
						.getY());
				lineStrings[i] = factory.createLineString(coords1);
				Coordinate[] coords2 = new Coordinate[2];
				coords2[0] = coords1[1];
				coords2[1] = lineStrings[i + 1].getCoordinateN(1);
				lineStrings[i + 1] = factory.createLineString(
						coords2);
			}
			g2 = factory.createMultiLineString(lineStrings);
			return FConverter.jts_to_igeometry(g2);
		case FShape.POLYGON:
		case FShape.CIRCLE:
		case FShape.ELLIPSE:
			g = TopologyPreservingSimplifier.simplify(g, 10d);
			Geometry gPolygon = g.getGeometryN(0);
			setDistancePolygon(gPolygon,pPos);
			Coordinate[] coordinates = gPolygon.getCoordinates();
			int numPointsPolygon = gPolygon.getNumPoints();
			if (numPointsPolygon < 2)
				return null;
			LineString[] polygonStrings = new LineString[numPointsPolygon];
			for (int j = 1; j < numPointsPolygon; j = j + 1) {
				c2[0] = coordinates[j - 1];
				c2[1] = coordinates[j];
				Point2D[] points = new Point2D[2];
				points[0] = new Point2D.Double(c2[0].x, c2[0].y);
				points[1] = new Point2D.Double(c2[1].x, c2[1].y);
				polygonStrings[j - 1] = factory.createLineString(
						getParallel(points, distance));
			}
			for (int i = 0; i < polygonStrings.length - 2; i++) {
				Coordinate coord = polygonStrings[i].getCoordinateN(0);
				Point2D p1 = new Point2D.Double(coord.x, coord.y);
				coord = polygonStrings[i].getCoordinateN(1);
				Point2D p2 = new Point2D.Double(coord.x, coord.y);
				coord = polygonStrings[i + 1].getCoordinateN(0);
				Point2D p3 = new Point2D.Double(coord.x, coord.y);
				coord = polygonStrings[i + 1].getCoordinateN(1);
				Point2D p4 = new Point2D.Double(coord.x, coord.y);
				Point2D intersection = UtilFunctions.getIntersection(p1, p2,
						p3, p4);
				Coordinate[] coords1 = new Coordinate[2];
				coords1[0] = polygonStrings[i].getCoordinateN(0);
				coords1[1] = new Coordinate(intersection.getX(), intersection
						.getY());
				polygonStrings[i] = factory.createLineString(
						coords1);
				Coordinate[] coords2 = new Coordinate[2];
				coords2[0] = coords1[1];
				coords2[1] = polygonStrings[i + 1].getCoordinateN(1);
				polygonStrings[i + 1] = factory.createLineString(
						coords2);
			}

			// /////////////
			Coordinate coord = polygonStrings[0].getCoordinateN(0);
			Point2D p1 = new Point2D.Double(coord.x, coord.y);
			coord = polygonStrings[0].getCoordinateN(1);
			Point2D p2 = new Point2D.Double(coord.x, coord.y);
			coord = polygonStrings[polygonStrings.length - 2].getCoordinateN(0);
			Point2D p3 = new Point2D.Double(coord.x, coord.y);
			coord = polygonStrings[polygonStrings.length - 2].getCoordinateN(1);
			Point2D p4 = new Point2D.Double(coord.x, coord.y);

			Point2D intersection = UtilFunctions
					.getIntersection(p1, p2, p3, p4);
			Coordinate[] coords1 = new Coordinate[2];
			coords1[0] = polygonStrings[polygonStrings.length - 2]
					.getCoordinateN(1);
			coords1[1] = new Coordinate(intersection.getX(), intersection
					.getY());
			polygonStrings[polygonStrings.length - 1] = factory
					.createLineString(coords1);
//			LineString[] pol=new LineString[polygonStrings.length-1];
//			for (int i=0;i<pol.length;i++) {
//				pol[i]=polygonStrings[i];
//			}
			Coordinate[] coords2 = new Coordinate[2];
			coords2[0] = coords1[1];
			coords2[1] = polygonStrings[0].getCoordinateN(1);
			polygonStrings[0] = factory.createLineString(coords2);
			// ////////////////////////////////
			Geometry geometryCollection = factory
					.createGeometryCollection(polygonStrings);
			LinearRing linearRing = factory.createLinearRing(
					geometryCollection.getCoordinates());
			LinearRing[] holes = new LinearRing[1];
			holes[0] = factory.createLinearRing(new Coordinate[0]);
			g2 = factory.createPolygon(linearRing, holes);
			return FConverter.jts_to_igeometry(g2);

		case FShape.MULTIPOINT:
		case FShape.POINT:
			break;
		default:
			break;
		}
		return null;
	}

    private void setDistanceLine(Coordinate position) {
    	Coordinate pStart=distanceLine.getCoordinateN(0);
    	Coordinate pEnd=distanceLine.getCoordinateN(distanceLine.getNumPoints()-1);
    	int pos=CGAlgorithms.computeOrientation(pStart,pEnd,position);
   		if (pos>0)
    			distance=Math.abs(distance);
    		else
    			distance=-Math.abs(distance);
   		distancePos=java.lang.Double.MAX_VALUE;
   		distanceLine=null;
	}

	private void setDistancePolygon(Geometry polygon, Geometry position) {
		boolean intersects=polygon.intersects(position);
		if (intersects)
			distance=-Math.abs(distance);
		else
			distance=Math.abs(distance);
	}

	/**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param x parámetro x del punto que se pase para dibujar.
     * @param y parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {

    }
    /**
	 * Add a diferent option.
	 *
	 * @param s
	 *            Diferent option.
	 */
    public void addOption(String s) {
//    	EquidistanceCADToolState actualState = (EquidistanceCADToolState) _fsm
//				.getPreviousState();
//		String status = actualState.getName();
//		if (status.equals("Equidistance.Distance")) {
//			distance=java.lang.Double.parseDouble(s);
//		}
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
    public void addValue(double d) {
    	EquidistanceCADToolState actualState = (EquidistanceCADToolState) _fsm
		.getPreviousState();
    	String status = actualState.getName();
    	if (status.equals("Equidistance.Distance")) {
    		distance=d;
    	}
    }

	public String getName() {
		return PluginServices.getText(this,"Equidistance_");
	}

	public String toString() {
		return "_Equidistance";
	}

	public boolean isApplicable(int shapeType) {
		if (shapeType==FShape.POINT ||
				shapeType==FShape.MULTIPOINT) {
			return false;
		}
		return true;
	}
}
