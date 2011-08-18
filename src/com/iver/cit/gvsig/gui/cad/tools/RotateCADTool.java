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
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.RotateCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.RotateCADToolContext.RotateCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class RotateCADTool extends DefaultCADTool {
    private RotateCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public RotateCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new RotateCADToolContext(this);
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
                "_rotate");
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
    	RotateCADToolState actualState = (RotateCADToolState) _fsm.getPreviousState();
    	String status = actualState.getName();
    	ArrayList selectedRow=getSelectedRows();
    	ArrayList selectedRowAux=new ArrayList();
    	VectorialLayerEdited vle=getVLE();
    	VectorialEditableAdapter vea = vle.getVEA();
    	if (status.equals("Rotate.PointMain")) {
    		firstPoint = new Point2D.Double(x, y);
    	} else if (status.equals("Rotate.AngleOrPoint")) {
    		PluginServices.getMDIManager().setWaitCursor();
    		lastPoint = new Point2D.Double(x,y);

    		double w;
    		double h;
    		w = lastPoint.getX() - firstPoint.getX();
    		h = lastPoint.getY() - firstPoint.getY();

    		try {
    			vea.startComplexRow();
    			for (int i = 0; i < selectedRow.size(); i++) {
    				DefaultRowEdited row=(DefaultRowEdited) selectedRow.get(i);
    				DefaultFeature fea = (DefaultFeature) row.getLinkedRow().cloneRow();
    				// Rotamos la geometry
    				UtilFunctions.rotateGeom(fea.getGeometry(), -Math.atan2(w,
    						h)
    						+ (Math.PI / 2), firstPoint.getX(), firstPoint
    						.getY());

    				vea.modifyRow(row.getIndex(), fea,
    						getName(),EditionEvent.GRAPHIC);
    				selectedRowAux.add(new DefaultRowEdited(fea,IRowEdited.STATUS_MODIFIED,row.getIndex()));
    			}

    			vea.endComplexRow(getName());
    			vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS, selectedRowAux);
    			//clearSelection();
    			//selectedRow.addAll(selectedRowAux);
    		} catch (ValidateRowException e) {
    			NotificationManager.addError(e.getMessage(),e);
    		} catch (ExpansionFileWriteException e) {
    			NotificationManager.addError(e.getMessage(),e);
    		} catch (ReadDriverException e) {
    			NotificationManager.addError(e.getMessage(),e);
    		} 
    		PluginServices.getMDIManager().restoreCursor();
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
        RotateCADToolState actualState = _fsm.getState();
        String status = actualState.getName();
        VectorialLayerEdited vle=getVLE();
        //ArrayList selectedRow=getSelectedRows();

       // drawHandlers(g, selectedRow,
       //          getCadToolAdapter().getMapControl().getViewPort()
       //              .getAffineTransform());
        if (status.equals("Rotate.AngleOrPoint")) {
			double w;
			double h;
			w = x - firstPoint.getX();
			h = y - firstPoint.getY();
			ViewPort vp=vle.getLayer().getMapContext().getViewPort();
			Point2D point = vp.fromMapPoint(firstPoint.getX(),
								firstPoint.getY());
			AffineTransform at = AffineTransform.getRotateInstance(Math.atan2(
								w, h) - (Math.PI / 2), (int) point.getX(),
							(int) point.getY());

			Image imgSel = vle.getSelectionImage();
            ((Graphics2D)g).drawImage(imgSel, at, null);
            drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y),DefaultCADTool.axisReferencesSymbol);
			///AffineTransform at = AffineTransform.getRotateInstance(+Math.atan2(
			///			w, h) - (Math.PI / 2), (int) point.getX(),
			///		(int) point.getY());
			///Image img = getCadToolAdapter().getVectorialAdapter().getImage();

			///((Graphics2D) g).drawImage(img, at, null);

			///drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y));

/*
			     for (int i = 0; i < selectedRow.size(); i++) {
			                   // IGeometry geometry =
								// getCadToolAdapter().getVectorialAdapter().getShape(i);
				IRowEdited edRow = (IRowEdited) selectedRow.get(i);
				IFeature feat = (IFeature) edRow.getLinkedRow();
				IGeometry geometry = feat.getGeometry().cloneGeometry();
				// Rotamos la geometry
				UtilFunctions.rotateGeom(geometry, -Math.atan2(w, h) + Math.PI
						/ 2, firstPoint.getX(), firstPoint.getY());

				geometry.draw((Graphics2D) g, getCadToolAdapter()
						.getMapControl().getViewPort(), CADTool.drawingSymbol);
				GeneralPathX elShape = new GeneralPathX(
						GeneralPathX.WIND_EVEN_ODD, 2);
				elShape.moveTo(firstPoint.getX(), firstPoint.getY());
				elShape.lineTo(x, y);
				ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g,
						getCadToolAdapter().getMapControl().getViewPort(),
						CADTool.drawingSymbol);

			}
			*/
		}else{
			if (!vle.getLayer().isVisible())
				return;
			 Image imgSel = vle.getSelectionImage();
             if (imgSel!=null)
            	 g.drawImage(imgSel, 0, 0, null);
             Image imgHand = vle.getHandlersImage();
             if (imgHand!=null)
            	 g.drawImage(imgHand, 0, 0, null);
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
    	RotateCADToolState actualState = (RotateCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        ArrayList selectedRow=getSelectedRows();
        VectorialLayerEdited vle=getVLE();
        VectorialEditableAdapter vea = vle.getVEA();
    	if (status.equals("Rotate.AngleOrPoint")) {
			try {

				vea.startComplexRow();
				///ArrayList selectedRowAux=new ArrayList();
				for (int i = 0; i < selectedRow.size(); i++) {
					DefaultRowEdited row=(DefaultRowEdited) selectedRow.get(i);
					DefaultFeature fea = (DefaultFeature) row.getLinkedRow().cloneRow();
						// Rotamos la geometry
						AffineTransform at = new AffineTransform();
						at.rotate(Math.toRadians(d),
	    						firstPoint.getX(), firstPoint.getY());
						fea.getGeometry().transform(at);
    					vea.modifyRow(row.getIndex(), fea,getName(),EditionEvent.GRAPHIC);
    					///selectedRowAux.add(new DefaultRowEdited(fea,IRowEdited.STATUS_MODIFIED,index));
				}
				vea.endComplexRow(getName());
				clearSelection();
				///selectedRow=selectedRowAux;
			} catch (ValidateRowException e) {
				NotificationManager.addError(e.getMessage(),e);
			} catch (ExpansionFileWriteException e) {
				NotificationManager.addError(e.getMessage(),e);
			} catch (ReadDriverException e) {
				NotificationManager.addError(e.getMessage(),e);
			} 
		}
    }

	public String getName() {
		return PluginServices.getText(this,"rotate_");
	}

	public String toString() {
		return "_rotate";
	}

	public void drawOperation(Graphics g, ArrayList pointList) {
		// TODO Auto-generated method stub
		
	}

	public boolean isMultiTransition() {
		// TODO Auto-generated method stub
		return false;
	}

	public void transition(InputEvent event) {
		// TODO Auto-generated method stub
		
	}

}
