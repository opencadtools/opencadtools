package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class RemovePartCADTool extends DefaultCADTool {

    private static final Logger logger = Logger
	    .getLogger(RemovePartCADTool.class);

    @Override
    public void init() {
	setNextTool("_selection");
	setQuestion(PluginServices.getText(this, "remove_part_question"));
    }

    @Override
    public void transition(InputEvent event) {
    }

    @Override
    public void transition(double x, double y, InputEvent event) {

	VectorialLayerEdited vle = getVLE();

	vle.selectWithPoint(x, y, false);

	if (vle.getSelectedRow().size() > 1) {
	    throw new IllegalStateException(
		    "No more than one feat should be selected");
	}

	IRowEdited rowEdited = (IRowEdited) vle.getSelectedRow().get(0);
	final IFeature feat = (IFeature) rowEdited.getLinkedRow();
	IGeometry geom = feat.getGeometry();

	Geometry jtsGeom = geom.toJTSGeometry();

	int numParts = jtsGeom.getNumGeometries();

	if (numParts < 2) {
	    throwNoPointsException(PluginServices.getText(this,
		    "remove_last_part_warning"));
	    return;
	}

	IGeometry newGeom = removePart(vle.getRectUsedForSnapping(), jtsGeom);
	feat.setGeometry(newGeom);

	try {
	    clearSelection();
	} catch (ReadDriverException e) {
	    logger.error(e.getStackTrace(), e);
	}
	modifyFeature(rowEdited.getIndex(), feat);
    }

    private IGeometry removePart(Rectangle2D rect, Geometry jtsGeom) {
	final GeometryFactory geomFactory = new GeometryFactory();

	Polygon snapRect = jtsPolygonFromRectangle2D(rect, geomFactory);

	final int nParts = jtsGeom.getNumGeometries();
	final int nPartsExpected = nParts - 1;
	ArrayList<Geometry> geomList = new ArrayList<Geometry>(nPartsExpected);

	for (int i = 0; i < nParts; i++) {
	    Geometry geometryN = jtsGeom.getGeometryN(i);
	    if (!geometryN.intersects(snapRect)) {
		geomList.add(geometryN);
	    }
	}

	if (geomList.size() != nPartsExpected) {
	    throw new IllegalArgumentException(String.format(
		    "Rectangle: %s intersects with all the geometry parts",
		    rect));
	}

	Geometry jtsMultiGeom = geomFactory.buildGeometry(geomList);

	return FConverter.jts_to_igeometry(jtsMultiGeom);
    }

    private Polygon jtsPolygonFromRectangle2D(Rectangle2D rect,
	    GeometryFactory geomFactory) {
	final Coordinate[] coordinates = new Coordinate[5];
	coordinates[0] = new Coordinate(rect.getMinX(), rect.getMaxY());
	coordinates[1] = new Coordinate(rect.getMaxX(), rect.getMaxY());
	coordinates[2] = new Coordinate(rect.getMaxX(), rect.getMinY());
	coordinates[3] = new Coordinate(rect.getMinX(), rect.getMinY());
	coordinates[4] = new Coordinate(rect.getMinX(), rect.getMaxY());
	final LinearRing linearRing = geomFactory.createLinearRing(coordinates);
	return geomFactory.createPolygon(linearRing, null);
    }

    @Override
    public void transition(double d) {
    }

    @Override
    public void transition(String s) throws CommandException {
    }

    @Override
    public void addPoint(double x, double y, InputEvent event) {
    }

    @Override
    public void addValue(double d) {
    }

    @Override
    public void addOption(String s) {
    }

    @Override
    public void drawOperation(Graphics g, double x, double y) {
    }

    @Override
    public void drawOperation(Graphics g, ArrayList pointList) {
    }

    @Override
    public String getName() {
	return PluginServices.getText(this, "remove_part");
    }

    @Override
    public String toString() {
	return "_remove_part";
    }
}
