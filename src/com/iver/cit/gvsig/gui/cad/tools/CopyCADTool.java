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
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.CopyCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.CopyCADToolContext.CopyCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class CopyCADTool extends DefaultCADTool {
    private CopyCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public CopyCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new CopyCADToolContext(this);
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
                "_copy");
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
        CopyCADToolState actualState = (CopyCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        VectorialLayerEdited vle=getVLE();
        VectorialEditableAdapter vea = vle.getVEA();
        ArrayList selectedRow=getSelectedRows();
        ArrayList selectedRowAux=new ArrayList();
        if (status.equals("Copy.FirstPointToMove")) {
            firstPoint = new Point2D.Double(x, y);
        } else if (status.equals("Copy.SecondPointToMove")) {
            PluginServices.getMDIManager().setWaitCursor();
            lastPoint = new Point2D.Double(x, y);
            vea.startComplexRow();

            try {
            	for (int i = 0; i < selectedRow.size(); i++) {
            		DefaultRowEdited dre=(DefaultRowEdited)selectedRow.get(i);
                    DefaultFeature fea = (DefaultFeature)dre.getLinkedRow()
                                                             .cloneRow();
                    // Movemos la geometría
                    UtilFunctions.moveGeom(fea.getGeometry(), lastPoint.getX() -
                            firstPoint.getX(), lastPoint.getY() - firstPoint.getY());

                    int index=vea.addRow(fea,getName(),EditionEvent.GRAPHIC);
                    selectedRowAux.add(new DefaultRowEdited(fea,IRowEdited.STATUS_ADDED,vea.getInversedIndex(index)));
                }
            	vea.endComplexRow(getName());
                //clearSelection();
                //selectedRow.addAll(selectedRowAux);
                vle.setSelectionCache(selectedRowAux);
            } catch (DriverIOException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
        CopyCADToolState actualState = ((CopyCADToolContext) _fsm).getState();
        String status = actualState.getName();
        VectorialLayerEdited vle=getVLE();
        //ArrayList selectedRow=getSelectedRows();
        // drawHandlers(g, selectedRow,
        //          getCadToolAdapter().getMapControl().getViewPort()
        //              .getAffineTransform());
        if (status.equals("Copy.SecondPointToMove")) {
        	ViewPort vp=vle.getLayer().getMapContext().getViewPort();
            int dx = vp.fromMapDistance(x - firstPoint.getX());
            int dy = -vp.fromMapDistance(y - firstPoint.getY());
            Image img = vle.getSelectionImage();
            g.drawImage(img, dx, dy, null);
          /*  	 for (int i = 0; i < selectedRow.size(); i++) {
            		IRowEdited edRow = (IRowEdited) selectedRow.get(i);
         			IFeature feat = (IFeature) edRow.getLinkedRow();
         			IGeometry geometry = feat.getGeometry().cloneGeometry();
            		 // Movemos la geometría
                    UtilFunctions.moveGeom(geometry, x - firstPoint.getX(), y - firstPoint.getY());
                    geometry.draw((Graphics2D) g,
                        getCadToolAdapter().getMapControl().getViewPort(),
                        CADTool.drawingSymbol);
                }
          */
        }else{
        	if (!vle.getLayer().isVisible())
				return;
        	 Image imgSel = vle.getSelectionImage();
             g.drawImage(imgSel, 0, 0, null);
             Image imgHand = vle.getHandlersImage();
             g.drawImage(imgHand, 0, 0, null);
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

	public String getName() {
		return PluginServices.getText(this,"copy_");
	}

	public String toString() {
		return "_copy";
	}


}
