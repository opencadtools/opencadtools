/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id:
 * $Log:
 */
package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import statemap.State;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.SplitGeometryCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.split.SplitStrategy;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * CAD Tool which splits the selected geometries of a vectorial editing layer
 * with a digitized polyline.
 * 
 * 
 * @author Alvaro Zabala
 * 
 */
public class SplitGeometryCADTool extends DefaultCADTool {

    private static Logger logger = Logger.getLogger(SplitGeometryCADTool.class
	    .getName());

    /**
     * String representation of this tool (used for example to active the tool
     * in mapcontrol)
     */
    public static final String SPLIT_GEOMETRY_TOOL_NAME = "_split_geometry";

    /**
     * finite state machine for this CAD tool
     */
    protected SplitGeometryCADToolContext _fsm;

    /**
     * Flag to mark if the digitized line has been finished.
     */
    protected boolean digitizingFinished = false;

    /**
     * Collection of digitized geometries
     */
    protected List<Point2D> clickedPoints;

    /**
     * Default Constructor
     */
    public SplitGeometryCADTool() {

    }

    /**
     * Initialization method.
     */
    @Override
    public void init() {
	digitizingFinished = false;
	_fsm = new SplitGeometryCADToolContext(this);
	setNextTool(SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME);
    }

    public boolean isDigitizingFinished() {
	return digitizingFinished;
    }

    @Override
    public String toString() {
	return SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME;
    }

    public void finishDigitizedLine() {
    }

    @Override
    public ArrayList getSelectedRows() {
	VectorialLayerEdited vle = getVLE();
	ArrayList selection = vle.getSelectedRow();
	if (selection.size() == 0) {
	    VectorialEditableAdapter vea = vle.getVEA();
	    try {
		FBitSet bitset = vea.getSelection();
		for (int j = bitset.nextSetBit(0); j >= 0; j = bitset
			.nextSetBit(j + 1)) {
		    IRowEdited rowEd = vea.getRow(j);
		    selection.add(rowEd);
		}
	    } catch (ExpansionFileReadException e) {
		e.printStackTrace();
	    } catch (ReadDriverException e) {
		e.printStackTrace();
	    }
	}// selection size
	return selection;
    }

    // public ArrayList getSelectedRows(){
    // return (ArrayList) CADUtil.getSelectedFeatures(getVLE());
    // }
    public Coordinate[] getPoint2DAsCoordinates(Point2D[] point2d) {
	Coordinate[] solution = new Coordinate[point2d.length];
	for (int i = 0; i < point2d.length; i++) {
	    solution[i] = new Coordinate(point2d[i].getX(), point2d[i].getY());
	}
	return solution;
    }

