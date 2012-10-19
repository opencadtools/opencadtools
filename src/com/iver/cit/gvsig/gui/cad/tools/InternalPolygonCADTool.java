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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
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
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.InternalPolygonCADToolContext;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class InternalPolygonCADTool extends DefaultCADTool {
    private InternalPolygonCADToolContext _fsm;
    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private IGeometry geometry = null;

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public InternalPolygonCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    @Override
    public void init() {
	_fsm = new InternalPolygonCADToolContext(this);
	points.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double, double)
     */
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
    public void transition(String s) throws CommandException {
	if (!super.changeCommand(s)) {
	    _fsm.addOption(s);
	}
    }

    /**
     * DOCUMENT ME!
     */
    public void selection() {
	ArrayList selectedRow = getSelectedRows();
	if (selectedRow.size() == 0
		&& !CADExtension
			.getCADTool()
			.getClass()
			.getName()
			.equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
	    CADExtension.setCADTool("_selection", false);
	    ((SelectionCADTool) CADExtension.getCADTool())
		    .setNextTool("_internalpolygon");
	}
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como parámetro el
     * editableFeatureSource que ya estará creado.
     * 
     * @param x
     *            parámetro x del punto que se pase en esta transición.
     * @param y
     *            parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y, InputEvent event) {
	VectorialLayerEdited vle = getVLE();
	ArrayList selectedRows = vle.getSelectedRow();
	if (selectedRows.size() == 1) {
	    // if (geometry==null){
	    IRowEdited row = (DefaultRowEdited) selectedRows.get(0);
	    IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
	    geometry = feat.getGeometry();
	    // }
	    if (geometry.contains(x, y)) {
		points.add(new Point2D.Double(x, y));
	    } else {
		JOptionPane
			.showMessageDialog(
				((Component) PluginServices.getMainFrame()),
				PluginServices
					.getText(this,
						"debe_insertar_el_punto_dentro_de_los_limites_de_la_geometria"));
	    }
	}
    }

    /**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param x
     *            parámetro x del punto que se pase para dibujar.
     * @param y
     *            parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {
	Point2D[] ps = (Point2D[]) points.toArray(new Point2D[0]);
	GeneralPathX gpx = new GeneralPathX();
	// GeneralPathX gpx1=new GeneralPathX();

	if (ps.length > 0) {
	    for (int i = 0; i < ps.length; i++) {
		if (i == 0) {
		    gpx.moveTo(ps[i].getX(), ps[i].getY());
		    // gpx1.moveTo(ps[i].getX(),ps[i].getY());
		} else {
		    gpx.lineTo(ps[i].getX(), ps[i].getY());
		    // gpx1.lineTo(ps[i].getX(),ps[i].getY());
		}

	    }
	    DefaultRowEdited[] rows = (DefaultRowEdited[]) getSelectedRows()
		    .toArray(new DefaultRowEdited[0]);
	    for (int i = 0; i < rows.length; i++) {
		((IFeature) rows[i].getLinkedRow()).getGeometry().drawInts(
			(Graphics2D) g,
			CADExtension.getEditionManager().getMapControl()
				.getViewPort(), DefaultCADTool.selectionSymbol);
	    }
	    gpx.lineTo(x, y);
	    gpx.closePath();
	    // gpx1.closePath();
	    if (ps.length == 1) {
		IGeometry geom = ShapeFactory.createPolyline2D(gpx);
		geom.drawInts((Graphics2D) g, CADExtension.getEditionManager()
			.getMapControl().getViewPort(),
			DefaultCADTool.geometrySelectSymbol);
	    }
	    IGeometry geom = ShapeFactory.createPolygon2D(gpx);
	    // IGeometry geom1=ShapeFactory.createPolygon2D(gpx1);
	    // geom1.drawInts((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.selectionSymbol);
	    geom.drawInts((Graphics2D) g, CADExtension.getEditionManager()
		    .getMapControl().getViewPort(),
		    DefaultCADTool.geometrySelectSymbol);
	}

    }

    /**
     * Add a diferent option.
     * 
     * @param s
     *            Diferent option.
     */
    public void addOption(String s) {
	VectorialLayerEdited vle = getVLE();
	ArrayList selectedRows = vle.getSelectedRow();
	VectorialEditableAdapter vea = vle.getVEA();
	IRowEdited row = null;
	if (s.equals(PluginServices.getText(this, "end"))
		|| s.equalsIgnoreCase(PluginServices.getText(this,
			"InternalPolygonCADTool.end"))) {
	    if (points.size() > 0) {
		row = (DefaultRowEdited) selectedRows.get(0);
		IFeature feat = (IFeature) row.getLinkedRow().cloneRow();

		geometry = feat.getGeometry();
		if (geometry instanceof FGeometryCollection) {
		    FGeometryCollection gc = (FGeometryCollection) geometry;
		    geometry = createNewPolygonGC(gc,
			    (Point2D[]) points.toArray(new Point2D[0]));
		} else {
		    geometry = createNewPolygon(geometry,
			    (Point2D[]) points.toArray(new Point2D[0]));
		}
		DefaultFeature df = new DefaultFeature(geometry,
			feat.getAttributes(), feat.getID());
		DefaultRowEdited dre = new DefaultRowEdited(df,
			DefaultRowEdited.STATUS_MODIFIED, row.getIndex());
		try {
		    vea.modifyRow(dre.getIndex(), dre.getLinkedRow(),
			    getName(), EditionEvent.GRAPHIC);
		} catch (ValidateRowException e) {
		    NotificationManager.addError(e.getMessage(), e);
		} catch (ExpansionFileWriteException e) {
		    NotificationManager.addError(e.getMessage(), e);
		} catch (ReadDriverException e) {
		    NotificationManager.addError(e.getMessage(), e);
		}
		ArrayList rows = new ArrayList();
		rows.add(dre);
		vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS,
			rows);
	    }
	    points.clear();

	} else if (s.equals(PluginServices.getText(this, "cancel"))) {
	    points.clear();
	}
	PluginServices.getMainFrame().enableControls();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }

    private IGeometry createNewPolygon(IGeometry gp, Point2D[] ps) {
	GeneralPathX newGp = new GeneralPathX();
	double[] theData = new double[6];

	PathIterator theIterator;
	int theType;
	int numParts = 0;

	theIterator = gp.getPathIterator(null, FConverter.FLATNESS);
	while (!theIterator.isDone()) {
	    theType = theIterator.currentSegment(theData);
	    switch (theType) {

	    case PathIterator.SEG_MOVETO:
		numParts++;
		newGp.moveTo(theData[0], theData[1]);
		break;

	    case PathIterator.SEG_LINETO:
		newGp.lineTo(theData[0], theData[1]);
		break;

	    case PathIterator.SEG_QUADTO:
		newGp.quadTo(theData[0], theData[1], theData[2], theData[3]);
		break;

	    case PathIterator.SEG_CUBICTO:
		newGp.curveTo(theData[0], theData[1], theData[2], theData[3],
			theData[4], theData[5]);
		break;

	    case PathIterator.SEG_CLOSE:
		newGp.closePath();
		break;
	    } // end switch

	    theIterator.next();
	} // end while loop
	GeneralPathX gpxInternal = new GeneralPathX();
	gpxInternal.moveTo(ps[ps.length - 1].getX(), ps[ps.length - 1].getY());
	for (int i = ps.length - 1; i >= 0; i--) {
	    gpxInternal.lineTo(ps[i].getX(), ps[i].getY());
	}
	gpxInternal.lineTo(ps[ps.length - 1].getX(), ps[ps.length - 1].getY());
	if (!gpxInternal.isCCW()) {
	    gpxInternal.flip();
	}
	newGp.append(gpxInternal, false);

	return ShapeFactory.createPolygon2D(newGp);
    }

    private IGeometry createNewPolygonGC(FGeometryCollection gp, Point2D[] ps) {
	ArrayList geoms = new ArrayList();
	IGeometry[] geometries = gp.getGeometries();
	for (int i = 0; i < geometries.length; i++) {
	    geoms.add(geometries[i]);
	}
	GeneralPathX gpx = new GeneralPathX();
	gpx.moveTo(ps[ps.length - 1].getX(), ps[ps.length - 1].getY());
	for (int i = ps.length - 2; i >= 0; i--) {
	    gpx.lineTo(ps[i].getX(), ps[i].getY());
	    geoms.add(ShapeFactory.createPolyline2D(gpx));
	    gpx = new GeneralPathX();
	    gpx.moveTo(ps[i].getX(), ps[i].getY());
	}
	gpx.lineTo(ps[ps.length - 1].getX(), ps[ps.length - 1].getY());
	geoms.add(ShapeFactory.createPolyline2D(gpx));
	FGeometryCollection gc = new FGeometryCollection(
		(IGeometry[]) geoms.toArray(new IGeometry[0]));
	// gc.setGeometryType(FShape.POLYGON);
	return gc;
    }

    public String getName() {
	return PluginServices.getText(this, "internal_polygon_");
    }

    @Override
    public String toString() {
	return "_internalpolygon";
    }

    @Override
    public boolean isApplicable(int shapeType) {
	switch (shapeType) {
	case FShape.POINT:
	case FShape.LINE:
	case FShape.MULTIPOINT:
	    return false;
	}
	return true;
    }

    @Override
    public void endTransition(double x, double y, MouseEvent event) {
	_fsm.endPoint(x, y, event);
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
