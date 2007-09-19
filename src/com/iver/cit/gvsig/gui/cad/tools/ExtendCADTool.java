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
import java.awt.event.InputEvent;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.ExtendCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.ExtendCADToolContext.ExtendCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ExtendCADTool extends DefaultCADTool {
    private ExtendCADToolContext _fsm;

    /**
     * Crea un nuevo ExtendCADTool.
     */
    public ExtendCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new ExtendCADToolContext(this);
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
        ArrayList rowSelected=getSelectedRows();
        if (rowSelected.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "_extend");
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
    	ExtendCADToolState actualState = (ExtendCADToolState) _fsm.getPreviousState();
    	String status = actualState.getName();


    	if (status.equals("Extend.SelectGeometryToExtend")) {

    		VectorialLayerEdited vle=getVLE();
    		VectorialEditableAdapter vea=vle.getVEA();
    		vea.startComplexRow();
    		ArrayList selectedRow=getSelectedRows();
    		ArrayList selectedRowAux=new ArrayList();
    		//for (int i=0;i<selectedRow.size();i++) {
    		selectedRowAux.addAll(selectedRow);
    		//}
    		//selection();
    		vle.selectWithPoint(x,y,false);
    		ArrayList newSelectedRow=getSelectedRows();
    		try {
    			for (int i=0;i<selectedRowAux.size();i++) {
    				IRowEdited edRow1 = (IRowEdited) selectedRowAux.get(i);
    				DefaultFeature fea1 = (DefaultFeature) edRow1.getLinkedRow().cloneRow();
    				IGeometry geometry1 = null;
    				geometry1 = fea1.getGeometry();

    				IRowEdited edRow2 = (IRowEdited) newSelectedRow.get(i);
    				DefaultFeature fea2 = (DefaultFeature) edRow2.getLinkedRow().cloneRow();
    				IGeometry geometry2 = null;
    				geometry2 = fea2.getGeometry();
    				//for (int j=0;j<newSelectedRow.size();j++) {
    				//if (geometry1 instanceof FPolygon2D) {
    				fea2.setGeometry(intersectsGeometry(geometry2,geometry1));
    				//}
    				//}

    				vea.modifyRow(edRow2.getIndex(),fea2,getName(),EditionEvent.GRAPHIC);
    				clearSelection();
    				newSelectedRow.add(new DefaultRowEdited(fea2,IRowEdited.STATUS_MODIFIED,edRow2.getIndex()));
    			}

    			vea.endComplexRow(getName());
    			vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS, newSelectedRow);
    		} catch (ValidateRowException e) {
    			NotificationManager.addError(e.getMessage(),e);
    		} catch (ExpansionFileWriteException e) {
    			NotificationManager.addError(e.getMessage(),e);
    		} catch (ReadDriverException e) {
    			NotificationManager.addError(e.getMessage(),e);
    		}
    	}
    }

    private IGeometry intersectsGeometry(IGeometry geometry1, IGeometry geometry2) {
    	Point2D p3=null;
        Point2D p4=null;
    	Point2D p1=null;
        Point2D p2=null;
		GeneralPathX gpx=new GeneralPathX();
		PathIterator theIterator=geometry1.getInternalShape().getPathIterator(null,FConverter.FLATNESS);
		boolean first=true;
		double[] theData = new double[6];
        int theType;
			while (!theIterator.isDone()) {
                theType = theIterator.currentSegment(theData);
                switch (theType) {

                    case PathIterator.SEG_MOVETO:
                    	p1=new Point2D.Double(theData[0], theData[1]);
                        gpx.moveTo(p1.getX(),p1.getY());
                    	break;

                    case PathIterator.SEG_LINETO:
                    	p2=new Point2D.Double(theData[0], theData[1]);
                    	ArrayList lines=getLines(geometry2);
                    	boolean isLineTo=true;
                    	for (int i=0;i<lines.size();i++) {
                    		Point2D[] ps1=(Point2D[])lines.get(i);
                    		Point2D p=UtilFunctions.getIntersection(ps1[0],ps1[1],p1,p2);
//                    		GeneralPathX gpxAux=new GeneralPathX();
//                    		gpxAux.moveTo(p.getX(),p.getY());
//                    		gpxAux.lineTo(ps1[0].getX(),ps1[0].getY());
//                    		Geometry gjts1=FConverter.java2d_to_jts((FShape)ShapeFactory.createPolyline2D(gpxAux).getInternalShape());
//                    		Geometry gjts2=FConverter.java2d_to_jts((FShape)geometry2.getInternalShape());
//                    		GeometryCollection result=(GeometryCollection)gjts1.intersection(gjts2);
//                    		Point point=(Point)result.getGeometryN(1);
//                    		p=new Point2D.Double(point.getX(),point.getY());
                    		if (p!=null && first) {
                    			gpx.lineTo(p.getX(),p.getY());
                    			first=false;
                    			isLineTo=false;
                    			break;
                    		}else {
                    			//gpx.lineTo(p2.getX(),p2.getY());
                    		}
                    	}
                    	if (!first && isLineTo)
                    		gpx.lineTo(p2.getX(),p2.getY());

                    	break;

                } //end switch

                theIterator.next();
            } //end while loop
			return ShapeFactory.createPolyline2D(gpx);
	}


	private ArrayList getLines(IGeometry geometry1) {
		Point2D p1=null;
        Point2D p2=null;
		ArrayList lines=new ArrayList();
    	PathIterator theIterator=geometry1.getInternalShape().getPathIterator(null,FConverter.FLATNESS);
		double[] theData = new double[6];
        int theType;
			while (!theIterator.isDone()) {
                theType = theIterator.currentSegment(theData);
                switch (theType) {
                    case PathIterator.SEG_MOVETO:
                    	p1=new Point2D.Double(theData[0], theData[1]);
                        break;

                    case PathIterator.SEG_LINETO:
                    	p2=new Point2D.Double(theData[0], theData[1]);
                    	lines.add(new Point2D[] {p1,p2});
                    	break;

                } //end switch

                theIterator.next();
            } //end while loop
		return lines;
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
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }

    public String getName() {
		return PluginServices.getText(this,"extend_");
	}

	public String toString() {
		return "_extend";
	}
	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.LINE:
		case FShape.MULTI:
			return true;
		}
		return false;
	}

}