    public void splitSelectedGeometryWithDigitizedLine() {
	Point2D[] clickedPts = new Point2D[this.clickedPoints.size()];
	clickedPoints.toArray(clickedPts);
	Coordinate[] digitizedCoords = getPoint2DAsCoordinates(clickedPts);
	LineString splittingLs = new GeometryFactory(new PrecisionModel(10000))
		.createLineString(digitizedCoords);

	ArrayList selectedRows = getSelectedRows();
	IRowEdited editedRow = null;
	VectorialLayerEdited vle = getVLE();
	VectorialEditableAdapter vea = vle.getVEA();
	getCadToolAdapter().getMapControl().getMapContext().beginAtomicEvent();
	vea.startComplexRow();
	List<Integer> indices = new ArrayList<Integer>();
	ArrayList auxSelectedRows = new ArrayList(selectedRows);
	try {
	    vle.clearSelection(false);
	} catch (ReadDriverException e1) {
	    e1.printStackTrace();
	}

	for (int i = 0; i < auxSelectedRows.size(); i++) {
	    editedRow = (IRowEdited) auxSelectedRows.get(i);
	    IFeature feat = (IFeature) editedRow.getLinkedRow().cloneRow();
	    IGeometry ig = feat.getGeometry();
	    Geometry jtsGeo = FConverter.java2d_to_jts((FShape) ig
		    .getInternalShape());
	    if (jtsGeo == null) {
		return;
	    }
	    try {
		Geometry splitGeo = SplitStrategy.splitOp(jtsGeo, splittingLs);
		if (splitGeo instanceof GeometryCollection
			&& ((GeometryCollection) splitGeo).getNumGeometries() > 1) {

		    // Saving originals polygons index
		    indices.add(new Integer(editedRow.getIndex()));
		    // and then, we add new features for each split geometry
		    GeometryCollection gc = (GeometryCollection) splitGeo;
		    ArrayList<Geometry> geoms0 = new ArrayList<Geometry>();
		    ArrayList<Geometry> geoms1 = new ArrayList<Geometry>();
		    if (gc.getNumGeometries() > 2) {
			Geometry[] splitGroups = createSplitGroups(jtsGeo,
				splittingLs);
			if (splitGroups.length == 0) {
			    continue;
			}

			for (int j = 0; j < gc.getNumGeometries(); j++) {
			    Geometry g = gc.getGeometryN(j);
			    if (splitGroups[0].contains(g)) {
				geoms0.add(g);
			    } else {
				geoms1.add(g);
			    }
			}
		    } else if (gc.getNumGeometries() == 2) {
			geoms0.add(gc.getGeometryN(0));
			geoms1.add(gc.getGeometryN(1));
		    } else {
			continue;
		    }
		    GeometryCollection gc0 = createMulti(geoms0,
			    gc.getFactory());

		    IGeometry fmapGeo = FConverter.jts_to_igeometry(gc0);
		    DefaultFeature df = null;
		    int newIdx = 0;
		    // if (j==0){
		    newIdx = editedRow.getIndex();
		    try {
			df = new DefaultFeature(fmapGeo, feat.getAttributes(),
				feat.getID());
			vea.modifyRow(newIdx, df, getName(),
				EditionEvent.GRAPHIC);
		    } catch (ValidateRowException e) {
			NotificationManager.addError(e.getMessage(), e);
		    } catch (ExpansionFileWriteException e) {
			NotificationManager.addError(e.getMessage(), e);
		    } catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(), e);
		    }
		    // }else{
		    GeometryCollection gc1 = createMulti(geoms1,
			    gc.getFactory());
		    fmapGeo = FConverter.jts_to_igeometry(gc1);
		    try {
			String newFID = vea.getNewFID();
			df = new DefaultFeature(fmapGeo, feat.getAttributes(),
				newFID);
			newIdx = vea
				.addRow(df, getName(), EditionEvent.GRAPHIC);
			SpatialCache spatialCache = ((FLyrVect) vle.getLayer())
				.getSpatialCache();
			IGeometry geometry = df.getGeometry();
			Rectangle2D r = geometry.getBounds2D();
			if (geometry.getGeometryType() == FShape.POINT) {
			    r = new Rectangle2D.Double(r.getX(), r.getY(), 1, 1);
			}
			spatialCache.insert(r, geometry);
		    } catch (ValidateRowException e) {
			NotificationManager.addError(e);
		    } catch (ReadDriverException e) {
			NotificationManager.addError(e);
		    }
		    // }
		    DefaultRowEdited newRowEdited = new DefaultRowEdited(df,
			    IRowEdited.STATUS_ADDED, newIdx);
		    vle.addSelectionCache(newRowEdited);
		    // }//for j
		}// if splitGeo
	    } catch (Exception ex) {
		PluginServices.getLogger().error(
			"Error splitting geom " + editedRow.getIndex(), ex);
	    }
	}
	vea.endComplexRow(getName());

	getCadToolAdapter().getMapControl().getMapContext().endAtomicEvent();
    }

    private GeometryCollection createMulti(ArrayList<Geometry> geoms,
	    GeometryFactory factory) {
	if (geoms.size() == 0) {
	    return null;
	}
	if (geoms.get(0) instanceof Polygon) {
	    return new MultiPolygon((Polygon[]) geoms.toArray(new Polygon[0]),
		    factory);
	} else if (geoms.get(0) instanceof LineString) {
	    return new MultiLineString(
		    (LineString[]) geoms.toArray(new LineString[0]), factory);
	} else if (geoms.get(0) instanceof Point) {
	    return new MultiPoint((Point[]) geoms.toArray(new Point[0]),
		    factory);
	}
	return null;
    }

    private Geometry[] createSplitGroups(Geometry splitGeo,
	    LineString splittingLs) {
	try {
	    Geometry[] geomsJTS = new Geometry[2];
	    Geometry r = splitGeo.getEnvelope();
	    Geometry splitG = SplitStrategy.splitOp(r, splittingLs);
	    if (splitG instanceof GeometryCollection
		    && ((GeometryCollection) splitG).getNumGeometries() > 1) {
		GeometryCollection gc = (GeometryCollection) splitG;
		for (int j = 0; j < gc.getNumGeometries(); j++) {
		    geomsJTS[j] = gc.getGeometryN(j);
		}
	    }
	    return geomsJTS;
	} catch (Exception e) {
	    NotificationManager
		    .showMessageError(PluginServices.getText(this,
			    "line_not_cross_rectangle"), e);
	}
	return null;
    }

    @Override
    public void end() {
	getCadToolAdapter().refreshEditedLayer();
	init();
    }

    public void addOption(String s) {
	State actualState = _fsm.getPreviousState();
	String status = actualState.getName();
	if (s.equals(PluginServices.getText(this, "cancel"))) {
	    init();
	    return;
	}
	if (status.equals("TopologicalEdition.FirstPoint")) {
	    return;
	}
	init();

    }

    public void addPoint(double x, double y, InputEvent event) {

	State actualState = _fsm.getPreviousState();
	String status = actualState.getName();
	if (status.equals("SplitGeometry.FirstPoint")) {
	    clickedPoints = new ArrayList<Point2D>();
	    clickedPoints.add(new Point2D.Double(x, y));
	} else if (status.equals("SplitGeometry.DigitizingLine")) {
	    clickedPoints.add(new Point2D.Double(x, y));
	    if (event != null && ((MouseEvent) event).getClickCount() == 2) {
		digitizingFinished = true;
		finishDigitizedLine();
		splitSelectedGeometryWithDigitizedLine();
		end();
	    }
	}
    }

    public void addValue(double d) {
    }

    /**
     * Draws a polyline with the clicked digitized points in the specified
     * graphics.
     * 
     * @param g2
     *            graphics on to draw the polyline
     * @param x
     *            last x mouse pointer position
     * @param y
     *            last y mouse pointer position
     */
    protected void drawPolyLine(Graphics2D g, double x, double y) {
	GeneralPathX gpx = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
		clickedPoints.size());
	Point2D firstPoint = clickedPoints.get(0);
	gpx.moveTo(firstPoint.getX(), firstPoint.getY());
	for (int i = 1; i < clickedPoints.size(); i++) {
	    Point2D clickedPoint = clickedPoints.get(i);
	    gpx.lineTo(clickedPoint.getX(), clickedPoint.getY());

	}
	gpx.lineTo(x, y);
	ShapeFactory.createPolyline2D(gpx).draw((Graphics2D) g,
		getCadToolAdapter().getMapControl().getViewPort(),
		DefaultCADTool.geometrySelectSymbol);
    }

    public void drawOperation(Graphics g, double x, double y) {
	try {
	    State actualState = _fsm.getState();
	    String status = actualState.getName();
	    drawRectangleOfSplit((Graphics2D) g);
	    // draw splitting line
	    if ((status.equals("SplitGeometry.DigitizingLine"))) {
		drawPolyLine((Graphics2D) g, x, y);
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	}
	// draw selection
	try {
	    Image imgSel = getVLE().getSelectionImage();
	    if (imgSel != null) {
		g.drawImage(imgSel, 0, 0, null);
	    }
	} catch (Exception e) {
	    PluginServices.getLogger().error("Error drawing Editing Selection",
		    e);
	}
    }

    private void drawRectangleOfSplit(Graphics2D g) {
	ArrayList selectedRows = getSelectedRows();
	for (int i = 0; i < selectedRows.size(); i++) {
	    IRowEdited editedRow = (IRowEdited) selectedRows.get(i);
	    IFeature feat = (IFeature) editedRow.getLinkedRow();
	    IGeometry ig = feat.getGeometry();
	    Geometry jtsG = ig.toJTSGeometry();
	    if (jtsG != null && jtsG instanceof GeometryCollection
		    && jtsG.getNumGeometries() > 1) {
		Rectangle2D r = ig.getBounds2D();
		GeneralPathX gpx = new GeneralPathX();
		gpx.moveTo(r.getX(), r.getY());
		gpx.lineTo(r.getMaxX(), r.getY());
		gpx.lineTo(r.getMaxX(), r.getMaxY());
		gpx.lineTo(r.getX(), r.getMaxY());
		gpx.closePath();
		ShapeFactory.createPolygon2D(gpx).draw((Graphics2D) g,
			getCadToolAdapter().getMapControl().getViewPort(),
			DefaultCADTool.axisReferencesSymbol);
	    }
	}

    }

    public String getName() {
	return PluginServices.getText(this, "split_geometry_shell");
    }

    public void transition(double x, double y, InputEvent event) {
	try {
	    _fsm.addPoint(x, y, event);
	} catch (Exception e) {
	    init();
	}

    }

    public void transition(double d) {
	_fsm.addValue(d);
    }

    public void transition(String s) throws CommandException {
	if (!super.changeCommand(s)) {
	    _fsm.addOption(s);
	}
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
