/*
 * Copyright 2008 Deputaci�n Provincial de A Coru�a
 * Copyright 2009 Deputaci�n Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coru�a
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coru�a (CartoLab).
 * http://www.cartolab.es
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
 */

package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.AreaCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.AreaCADToolContext.AreaCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * Herramienta que permite digitalizar un pol�gono
 * 
 * @author Isabel P�rez-Urria Lage [LBD]
 * @author Javier Est�vez [Cartolab]
 */
public class AreaCADTool extends InsertionCADTool {

    public static final String AREA_ACTION_COMMAND = "_area";

    private AreaCADToolContext _fsm;

    /**
     * Contiene los puntos del pol�gono digitalizado actualmente (puede ser el
     * anillo exterior o alguno de los huecos)
     * */
    private ArrayList points = new ArrayList();

    private int numShapes;

    private boolean isHole;

    /**
     * Index of the last feature introduced in VEA.
     */
    private Integer virtualIndex;

    private IGeometry insertedGeometry;
    private IRowEdited rowEdited;

    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    @Override
    public void init() {
	// clear();
	if (_fsm == null) {
	    _fsm = new AreaCADToolContext(this);
	}
    }

    @Override
    public void clear() {
	super.init();
	this.setMultiTransition(true);
	points.clear();
	numShapes = 0;
	isHole = false;
	// con esto limpio el ultimo punto pulsado para reinicializar el
	// seguimiento de
	// los snappers
	getCadToolAdapter().setPreviousPoint((double[]) null);
	_fsm = new AreaCADToolContext(this);
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
	    if (s.equals(PluginServices.getText(this, "removePoint"))) {
		_fsm.removePoint(null, points.size());
	    } else {
		_fsm.addOption(s);
	    }
	}
    }

    public void transition(InputEvent event) {
	_fsm.removePoint(event, points.size());
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como par�metro el
     * editableFeatureSource que ya estar� creado.
     * 
     * @param x
     *            par�metro x del punto que se pase en esta transici�n.
     * @param y
     *            par�metro y del punto que se pase en esta transici�n.
     */
    public void addPoint(double x, double y, InputEvent event) {
	AreaCADToolState actualState = (AreaCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();
	// NACHOV
	// if ( status.equals("Area.FirstPoint")){
	// //esto es para que no haya problemas con las entidades compuestas
	// (como carreteras)
	// CADExtension.ifCompoundStopAnotherLayersEdition(getActiveLayer());
	// }
	points.add(new Point2D.Double(x, y));
    }

    public void removePoint(InputEvent event) {
	AreaCADToolState actualState = (AreaCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();

	if ((status.equals("Area.FirstPoint"))
		|| (status.equals("Area.SecondPoint"))) {
	    if (numShapes == 0) {
		cancel();
		// Ya tenemos alg�n pol�gono
	    } else {
		points.clear();
	    }
	    // prueba para actualizar el ultimo punto pulsado
	    getCadToolAdapter().setPreviousPoint((double[]) null);

	} else if ((status.equals("Area.ThirdPoint"))
		|| (status.equals("Area.NextPoint"))) {
	    // prueba para actualizar el ultimo punto pulsado
	    getCadToolAdapter().setPreviousPoint(
		    (Point2D) points.get(points.size() - 2));
	    points.remove(points.size() - 1);
	}
    }

    // NACHOV
    // /**
    // * Acci�n que abre el formulario de edici�n de las propiedades para
    // * el punto introducido
    // */
    // public void openForm(){
    // keys = openInsertEntityForm();
    // if (keys.size() == 0){
    // setFormState(InsertionCADTool.FORM_CANCELLED);
    // }else{
    // setFormState(InsertionCADTool.FORM_ACCEPTED);
    // }
    // }

    /**
     * Acci�n que almacena la geometria editada en el VectorialEditableAdapter
     * */
    public void saveTempGeometry() {
	VectorialLayerEdited vle = getVLE();
	VectorialEditableAdapter vea = vle.getVEA();
	IRowEdited row = null;

	try {
	    if (points.size() > 0) {
		if (numShapes != 0) {
		    if (virtualIndex != null) {
			row = (DefaultRowEdited) vea.getRow(virtualIndex
				.intValue());
			rowEdited = row;
			IFeature feat = (IFeature) row.getLinkedRow()
				.cloneRow();
			IGeometry geometry = feat.getGeometry();
			geometry = addHoleToGeom(geometry,
				(Point2D[]) points.toArray(new Point2D[0]));
			feat.setGeometry(geometry);
			modifyFeature(virtualIndex.intValue(), feat);
			insertedGeometry = geometry;
		    }

		} else {
		    insertedGeometry = createNewPolygon((Point2D[]) points
			    .toArray(new Point2D[0]));
		    addGeometry(insertedGeometry);

		    virtualIndex = new Integer(vea.getRowCount() - 1);
		    rowEdited = vea.getRow(virtualIndex.intValue());
		}
		numShapes++;
	    }
	    // con esto limpio el ultimo punto pulsado para reinicializar el
	    // seguimiento de
	    // los snappers
	    getCadToolAdapter().setPreviousPoint((double[]) null);
	} catch (ExpansionFileReadException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ReadDriverException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public IGeometry getInsertedGeometry() {
	return insertedGeometry;
    }

    public IRowEdited getRowEdited() {
	return rowEdited;
    }

    public int getVirtualIndex() {
	return virtualIndex;
    }

    /**
     * Acci�n que se ejecuta cuando cancelamos el formulario de inserci�n. Borra
     * la �ltima fila a�adida al VectorialEditableAdapter. Si se estaba
     * digitalizando un hueco s�lo se elimina el �ltimo hueco. Si s�lo estaba
     * digitalizado el anillo exterior se elimina la fila entera.
     * */
    public void cancelInsertion() {
	VectorialLayerEdited vle = getVLE();
	VectorialEditableAdapter vea = vle.getVEA();
	IRowEdited row = null;
	try {
	    if (numShapes > 1) {
		row = (DefaultRowEdited) vea.getRow(virtualIndex.intValue());
		IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
		IGeometry geometry = feat.getGeometry();
		geometry = removeLastShape(geometry);
		feat.setGeometry(geometry);
		modifyFeature(virtualIndex.intValue(), feat);
	    } else {
		deleteFromVea();
		// getCadToolAdapter().delete(virtualIndex.intValue());
		// //TODO:Comprobar que es necesaria esta l�nea
		// virtualIndex = null;
	    }
	    numShapes--;
	} catch (ExpansionFileReadException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ReadDriverException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void deleteFromVea() {
	getCadToolAdapter().delete(virtualIndex.intValue());
	virtualIndex = null;
    }

    /**
     * Acci�n que guarda en base de datos la geometr�a digitalizada
     */
    public void save() {
	// NACHOV
	// insertGeometry(keys);
	initialize();
    }

    public void cancel() {
	// Se ha insertado en vea una geometria
	if ((virtualIndex != null) && (numShapes > 0)) {
	    getCadToolAdapter().delete(virtualIndex.intValue());
	}
	initialize();

    }

    private void initialize() {
	points.clear();
	virtualIndex = null;
	numShapes = 0;
	isHole = false;

	// con esto limpio el ultimo punto pulsado para reinicializar el
	// seguimiento de
	// los snappers
	getCadToolAdapter().setPreviousPoint((double[]) null);

    }

    public void clearPoints() {
	points.clear();
    }

    /**
     * M�todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param x
     *            par�metro x del punto que se pase para dibujar.
     * @param y
     *            par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {

	Point2D[] ps = (Point2D[]) points.toArray(new Point2D[0]);
	GeneralPathX gpx = new GeneralPathX();
	GeneralPathX gpx1 = new GeneralPathX();

	if (ps.length > 0) {
	    for (int i = 0; i < ps.length; i++) {
		if (i == 0) {
		    gpx.moveTo(ps[i].getX(), ps[i].getY());
		    gpx1.moveTo(ps[i].getX(), ps[i].getY());
		} else {
		    gpx.lineTo(ps[i].getX(), ps[i].getY());
		    gpx1.lineTo(ps[i].getX(), ps[i].getY());
		}

	    }
	    gpx.lineTo(x, y);
	    gpx.closePath();
	    gpx1.closePath();
	    IGeometry geom = ShapeFactory.createPolygon2D(gpx);
	    IGeometry geom1 = ShapeFactory.createPolygon2D(gpx1);
	    geom1.draw((Graphics2D) g, CADExtension.getEditionManager()
		    .getMapControl().getViewPort(),
		    DefaultCADTool.drawingSymbol);
	    geom.draw((Graphics2D) g, CADExtension.getEditionManager()
		    .getMapControl().getViewPort(), DefaultCADTool.modifySymbol);
	}
    }

    /**
     * M�todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param listaPuntos
     *            lista con los puntos a dibujar
     */
    public void drawOperation(Graphics g, ArrayList listaPuntos) {
	Point2D[] ps = (Point2D[]) points.toArray(new Point2D[0]);
	GeneralPathX gpx = new GeneralPathX();
	GeneralPathX gpx1 = new GeneralPathX();

	if (ps.length > 0) {
	    for (int i = 0; i < ps.length; i++) {
		if (i == 0) {
		    gpx.moveTo(ps[i].getX(), ps[i].getY());
		    gpx1.moveTo(ps[i].getX(), ps[i].getY());
		} else {
		    gpx.lineTo(ps[i].getX(), ps[i].getY());
		    gpx1.lineTo(ps[i].getX(), ps[i].getY());
		}

	    }
	    if (listaPuntos != null) {
		// recorremos los puntos de la lista y los a�adimos al path
		for (int i = 0; i < listaPuntos.size(); i++) {
		    Point2D punto = (Point2D) listaPuntos.get(i);
		    gpx.lineTo(punto.getX(), punto.getY());

		    // ademas pintamos el punto para que se note el snapin
		    if (i < listaPuntos.size() - 1) {
			Point2D actual = null;
			actual = CADExtension.getEditionManager()
				.getMapControl().getViewPort()
				.fromMapPoint(punto);
			int sizePixels = 12;
			int half = sizePixels / 2;
			g.drawRect((int) (actual.getX() - half),
				(int) (actual.getY() - half), sizePixels,
				sizePixels);
		    }
		}
	    }
	    gpx.closePath();
	    gpx1.closePath();
	    IGeometry geom = ShapeFactory.createPolygon2D(gpx);
	    IGeometry geom1 = ShapeFactory.createPolygon2D(gpx1);
	    geom1.draw((Graphics2D) g, CADExtension.getEditionManager()
		    .getMapControl().getViewPort(),
		    DefaultCADTool.drawingSymbol);
	    geom.draw((Graphics2D) g, CADExtension.getEditionManager()
		    .getMapControl().getViewPort(), DefaultCADTool.modifySymbol);
	} else {
	    if (listaPuntos != null) {
		// recorremos los puntos de la lista y los a�adimos al path
		for (int i = 0; i < listaPuntos.size(); i++) {
		    Point2D punto = (Point2D) listaPuntos.get(i);
		    if (i == 0) {
			gpx.moveTo(punto.getX(), punto.getY());
		    } else {
			gpx.lineTo(punto.getX(), punto.getY());
		    }
		    // ademas pintamos el punto para que se note el snapin
		    if (i < listaPuntos.size() - 1) {
			Point2D actual = null;
			actual = CADExtension.getEditionManager()
				.getMapControl().getViewPort()
				.fromMapPoint(punto);
			int sizePixels = 12;
			int half = sizePixels / 2;
			g.drawRect((int) (actual.getX() - half),
				(int) (actual.getY() - half), sizePixels,
				sizePixels);
		    }
		}
	    }
	    gpx.closePath();
	    IGeometry geom = ShapeFactory.createPolygon2D(gpx);
	    geom.draw((Graphics2D) g, CADExtension.getEditionManager()
		    .getMapControl().getViewPort(), DefaultCADTool.modifySymbol);
	}
    }

    /**
     * Add a diferent option.
     * 
     * @param s
     *            Diferent option.
     */
    public void addOption(String s) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }

    private IGeometry createNewPolygon(Point2D[] ps) {
	GeneralPathX gpx = new GeneralPathX();
	for (int i = 0; i < ps.length; i++) {
	    if (i == 0) {
		gpx.moveTo(ps[i].getX(), ps[i].getY());
	    } else {
		gpx.lineTo(ps[i].getX(), ps[i].getY());
	    }
	}
	gpx.closePath();
	// Los shell son CW y los holes CCW
	if (isHole) {
	    if (!gpx.isCCW()) {
		gpx.flip();
	    }
	} else {
	    if (gpx.isCCW()) {
		gpx.flip();
	    }
	}

	return ShapeFactory.createPolygon2D(gpx);
    }

    /**
     * Obtiene la geometria actual a partir de los puntos introducidos
     * */
    public IGeometry getCurrentGeom() {
	return createNewPolygon((Point2D[]) points.toArray(new Point2D[0]));
    }

    private IGeometry addHoleToGeom(IGeometry gp, Point2D[] ps) {

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

    private IGeometry removeLastShape(IGeometry gp) {

	GeneralPathX newGp = new GeneralPathX();
	double[] theData = new double[6];

	PathIterator theIterator;
	int theType;
	int numParts = 0;
	boolean endGeom = false;

	theIterator = gp.getPathIterator(null, FConverter.FLATNESS);
	while (!theIterator.isDone()) {
	    if (endGeom) {
		break;
	    }
	    theType = theIterator.currentSegment(theData);

	    switch (theType) {

	    case PathIterator.SEG_MOVETO:
		numParts++;
		if (numParts == numShapes) {
		    endGeom = true;
		    break;
		}
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

	return ShapeFactory.createPolygon2D(newGp);

    }

    public boolean isHole() {
	return isHole;
    }

    public void setHole(boolean isHole) {
	this.isHole = isHole;
    }

    public boolean pointInsidePolygon(double pointX, double pointY) {

	if (numShapes == 0) {
	    return true;
	}
	VectorialEditableAdapter vea = getVLE().getVEA();
	IRowEdited row = null;
	try {
	    row = (DefaultRowEdited) vea.getRow(virtualIndex.intValue());
	    IGeometry geometry = ((IFeature) row.getLinkedRow().cloneRow())
		    .getGeometry();

	    boolean pointInside = geometry.contains(new Point2D.Double(pointX,
		    pointY));
	    return pointInside;

	} catch (ExpansionFileReadException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ReadDriverException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return false;

    }

    public String getName() {
	return PluginServices.getText(this, "area_");
    }

    @Override
    public String toString() {
	return AREA_ACTION_COMMAND;
    }

    @Override
    public boolean isApplicable(int shapeType) {
	switch (shapeType) {
	// [LBD comment]
	// case GeometryTypes.POLYGON:
	// case GeometryTypes.MULTIPOLYGON:
	case FShape.POLYGON:
	    return true;
	}
	return false;
    }

    public void setPreviousTool(DefaultCADTool tool) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isMultiTransition() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void setMultiTransition(boolean condicion) {
	// TODO Auto-generated method stub

    }

}
