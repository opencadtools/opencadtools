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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.JoinCADToolContext;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class JoinCADTool extends DefaultCADTool {
	private JoinCADToolContext _fsm;
	private SelectionCADTool selectionCADTool;
	/**
     * Crea un nuevo JoinCADTool.
     */
    public JoinCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new JoinCADToolContext(this);
        try {
			clearSelection();
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		}
        selectionCADTool=new SelectionCADTool();//(SelectionCADTool) CADExtension.getCADTool();
        selectionCADTool.init();
        selectionCADTool.multipleSelection(true);
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
    	if (!CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
            ((SelectionCADTool)CADExtension.getCADTool()).multipleSelection(true);
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "_join");
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
       	 selectionCADTool.selectFeatures(x,y,event);
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
         DefaultRowEdited[] dres=(DefaultRowEdited[])selectedRows.toArray(new DefaultRowEdited[0]);
         for (int i=0;i<dres.length;i++) {
        	 IGeometry geom=((IFeature)dres[i].getLinkedRow()).getGeometry().cloneGeometry();
        	 geom.draw((Graphics2D)g,vp,DefaultCADTool.geometrySelectSymbol);
         }
    }
  /**
     * Add a diferent option.
     *
     * @param s Diferent option.
     */
    public void addOption(String s) {
    	if (s.equals(PluginServices.getText(this,"cancel"))){
    		init();
    	}else {
    		try {
				joinGeometries();
			} catch (ReadDriverException e) {
				NotificationManager.addError(e.getMessage(),e);
			} catch (ValidateRowException e) {
				NotificationManager.addError(e.getMessage(),e);
			} 
    	}
    }
    private Handler[] getHandlers(DefaultRowEdited[] selectedRows) {
    	ArrayList handlers=new ArrayList();
    	for (int i=0;i<selectedRows.length;i++) {
    		IGeometry geometry=((IFeature)selectedRows[i].getLinkedRow()).getGeometry();
    		Handler[] hs=geometry.getHandlers(IGeometry.SELECTHANDLER);
    		for (int j=0;j<hs.length;j++) {
    			handlers.add(hs[j]);
    		}
    	}
    	return (Handler[])handlers.toArray(new Handler[0]);
    }
    private boolean isIntoRect(Point2D p,Handler[] handlers) {
    	double tol=0.02;
    	double angle=Double.NEGATIVE_INFINITY;

    	//for (int i=0;i<handlers.length;i++) {
    	//if (handlers.length>0) {
    	Point2D p0=p;
    		for(int j=0;j<handlers.length;j++) {
    			Point2D p1Aux=handlers[j].getPoint();
    			if (!handlers[0].equalsPoint(handlers[j])) {
    				double angleAux=UtilFunctions.getAngle(p0,p1Aux);
    				if (angle!=Double.NEGATIVE_INFINITY && (angle>angleAux+tol || angle<angleAux-tol)) {
    					return false;
    				}
    				angle=angleAux;
    			}
    		}
    	//}
    	return true;
    }
private Point2D[] startAndEndPoints(Handler[] handlers) {
	Point2D first=null;
	Point2D end=null;
	for (int i=0;i<handlers.length;i++) {
		Point2D aux=(Point2D)handlers[i].getPoint().clone();
		if (first == null || aux.getX()<first.getX()) {
			first=aux;
		}
		if (end == null || aux.getX()>end.getX()) {
			end=aux;
		}
	}
	if (first.getX()==end.getX()) {
		for (int i=0;i<handlers.length;i++) {
			Point2D aux=(Point2D)handlers[i].getPoint().clone();
			if (first == null || aux.getY()<first.getY()) {
				first=aux;
			}
			if (end == null || aux.getY()>end.getY()) {
				end=aux;
			}
		}
	}
	return new Point2D[] {first,end};
}



    private void joinGeometries() throws ReadDriverException, ExpansionFileReadException, ValidateRowException, ExpansionFileWriteException {
    	DefaultRowEdited[] rows = (DefaultRowEdited[]) getSelectedRows()
				.toArray(new DefaultRowEdited[0]);
		Handler[] handlers = getHandlers(rows);
		if (handlers.length < 2) {
			return;
		}
		Point2D[] points = startAndEndPoints(handlers);

		if (!isIntoRect(points[0], handlers))
			return;

		VectorialLayerEdited vle = getVLE();
		VectorialEditableAdapter vea = vle.getVEA();
		vea.startComplexRow();

		String newFID = vea.getNewFID();
		GeneralPathX gpx = new GeneralPathX();
		gpx.moveTo(points[0].getX(), points[0].getY());
		gpx.lineTo(points[1].getX(), points[1].getY());

		IGeometry geom = ShapeFactory.createPolyline2D(gpx);
		DefaultFeature df1 = new DefaultFeature(geom, null, newFID);

		for (int i = rows.length - 1; i >= 0; i--) {
			vea.removeRow(rows[i].getIndex(), getName(), EditionEvent.GRAPHIC);
		}
		int index1 = vea.addRow(df1, PluginServices.getText(this, "parte1"),
				EditionEvent.GRAPHIC);
		//clearSelection();
		ArrayList selectedRowAux = new ArrayList();
		selectedRowAux.add(new DefaultRowEdited(df1, IRowEdited.STATUS_ADDED,
				vea.getInversedIndex(index1)));
		vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS, selectedRowAux);
		vea.endComplexRow(getName());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
    public void addValue(double d) {
    }
    public String getName() {
		return PluginServices.getText(this,"join_");
	}

	public String toString() {
		return "_join";
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
