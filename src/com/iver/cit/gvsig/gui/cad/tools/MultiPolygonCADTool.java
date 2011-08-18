/*
 * Copyright 2008 Deputación Provincial de A Coruña
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
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
import com.iver.andami.messages.NotificationManager;
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
import com.iver.cit.gvsig.gui.cad.tools.smc.MultiPolygonCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.MultiPolygonCADToolContext.MultiAreaCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Isabel Pérez-Urria Lage [LBD]
 * @author Nacho Varela [Cartolab]
 * @author Pablo Sanxiao [CartoLab]
 */
public class MultiPolygonCADTool extends InsertionCADTool {

	private MultiPolygonCADToolContext _fsm;

	/** 
	 * It contents the points of the polygon that has just been digitalized.
	 */
	private ArrayList<Point2D> points = new ArrayList<Point2D>();

	private int numShapes;
	
	private boolean isHole;

	/**
	 * Initial method, used in order to load all the necessary stuff before using the tool.
	 */
	public void init() {
		clear();
		_fsm = new MultiPolygonCADToolContext(this);
		//cleaning last point clicked in order to reset the snappers
		getCadToolAdapter().setPreviousPoint((double[])null);
	}
	
	public void clear() {
		super.init();
		this.setMultiTransition(true);
		points.clear();
		numShapes = 0;
		isHole = false;
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

	public void transition(InputEvent event){
		_fsm.removePoint(event, points.size());
	}


	/**
	 * Its equivalent to the transition of the prototype but without the
	 * editableFeatureSource parameter, that will be created.
	 *
	 * @param x coordinate x of the point.
	 * @param y coordinate y of the point.
	 */
	public void addPoint(double x, double y,InputEvent event) {
		points.add(new Point2D.Double(x,y));
	}



	public void removePoint(InputEvent event) {
		MultiAreaCADToolState actualState = (MultiAreaCADToolState) _fsm.getPreviousState();
		String status = actualState.getName();

		if (( status.equals("MultiArea.FirstPoint")) || (status.equals("MultiArea.SecondPoint"))){
//			First polygon was not introduced yet
			if(numShapes == 0){
				cancel();
			}else{
				points.clear();
			}
			getCadToolAdapter().setPreviousPoint((double[])null);
		}else if((status.equals("MultiArea.ThirdPoint"))|| (status.equals("MultiArea.NextPoint"))){
			getCadToolAdapter().setPreviousPoint((Point2D)points.get(points.size()-2));
			points.remove(points.size()-1);
		}
	}


	/**
	 * Action that open the form
	 */
	public void openForm(){
//		keys = openInsertEntityForm();
		if (keys.size() == 0){
			setFormState(InsertionCADTool.FORM_CANCELLED);
		}else{
			setFormState(InsertionCADTool.FORM_ACCEPTED);
		}
	}


	/**
	 * Action that saves the edited geometry in the VectorialEditableAdapter
	 * */
	public void saveTempGeometry(){

		VectorialLayerEdited vle=getVLE();
		VectorialEditableAdapter vea = vle.getVEA();
		IRowEdited row=null;

		try {
			if (points.size() > 0){
				if(numShapes != 0){
					if(virtualIndex != null){
						row = (DefaultRowEdited)vea.getRow(virtualIndex.intValue());
						IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
						IGeometry geometry = feat.getGeometry();
						geometry = addShapeToGeom(geometry, (Point2D[])points.toArray(new Point2D[0]));
						feat.setGeometry(geometry);
						modifyFeature(virtualIndex.intValue(), feat);
					}
				}else{
					addGeometry(createNewPolygon((Point2D[])points.toArray(new Point2D[0])));
						virtualIndex = new Integer(vea.getRowCount()-1);
				}
				numShapes++;
			}
			getCadToolAdapter().setPreviousPoint((double[])null);

			} catch (ReadDriverException e) {
				NotificationManager.addError(e.getMessage(),e);
			}
	}
	

	/**
	 * It gets the current geometry from the introduced points.
	 * */
	public IGeometry getCurrentGeom(){
		VectorialLayerEdited vle=getVLE();
		VectorialEditableAdapter vea = vle.getVEA();
		IRowEdited row=null;

		IGeometry geom = null;
		try {

			if (points.size() > 0){
				if(numShapes != 0){
					if(virtualIndex != null){
							row = (DefaultRowEdited)vea.getRow(virtualIndex.intValue());
						IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
						IGeometry geometry = feat.getGeometry();
						geom = addShapeToGeom(geometry, (Point2D[])points.toArray(new Point2D[0]));

					}
				}else{
					geom = createNewPolygon((Point2D[])points.toArray(new Point2D[0]));
				}
			}

		} catch (ExpansionFileReadException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		}
		return geom;
	}

	
	
	/**
	 * Action that its executed when we cancel the insertion form.
	 * It deletes the last row adde to the VectorialEditableAdapter, if there
	 * is only one polygon. If there are more than one polygon it deletes the one
	 * from the memory.
	 */
	public void cancelInsertion(){
		VectorialLayerEdited vle=getVLE();
		VectorialEditableAdapter vea = vle.getVEA();
		IRowEdited row=null;
		try {

			if(numShapes > 1){
					row = (DefaultRowEdited)vea.getRow(virtualIndex.intValue());
				IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
				IGeometry geometry = feat.getGeometry();
				geometry = removeLastShape(geometry);
				feat.setGeometry(geometry);
				modifyFeature(virtualIndex.intValue(), feat);
			}else{
				getCadToolAdapter().delete(virtualIndex.intValue());
				virtualIndex = null;
			}
			numShapes--;
		} catch (ExpansionFileReadException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		}
	}



	/**
	 * It saves in the BD the geometry
	 */
	public void save(){
//		insertGeometry(keys);
		_fsm = new MultiPolygonCADToolContext(this);
		initialize();
	}


	public void cancel(){
		if((virtualIndex != null) && (numShapes > 0)){
			getCadToolAdapter().delete(virtualIndex.intValue());
			initialize();
		}else{
			initialize();
		}
	}

	private void initialize(){
		points.clear();
		keys.clear();
		initializeFormState();
		virtualIndex = null;
		numShapes = 0;
		isHole = false;
		getCadToolAdapter().setPreviousPoint((double[])null);
		

	}


	public void clearPoints(){
		points.clear();
	}

	/**
	 * It draws the necessary according to the current state.
	 *
	 * @param g Graphics where draw.
	 * @param x coordinate x of the point.
	 * @param y coordinate y of the point.
	 */
	public void drawOperation(Graphics g, double x, double y) {

		GeneralPathX gpx=new GeneralPathX();
		GeneralPathX gpx1=new GeneralPathX();

		if (points.size()>0){
			for (int i=0;i<points.size();i++){
				if (i==0){
					gpx.moveTo(points.get(i).getX(),points.get(i).getY());
					gpx1.moveTo(points.get(i).getX(),points.get(i).getY());
				}else{
					gpx.lineTo(points.get(i).getX(),points.get(i).getY());
					gpx1.lineTo(points.get(i).getX(),points.get(i).getY());
					drawLine((Graphics2D) g, points.get(i-1), points.get(i),DefaultCADTool.geometrySelectSymbol);
				}

			}
			gpx.lineTo(x,y);
			gpx.closePath();
			gpx1.closePath();
			IGeometry geom=ShapeFactory.createPolygon2D(gpx);
			IGeometry geom1=ShapeFactory.createPolygon2D(gpx1);
			geom1.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.drawingSymbol);
			geom.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.modifySymbol);
			
			MultiAreaCADToolState actualState = ((MultiPolygonCADToolContext)_fsm).getState();
			String status = actualState.getName();
			if (status.equals("MultiArea.SecondPoint") || status.equals("MultiArea.ThirdPoint") 
					|| status.equals("MultiArea.NextPoint")) {
					drawLine((Graphics2D) g, points.get(points.size()-1), new Point2D.Double(x, y),DefaultCADTool.geometrySelectSymbol);
			}
			
		}

	}

	/**
	 * It draws the necessary according to the current state.
	 *
	 * @param g Graphics where draw.
	 * @param pointsList list of the points to be drawn.
	 */
	public void drawOperation(Graphics g, ArrayList pointsList) {
		Point2D[] ps=(Point2D[])points.toArray(new Point2D[0]);
		GeneralPathX gpx=new GeneralPathX();
		GeneralPathX gpx1=new GeneralPathX();

		if (ps.length>0){
			for (int i=0;i<ps.length;i++){
				if (i==0){
					gpx.moveTo(ps[i].getX(),ps[i].getY());
					gpx1.moveTo(ps[i].getX(),ps[i].getY());
				}else{
					gpx.lineTo(ps[i].getX(),ps[i].getY());
					gpx1.lineTo(ps[i].getX(),ps[i].getY());
				}

			}
			if(pointsList!=null){
				for(int i=0; i<pointsList.size();i++){
					Point2D point = (Point2D)pointsList.get(i);
					gpx.lineTo(point.getX(), point.getY());

					if(i<pointsList.size()-1){
						Point2D actual = null;
						actual = CADExtension.getEditionManager().getMapControl().getViewPort().fromMapPoint(point);
						int sizePixels = 12;
						int half = sizePixels / 2;
						g.drawRect((int) (actual.getX() - half),
								(int) (actual.getY() - half),
								sizePixels, sizePixels);
					}
				}
			}
			gpx.closePath();
			gpx1.closePath();
			IGeometry geom=ShapeFactory.createPolygon2D(gpx);
			IGeometry geom1=ShapeFactory.createPolygon2D(gpx1);
			geom1.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.drawingSymbol);
			geom.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.modifySymbol);
		}else{
			if(pointsList!=null){
				for(int i=0; i<pointsList.size();i++){
					Point2D point = (Point2D)pointsList.get(i);
					if (i==0){
						gpx.moveTo(point.getX(), point.getY());
					}else{
						gpx.lineTo(point.getX(), point.getY());
					}
					if(i<pointsList.size()-1){
						Point2D actual = null;
						actual = CADExtension.getEditionManager().getMapControl().getViewPort().fromMapPoint(point);
						int sizePixels = 12;
						int half = sizePixels / 2;
						g.drawRect((int) (actual.getX() - half),
								(int) (actual.getY() - half),
								sizePixels, sizePixels);
					}
				}
			}
			gpx.closePath();
			IGeometry geom=ShapeFactory.createPolygon2D(gpx);
			geom.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.modifySymbol);
		}

	}

	/**
	 * Add a diferent option.
	 *
	 * @param s Diferent option.
	 */
	public void addOption(String s) {

	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
	public void addValue(double d) {
	}


	private IGeometry createNewPolygon(Point2D[] ps) {

		GeneralPathX gpx=new GeneralPathX();
		gpx.moveTo(ps[ps.length-1].getX(),ps[ps.length-1].getY());
		for (int i=ps.length-1;i>=0;i--){
			gpx.lineTo(ps[i].getX(),ps[i].getY());
		}
		gpx.lineTo(ps[ps.length-1].getX(),ps[ps.length-1].getY());
		if (!gpx.isCCW()) {
			gpx.flip();
		}
		return ShapeFactory.createPolygon2D(gpx);
	}

	/** It allows to add holes to the current polygon or new polygons to
	 * a multipolygon.
	 * */
	private IGeometry addShapeToGeom(IGeometry gp, Point2D[] ps) {

		GeneralPathX oldGp = new GeneralPathX();
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
				oldGp.moveTo(theData[0], theData[1]);
				break;

			case PathIterator.SEG_LINETO:
				oldGp.lineTo(theData[0], theData[1]);
				break;

			case PathIterator.SEG_QUADTO:
				oldGp.quadTo(theData[0], theData[1], theData[2], theData[3]);
				break;

			case PathIterator.SEG_CUBICTO:
				oldGp.curveTo(theData[0], theData[1], theData[2], theData[3], theData[4], theData[5]);
				break;

			case PathIterator.SEG_CLOSE:
				oldGp.closePath();
				break;
			} //end switch

			theIterator.next();
		} //end while loop
		
		//Building the new polygon
		GeneralPathX gpx=new GeneralPathX();
        for (int i=0;i<ps.length;i++){
        	if (i==0){
        		gpx.moveTo(ps[i].getX(),ps[i].getY());
        	}else{
        		gpx.lineTo(ps[i].getX(),ps[i].getY());
        	}
        }        
        gpx.closePath();
        //shell are CW and holes are CCW
        if(isHole){
        	if(!gpx.isCCW())
        		gpx.flip();
        }else{
        	if(gpx.isCCW())
        		gpx.flip();
        }

		oldGp.append(gpx, false);

		return ShapeFactory.createPolygon2D(oldGp);

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
			if(endGeom)
				break;
			theType = theIterator.currentSegment(theData);

			switch (theType) {

			case PathIterator.SEG_MOVETO:
				numParts++;
				if(numParts == numShapes){
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
				newGp.curveTo(theData[0], theData[1], theData[2], theData[3], theData[4], theData[5]);
				break;

			case PathIterator.SEG_CLOSE:
				newGp.closePath();
				break;
			} //end switch

			theIterator.next();
		} //end while loop

		return ShapeFactory.createPolygon2D(newGp);

	}

	public boolean pointInsidePolygon(double pointX, double pointY){

		if(numShapes == 0){
			return true;
		}
		VectorialEditableAdapter vea = getVLE().getVEA();
		IRowEdited row=null;

		try {
			row = (DefaultRowEdited)vea.getRow(virtualIndex.intValue());
			IGeometry geometry = ((IFeature) row.getLinkedRow().cloneRow()).getGeometry();

			boolean pointInside = geometry.contains(new Point2D.Double(pointX, pointY));
			return pointInside;

		} catch (ExpansionFileReadException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (ReadDriverException e) {
				NotificationManager.addError(e.getMessage(),e);
		}

		return false;

	}
	
	public boolean isHole() {
		return isHole;
	}

	public void setHole(boolean isHole) {
		this.isHole = isHole;
	}
	
	public String getName() {
		return PluginServices.getText(this,"multiarea_");
	}

	public String toString() {
		return "_multiarea";
	}

	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.POLYGON:
		case FShape.MULTI:
			return true;
		}
		return false;
	}
	
	public int getPointsCount() {
		return points.size();
	}

	public boolean isMultiTransition() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setMultiTransition(boolean condicion) {
		// TODO Auto-generated method stub
		
	}

	public void setPreviousTool(DefaultCADTool tool) {
		// TODO Auto-generated method stub
		
	}
}
