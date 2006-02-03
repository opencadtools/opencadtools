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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext.SelectionCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class SelectionCADTool extends DefaultCADTool {
    public final static int tolerance = 4;
    private SelectionCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;
    private ArrayList selectedHandler = new ArrayList();
    private ArrayList selectedRow = new ArrayList();
    private ArrayList selectedRowIndex = new ArrayList();
    private String tool="selection";

    //double FLATNESS=getCadToolAdapter().getMapControl().getViewPort().toMapDistance(2);

    /**
     * Crea un nuevo LineCADTool.
     */
    public SelectionCADTool() {

    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    	_fsm = new SelectionCADToolContext(this);
    	setNextTool("selection");
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y) {
    	((SelectionCADToolContext)_fsm).addPoint(x, y);
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
    public void transition(String s) {
        _fsm.addOption(s);
    }


    public boolean isSelected(double x, double y){
    	firstPoint=new Point2D.Double(x,y);
    	FBitSet selection=getCadToolAdapter().getVectorialAdapter().getSelection();
    	if (selection.cardinality() > 0) {
            //Se comprueba si se pincha un handler. El más cercano (o los más cercanos si hay empate)
            selectedRow.clear();
            selectedRowIndex.clear();
            selectedHandler.clear();

            double min = Double.MAX_VALUE;

            for (int i = selection.nextSetBit(0); i >= 0;
                    i = selection.nextSetBit(i + 1)) {
                Handler[] handlers=null;
				try {
					handlers = getCadToolAdapter().getVectorialAdapter().getShape(i)
					                         .getHandlers(IGeometry.SELECTHANDLER);
				} catch (DriverIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                DefaultFeature fea = null;

                for (int j = 0; j < handlers.length; j++) {
                    Point2D handlerPoint = handlers[j].getPoint();
                    double distance = firstPoint.distance(handlerPoint);
                    if ((distance <= min) &&
                            (distance < getCadToolAdapter()
                                                .getMapControl()
                                                .getViewPort()
                                                .toMapDistance(tolerance))) {
                        if (distance < min) {
                            selectedRow.clear();
                            selectedRowIndex.clear();
                            selectedHandler.clear();
                        }

                        min = distance;

                        if (fea == null) {
                            IGeometry clonedGeometry=null;
							try {
								clonedGeometry = getCadToolAdapter().getVectorialAdapter()
								                               .getShape(i)
								                               .cloneGeometry();
							} catch (DriverIOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            try {
								fea = (DefaultFeature) getCadToolAdapter().getVectorialAdapter().getRow(i).getLinkedRow();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (DriverIOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            selectedRow.add(new DefaultFeature(clonedGeometry,fea.getAttributes()));
                            selectedHandler.add(clonedGeometry
                                                   .getHandlers(IGeometry.SELECTHANDLER)[j]);
                            selectedRowIndex.add(new Integer(i));
                        }


                    }
                }
            }

        }

        if ((selectedRow.size() == 0) ||
                (selection.cardinality() == 0)) {
            // Se comprueba si se pincha en una gemometría
            PluginServices.getMDIManager().setWaitCursor();

            double tam = getCadToolAdapter().getMapControl()
                             .getViewPort().toMapDistance(tolerance);
            Rectangle2D rect = new Rectangle2D.Double(firstPoint.getX() -
                    tam, firstPoint.getY() - tam, tam * 2, tam * 2);
            int[] indexes = getCadToolAdapter().getVectorialAdapter().getRowsIndexes(rect);

            selection.clear();

            for (int i = 0; i < indexes.length; i++) {
                try {
					if (getCadToolAdapter().getVectorialAdapter().getShape(indexes[i])
					            .intersects(rect)) {
					    // .intersects(rect,FLATNESS)) {
					    selection.set(indexes[i], true);
					}
				} catch (DriverIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            PluginServices.getMDIManager().restoreCursor();
        }
        return selection.cardinality()>0;
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     *
     * @param selection Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y) {
        SelectionCADToolState actualState = (SelectionCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        FBitSet selection=getCadToolAdapter().getVectorialAdapter().getSelection();
        try {
            if (status.equals("ExecuteMap.Initial")) {
                //firstPoint = new Point2D.Double(x, y);
            } else if (status.equals("ExecuteMap.First")) {
                //PluginServices.getMDIManager().setWaitCursor();
                lastPoint = new Point2D.Double(x, y);
                selection.clear();

                double x1;
                double y1;
                double w1;
                double h1;

                if (firstPoint.getX() < lastPoint.getX()) {
                    x1 = firstPoint.getX();
                    w1 = lastPoint.getX() - firstPoint.getX();
                } else {
                    x1 = lastPoint.getX();
                    w1 = firstPoint.getX() - lastPoint.getX();
                }

                if (firstPoint.getY() < lastPoint.getY()) {
                    y1 = firstPoint.getY();
                    h1 = lastPoint.getY() - firstPoint.getY();
                } else {
                    y1 = lastPoint.getY();
                    h1 = firstPoint.getY() - lastPoint.getY();
                }

                Rectangle2D rect = new Rectangle2D.Double(x1, y1, w1, h1);

                int[] indexes = getCadToolAdapter().getVectorialAdapter().getRowsIndexes(new Rectangle2D.Double(
                            x1, y1, w1, h1));

                for (int i = 0; i < indexes.length; i++) {
                    if (firstPoint.getX() < lastPoint.getX()) {
                        if (rect.contains(getCadToolAdapter().getVectorialAdapter()
                                                  .getShape(indexes[i])
                                                  .getBounds2D())) {
                            selection.set(indexes[i], true);
                        }
                    } else {
                        if (getCadToolAdapter().getVectorialAdapter().getShape(indexes[i])
                                    .intersects(rect)) { //, 0.1)){
                            selection.set(indexes[i], true);
                        }
                    }
                }

                PluginServices.getMDIManager().restoreCursor();
                //cardinality = selection.cardinality();
            } else if (status.equals("ExecuteMap.Second")) {
            	for (int i = 0; i < selectedRow.size(); i++) {
                    Handler h = (Handler) selectedHandler.get(i);
                    DefaultFeature row = (DefaultFeature) selectedRow.get(i);
                    int index = ((Integer) selectedRowIndex.get(i)).intValue();

                    h.set(x, y);
                    //getVectorialAdapter().modifyRow(index, row);
                    modifyFeature(index,row);
                }
            }
       // } catch (IOException e) {
            // TODO Auto-generated catch block
        //    e.printStackTrace();
        } catch (DriverIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometrías seleccionadas.
     * @param x parámetro x del punto que se pase para dibujar.
     * @param y parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x,
        double y) {
    	SelectionCADToolState actualState = ((SelectionCADToolContext)_fsm).getState();
        String status = actualState.getName();
        FBitSet selection=getCadToolAdapter().getVectorialAdapter().getSelection();
        try {
			drawHandlers(g, selection, getCadToolAdapter().getMapControl().getViewPort().getAffineTransform());
		} catch (DriverIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (status.equals("ExecuteMap.First")) {
            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    4);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(x, firstPoint.getY());
            elShape.lineTo(x, y);
            elShape.lineTo(firstPoint.getX(), y);
            elShape.lineTo(firstPoint.getX(), firstPoint.getY());
            ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g,
                getCadToolAdapter().getMapControl().getViewPort(),
                CADTool.selectSymbol);
        } else if (status.equals("ExecuteMap.Second")) {
            for (int i = 0; i < selectedRow.size(); i++) {
                Handler h = (Handler) selectedHandler.get(i);
                IGeometry geom = ((IGeometry) ((DefaultFeature)selectedRow.get(i)).getGeometry()).cloneGeometry();
                System.out.println(geom.getBounds2D());
                int index = ((Integer) selectedRowIndex.get(i)).intValue();
                g.setColor(Color.gray);
                h.set(x, y);
                geom.draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
            }
        }

    }

    /**
     * Add a diferent option.
     *
     * @param sel DOCUMENT ME!
     * @param s Diferent option.
     */
    public void addOption(String s) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSelectedRowSize() {
        return this.selectedRow.size();
    }

    public String getStatus(){
    	try{
    	SelectionCADToolState actualState = (SelectionCADToolState) _fsm.getPreviousState();
         String status = actualState.getName();
         return status;
    	}catch (NullPointerException e) {
			return "ExecuteMap.Initial";
		}
    }


    public String getTool() {
		return tool;
	}

	public void setNextTool(String tool) {
		this.tool = tool;
	}
	public void end() {
    	CADExtension.setCADTool(getTool());
    }

}
