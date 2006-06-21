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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.BreakCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.BreakCADToolContext.BreakCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class BreakCADTool extends DefaultCADTool {
	private BreakCADToolContext _fsm;
	private Point2D firstPoint;
	private Point2D secondPoint;
	private DefaultRowEdited rowEdited;
	/**
     * Crea un nuevo PolylineCADTool.
     */
    public BreakCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new BreakCADToolContext(this);
        firstPoint=null;
        secondPoint=null;
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
    	ArrayList selectedRow=getSelectedRows();
        if (selectedRow.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "_break");
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
    	 BreakCADToolState actualState = (BreakCADToolState) _fsm.getPreviousState();
         String status = actualState.getName();

         if (status.equals("Break.FirstPoint")) {
        	// if (rowEdited!=null && intersects(((DefaultFeature)rowEdited.getLinkedRow()).getGeometry(),new Point2D.Double(x,y)))
        		 firstPoint=new Point2D.Double(x,y);

         } else if (status.equals("Break.SecondPoint")) {
          // if (rowEdited !=null && intersects(((DefaultFeature)rowEdited.getLinkedRow()).getGeometry(),new Point2D.Double(x,y))){
        	   secondPoint=new Point2D.Double(x,y);
        	   try {
//        		   IGeometry geom=((DefaultFeature)rowEdited.getLinkedRow()).getGeometry();
//        		   if (geom instanceof FGeometryCollection) {
//        		     breakGeometryGC(rowEdited);
//        		}else {
					breakGeometry(rowEdited);
//				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DriverIOException e) {
				e.printStackTrace();
			}
           }
         //}
    }
  /*  private void breakGeometryGC(DefaultRowEdited dre) throws IOException, DriverIOException {
		GeneralPathX newGp1 = new GeneralPathX();
		GeneralPathX newGp2 = new GeneralPathX();
		FGeometryCollection gc=(FGeometryCollection)((DefaultFeature)rowEdited.getLinkedRow()).getGeometry();
		IGeometry[] geoms=gc.getGeometries();
		for (int i = 0;i<geoms.length;i++) {
			PathIterator theIterator=geoms[i].getPathIterator(null);
			double[] theData = new double[6];
			boolean isFirstPart=true;
			boolean isCut=false;
			int theType;
			int numParts = 0;

			Point2D previous=null;

	        while (!theIterator.isDone()) {
	            theType = theIterator.currentSegment(theData);
	            switch (theType) {

	                case PathIterator.SEG_MOVETO:
	                    numParts++;
	                    previous=new Point2D.Double(theData[0], theData[1]);
	                    if (isFirstPart)
	                		newGp1.moveTo(theData[0], theData[1]);
	                	else
	                		newGp2.moveTo(theData[0], theData[1]);
	                	break;

	                case PathIterator.SEG_LINETO:
	                	 if (previous!=null){
		                    	GeneralPathX gpx=new GeneralPathX();
		                    	gpx.moveTo(previous.getX(),previous.getY());
		                    	gpx.lineTo(theData[0], theData[1]);
		                    	IGeometry geom=ShapeFactory.createPolyline2D(gpx);
		                    	Point2D p1=getNearPoint(previous);
		                    	Point2D p2=getDistantPoint(previous);
		                    	if (intersects(geom,p1)){
		                    		isFirstPart=false;
		                    		newGp1.lineTo(p1.getX(),p1.getY());
		                    		newGp2.moveTo(p2.getX(),p2.getY());
		                    		isCut=true;
		                    	}
		                    }
	                	 previous=new Point2D.Double(theData[0], theData[1]);
	                	if (isFirstPart)
	                		newGp1.lineTo(theData[0], theData[1]);
	                	else
	                		newGp2.lineTo(theData[0], theData[1]);
	                	break;

	                case PathIterator.SEG_QUADTO:
	                	 if (previous!=null){
		                    	GeneralPathX gpx=new GeneralPathX();
		                    	gpx.moveTo(previous.getX(),previous.getY());
		                    	gpx.quadTo(theData[0], theData[1],theData[2], theData[3]);
		                    	IGeometry geom=ShapeFactory.createPolyline2D(gpx);
		                    	Point2D p1=getNearPoint(previous);
		                    	Point2D p2=getDistantPoint(previous);
		                    	if (intersects(geom,p1)){
		                    		isFirstPart=false;
		                    		newGp1.lineTo(p1.getX(),p1.getY());
		                    		newGp2.moveTo(p2.getX(),p2.getY());
		                    		isCut=true;
		                    	}
		                    }
	                	 previous=new Point2D.Double(theData[0], theData[1]);
	                	if (isFirstPart)
	                		newGp1.quadTo(theData[0], theData[1],theData[2], theData[3]);
	                	else
	                		newGp2.quadTo(theData[0], theData[1],theData[2], theData[3]);

	                	break;

	                case PathIterator.SEG_CUBICTO:
	                	 if (previous!=null){
		                    	GeneralPathX gpx=new GeneralPathX();
		                    	gpx.moveTo(previous.getX(),previous.getY());
		                    	gpx.curveTo(theData[0], theData[1],theData[2], theData[3],theData[4], theData[5]);
		                    	IGeometry geom=ShapeFactory.createPolyline2D(gpx);
		                    	Point2D p1=getNearPoint(previous);
		                    	Point2D p2=getDistantPoint(previous);
		                    	if (intersects(geom,p1)){
		                    		isFirstPart=false;
		                    		newGp1.lineTo(p1.getX(),p1.getY());
		                    		newGp2.moveTo(p2.getX(),p2.getY());
		                    		isCut=true;
		                    	}
		                    }
	                	 previous=new Point2D.Double(theData[0], theData[1]);
	                	if (isFirstPart)
	                		newGp1.curveTo(theData[0], theData[1],theData[2], theData[3],theData[4], theData[5]);
	                	else
	                		newGp2.curveTo(theData[0], theData[1],theData[2], theData[3],theData[4], theData[5]);

	                    break;

	                case PathIterator.SEG_CLOSE:
	                	//if (isFirstPart)
	                	//	newGp1.closePath();
	                	//else
	                	//	newGp2.closePath();
	                    break;
	            } //end switch

	            theIterator.next();
	        } //end while loop

	        if (isCut) {
	        	IGeometry geom1 = ShapeFactory.createPolyline2D(newGp1);
				IGeometry geom2 = ShapeFactory.createPolyline2D(newGp2);
				VectorialLayerEdited vle = getVLE();
				VectorialEditableAdapter vea = vle.getVEA();
				ArrayList selectedRow = vle.getSelectedRow();
				vea.startComplexRow();
				vea.removeRow(dre.getIndex(), getName(), EditionEvent.GRAPHIC);
				int num = vea.getRowCount();
				if (gc.isClosed()) {
					ArrayList geomsAux1 = new ArrayList();
					geomsAux1.add(geom2);
					for (int k = i + 1; k < geoms.length; k++) {
						geomsAux1.add(geoms[k]);
					}
					for (int k = 0; k < i; k++) {
						geomsAux1.add(geoms[k]);
					}
					geomsAux1.add(geom1);

					DefaultFeature df1 = new DefaultFeature(
							new FGeometryCollection((IGeometry[]) geomsAux1
									.toArray(new IGeometry[0])), dre
									.getAttributes(), String.valueOf(num));
					int index1 = vea.addRow(df1, PluginServices.getText(this,
							"parte1"), EditionEvent.GRAPHIC);

					clearSelection();
					selectedRow.add(new DefaultRowEdited(df1,
							IRowEdited.STATUS_ADDED, index1));
					vea.endComplexRow();
					return;
				}else {

					ArrayList geomsAux1 = new ArrayList();
					for (int k = 0; k < i; k++) {
						geomsAux1.add(geoms[k]);
					}
					geomsAux1.add(geom1);

					ArrayList geomsAux2 = new ArrayList();
					geomsAux2.add(geom2);
					for (int k = i + 1; k < geoms.length; k++) {
						geomsAux2.add(geoms[k]);
					}

					DefaultFeature df1 = new DefaultFeature(
							new FGeometryCollection((IGeometry[]) geomsAux1
									.toArray(new IGeometry[0])), dre
									.getAttributes(), String.valueOf(num));
					int index1 = vea.addRow(df1, PluginServices.getText(this,
							"parte1"), EditionEvent.GRAPHIC);
					DefaultFeature df2 = new DefaultFeature(
							new FGeometryCollection((IGeometry[]) geomsAux2
								.toArray(new IGeometry[0])), dre
								.getAttributes(), String.valueOf(num + 1));
					int index2 = vea.addRow(df2, PluginServices.getText(this,
						"parte2"), EditionEvent.GRAPHIC);
					clearSelection();
					selectedRow.add(new DefaultRowEdited(df2,
							IRowEdited.STATUS_ADDED, index2));
					selectedRow.add(new DefaultRowEdited(df1,
							IRowEdited.STATUS_ADDED, index1));

					vea.endComplexRow();
					return;
				}

			}
		}
	}
*/

	private void breakGeometry(DefaultRowEdited dre) throws IOException, DriverIOException {
		GeneralPathX newGp1 = new GeneralPathX();
		GeneralPathX newGp2 = new GeneralPathX();
		IGeometry geomAux=((DefaultFeature)rowEdited.getLinkedRow()).getGeometry();
		PathIterator theIterator=geomAux.getPathIterator(null);

		double[] theData = new double[6];
		boolean isFirstPart=true;
		int theType;
	    int numParts = 0;

	    Point2D previous=null;

	        while (!theIterator.isDone()) {
	            theType = theIterator.currentSegment(theData);
	            switch (theType) {

	                case PathIterator.SEG_MOVETO:
	                    numParts++;

	                    previous=new Point2D.Double(theData[0], theData[1]);

	                    if (isFirstPart)
	                		newGp1.moveTo(theData[0], theData[1]);
	                	else
	                		newGp2.moveTo(theData[0], theData[1]);
	                	break;

	                case PathIterator.SEG_LINETO:
	                	 if (previous!=null){
		                    	GeneralPathX gpx=new GeneralPathX();
		                    	gpx.moveTo(previous.getX(),previous.getY());
		                    	gpx.lineTo(theData[0], theData[1]);
		                    	IGeometry geom=ShapeFactory.createPolyline2D(gpx);
		                    	Point2D p1=getNearPoint(previous);
		                    	Point2D p2=getDistantPoint(previous);
		                    	if (intersects(geom,p1)){
		                    		isFirstPart=false;
		                    		newGp1.lineTo(p1.getX(),p1.getY());
		                    		newGp2.moveTo(p2.getX(),p2.getY());
		                    	}
		                    }
	                	 previous=new Point2D.Double(theData[0], theData[1]);
	                	if (isFirstPart)
	                		newGp1.lineTo(theData[0], theData[1]);
	                	else
	                		newGp2.lineTo(theData[0], theData[1]);
	                	break;

	                case PathIterator.SEG_QUADTO:
	                	 if (previous!=null){
		                    	GeneralPathX gpx=new GeneralPathX();
		                    	gpx.moveTo(previous.getX(),previous.getY());
		                    	gpx.quadTo(theData[0], theData[1],theData[2], theData[3]);
		                    	IGeometry geom=ShapeFactory.createPolyline2D(gpx);
		                    	Point2D p1=getNearPoint(previous);
		                    	Point2D p2=getDistantPoint(previous);
		                    	if (intersects(geom,p1)){
		                    		isFirstPart=false;
		                    		newGp1.lineTo(p1.getX(),p1.getY());
		                    		newGp2.moveTo(p2.getX(),p2.getY());
		                    	}
		                    }
	                	 previous=new Point2D.Double(theData[0], theData[1]);
	                	if (isFirstPart)
	                		newGp1.quadTo(theData[0], theData[1],theData[2], theData[3]);
	                	else
	                		newGp2.quadTo(theData[0], theData[1],theData[2], theData[3]);

	                	break;

	                case PathIterator.SEG_CUBICTO:
	                	 if (previous!=null){
		                    	GeneralPathX gpx=new GeneralPathX();
		                    	gpx.moveTo(previous.getX(),previous.getY());
		                    	gpx.curveTo(theData[0], theData[1],theData[2], theData[3],theData[4], theData[5]);
		                    	IGeometry geom=ShapeFactory.createPolyline2D(gpx);
		                    	Point2D p1=getNearPoint(previous);
		                    	Point2D p2=getDistantPoint(previous);
		                    	if (intersects(geom,p1)){
		                    		isFirstPart=false;
		                    		newGp1.lineTo(p1.getX(),p1.getY());
		                    		newGp2.moveTo(p2.getX(),p2.getY());
		                    	}
		                    }
	                	 previous=new Point2D.Double(theData[0], theData[1]);
	                	if (isFirstPart)
	                		newGp1.curveTo(theData[0], theData[1],theData[2], theData[3],theData[4], theData[5]);
	                	else
	                		newGp2.curveTo(theData[0], theData[1],theData[2], theData[3],theData[4], theData[5]);

	                    break;

	                case PathIterator.SEG_CLOSE:
	                	//if (isFirstPart)
	                	//	newGp1.closePath();
	                	//else
	                	//	newGp2.closePath();
	                    break;
	            } //end switch

	            theIterator.next();
	        } //end while loop
	        GeneralPathX gpx=new GeneralPathX();
	        gpx.append(geomAux.getInternalShape(),true);
	        if (gpx.isClosed()) {
	        	newGp2.append(newGp1.getPathIterator(null),true);
	        	IGeometry geom1=ShapeFactory.createPolyline2D(newGp2);
		        VectorialLayerEdited vle = getVLE();
		        VectorialEditableAdapter vea = vle.getVEA();
		        int num=vea.getRowCount();
		        DefaultFeature df1 = new DefaultFeature(geom1, dre.getAttributes(),String.valueOf(num));

		        vea.startComplexRow();
		        vea.removeRow(dre.getIndex(),getName(),EditionEvent.GRAPHIC);
		        int index1=vea.addRow(df1,PluginServices.getText(this,"parte1"),EditionEvent.GRAPHIC);
		        vea.endComplexRow();
		        ViewPort vp=CADExtension.getEditionManager().getMapControl().getViewPort();
		        BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D gs = selectionImage.createGraphics();
				clearSelection();
				ArrayList selectedRow = vle.getSelectedRow();
				selectedRow.add(new DefaultRowEdited(df1, IRowEdited.STATUS_ADDED, index1));
				geom1.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
				vle.drawHandlers(geom1.cloneGeometry(),gs,vp);
				vea.setSelectionImage(selectionImage);
	        }else {
	        IGeometry geom1=ShapeFactory.createPolyline2D(newGp1);
	        IGeometry geom2=ShapeFactory.createPolyline2D(newGp2);
	        VectorialLayerEdited vle = getVLE();
	        VectorialEditableAdapter vea = vle.getVEA();
	        int num=vea.getRowCount();
	        DefaultFeature df1 = new DefaultFeature(geom1, dre.getAttributes(),String.valueOf(num));
	        DefaultFeature df2 = new DefaultFeature(geom2, dre.getAttributes(),String.valueOf(num+1));

	        vea.startComplexRow();
	        int index1=vea.addRow(df1,PluginServices.getText(this,"parte1"),EditionEvent.GRAPHIC);
	        int index2=vea.addRow(df2,PluginServices.getText(this,"parte2"),EditionEvent.GRAPHIC);
	        vea.removeRow(dre.getIndex(),getName(),EditionEvent.GRAPHIC);

	        vea.endComplexRow();
	        ViewPort vp=CADExtension.getEditionManager().getMapControl().getViewPort();
	        BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			clearSelection();
			ArrayList selectedRow = vle.getSelectedRow();
			selectedRow.add(new DefaultRowEdited(df1, IRowEdited.STATUS_ADDED, index1));
			selectedRow.add(new DefaultRowEdited(df2, IRowEdited.STATUS_ADDED, index2));
			geom1.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
			vle.drawHandlers(geom1.cloneGeometry(),gs,vp);
			geom2.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
			vle.drawHandlers(geom2.cloneGeometry(),gs,vp);
			vea.setSelectionImage(selectionImage);
	        }
	}

	private Point2D getNearPoint(Point2D previous) {
		if (firstPoint.distance(previous)<=secondPoint.distance(previous)){
			return firstPoint;
		}
		return secondPoint;

	}
	private Point2D getDistantPoint(Point2D previous){
		if (firstPoint.distance(previous)>secondPoint.distance(previous)){
			return firstPoint;
		}
		return secondPoint;
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
    	 VectorialLayerEdited vle=getVLE();
         ArrayList selectedRows=vle.getSelectedRow();
         ViewPort vp=CADExtension.getEditionManager().getMapControl().getViewPort();
         if (selectedRows.size()==1){
        	 if (firstPoint!=null){
        		 IGeometry g1=ShapeFactory.createCircle(firstPoint,vp.toMapDistance(3));
        		 IGeometry g2=ShapeFactory.createCircle(firstPoint,vp.toMapDistance(5));
        		 g1.draw((Graphics2D)g,vp,DefaultCADTool.modifySymbol);
        		 g2.draw((Graphics2D)g,vp,DefaultCADTool.modifySymbol);
        	 }
        	 rowEdited = (DefaultRowEdited)((DefaultRowEdited)selectedRows.get(0));
             	//.getLinkedRow()
             	//	.cloneRow();
        	 IGeometry geom=((DefaultFeature)rowEdited.getLinkedRow()).getGeometry().cloneGeometry();
        	 if (intersects(geom,new Point2D.Double(x,y))){
        		 geom.draw((Graphics2D)g,vp,DefaultCADTool.drawingSymbol);
        	 }
         }
    }
    public boolean intersects(double x,double y){
    	Point2D p = new Point2D.Double(x, y);
		VectorialLayerEdited vle = getVLE();
		ArrayList selectedRows = vle.getSelectedRow();
		if (selectedRows.size() == 1) {
			rowEdited = (DefaultRowEdited) ((DefaultRowEdited) selectedRows
					.get(0));
			IGeometry g = ((DefaultFeature) rowEdited.getLinkedRow())
					.getGeometry().cloneGeometry();
			return intersects(g, p);
		}
		return false;
    }
    private boolean intersects(IGeometry geom, Point2D p){
    	double tol = 1;
		tol = CADExtension.getEditionManager().getMapControl().getViewPort()
				.toMapDistance((int) tol);
		Rectangle2D r = new Rectangle2D.Double(p.getX() - tol / 2, p.getY()
				- tol / 2, tol, tol);
		return (geom.intersects(r) && !geom.contains(r));
    }
    /**
     * Add a diferent option.
     *
     * @param s Diferent option.
     */
    public void addOption(String s) {
    	if (s.equals(PluginServices.getText(this,"cancel"))|| s.equals("c")|| s.equals("C")){
    		init();
    	}
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }
    public String getName() {
		return PluginServices.getText(this,"break_");
	}

	public String toString() {
		return "_break";
	}

	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.MULTI:
		case FShape.LINE:
			return true;
		}
		return false;
	}


}
