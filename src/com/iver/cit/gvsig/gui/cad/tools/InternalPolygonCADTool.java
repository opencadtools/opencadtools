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
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommadException;
import com.iver.cit.gvsig.gui.cad.tools.smc.InternalPolygonCADToolContext;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class InternalPolygonCADTool extends DefaultCADTool {
    private InternalPolygonCADToolContext _fsm;
   private ArrayList points=new ArrayList();
    /**
     * Crea un nuevo PolylineCADTool.
     */
    public InternalPolygonCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new InternalPolygonCADToolContext(this);
        points.clear();
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
        //_fsm.addValue(sel,d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(String s) throws CommadException {
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
                "_internalpolygon");
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
    	VectorialLayerEdited vle=getVLE();
        ArrayList selectedRows=vle.getSelectedRow();
        if (selectedRows.size()==1){
			points.add(new Point2D.Double(x,y));
        }
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
    	gpx.lineTo(x,y);
    	gpx.closePath();
    	gpx1.closePath();
    	IGeometry geom=ShapeFactory.createPolygon2D(gpx);
    	IGeometry geom1=ShapeFactory.createPolygon2D(gpx1);
    	geom1.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.drawingSymbol);
    	geom.draw((Graphics2D)g,CADExtension.getEditionManager().getMapControl().getViewPort(),DefaultCADTool.modifySymbol);
    	}
    }

    /**
     * Add a diferent option.
     *
     * @param s Diferent option.
     */
    public void addOption(String s) {
    	VectorialLayerEdited vle=getVLE();
    	ArrayList selectedRows=vle.getSelectedRow();
    	VectorialEditableAdapter vea = vle.getVEA();
    	IRowEdited row=null;
    	if (s.equals(PluginServices.getText(this,"end")) || s.equals("e")|| s.equals("E")){
    		if (points.size()>0){
    			row =  (DefaultRowEdited) selectedRows.get(0);
    			IFeature feat = (IFeature) row.getLinkedRow().cloneRow();

    			IGeometry geometry=feat.getGeometry();
    			geometry=createNewPolygon(geometry,(Point2D[])points.toArray(new Point2D[0]));
    			DefaultFeature df=new DefaultFeature(geometry,feat.getAttributes());
    			DefaultRowEdited dre=new DefaultRowEdited(df,DefaultRowEdited.STATUS_MODIFIED,row.getIndex());
    			try {
					vea.modifyRow(dre.getIndex(),dre.getLinkedRow(),getName(),EditionEvent.GRAPHIC);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (DriverIOException e) {
					e.printStackTrace();
				}
				selectedRows.clear();
	    		selectedRows.add(dre);
    		}
    		points.clear();


    	}else if (s.equals(PluginServices.getText(this,"cancel"))|| s.equals("c")|| s.equals("C")){
    		points.clear();
    	}
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }
    private IGeometry createNewPolygon(IGeometry gp,Point2D[] ps) {
        GeneralPathX newGp = new GeneralPathX();
        double[] theData = new double[6];

        PathIterator theIterator;
        int theType;
        int numParts = 0;


        theIterator = gp.getPathIterator(null); //, flatness);
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
                    newGp.curveTo(theData[0], theData[1], theData[2], theData[3], theData[4], theData[5]);
                    break;

                case PathIterator.SEG_CLOSE:
                	newGp.closePath();
                    break;
            } //end switch

            theIterator.next();
        } //end while loop
        newGp.moveTo(ps[ps.length-1].getX(),ps[ps.length-1].getY());
     for (int i=ps.length-1;i>=0;i--){
    	 newGp.lineTo(ps[i].getX(),ps[i].getY());
     }
     newGp.closePath();
     return ShapeFactory.createPolygon2D(newGp);
    }
    public String getName() {
		return PluginServices.getText(this,"internal_polygon_");
	}

	public String toString() {
		return "_internalpolygon";
	}

	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.POINT:
		case FShape.LINE:
		case FShape.MULTIPOINT:
			return false;
		}
		return true;
	}


}
