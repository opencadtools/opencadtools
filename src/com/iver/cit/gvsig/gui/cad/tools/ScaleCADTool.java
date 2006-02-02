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
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.IOException;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.ScaleCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.ScaleCADToolContext.ScaleCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ScaleCADTool extends DefaultCADTool {
    private ScaleCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;
	private Point2D scalePoint;
	private Double orr;
	private Double frr;
	private Double ore;
	private Double fre;

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public ScaleCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new ScaleCADToolContext(this);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y) {
        _fsm.addPoint(x, y);
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
    public void transition(String s) {
        _fsm.addOption(s);
    }

    /**
     * DOCUMENT ME!
     */
    public void selection() {
        FBitSet selection = CADExtension.getCADToolAdapter()
                                        .getVectorialAdapter().getSelection();

        if (selection.cardinality() == 0) {
            CADExtension.setCADTool("selection");
            ((SelectionCADTool) CADExtension.getCADToolAdapter().getCadTool()).setNextTool(
                "scale");
        }
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como parámetro el
     * editableFeatureSource que ya estará creado.
     *
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y) {
        ScaleCADToolState actualState = (ScaleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        VectorialEditableAdapter vea = getCadToolAdapter().getVectorialAdapter();
        FBitSet selection = vea.getSelection();


        if (status.equals("ExecuteMap.Initial")) {
				firstPoint = new Point2D.Double(x, y);
			    scalePoint = firstPoint;
		} else if (status.equals("ExecuteMap.First")) {
			PluginServices.getMDIManager().setWaitCursor();
			lastPoint = new Point2D.Double(x, y);

			double w;
			double h;
			w = lastPoint.getX() - firstPoint.getX();
			h = lastPoint.getY() - firstPoint.getY();

			try {
				double size=getCadToolAdapter().getMapControl().getViewPort().toMapDistance(getCadToolAdapter().getMapControl().getWidth());
				scale(firstPoint.distance(lastPoint)/(size/40));
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			PluginServices.getMDIManager().restoreCursor();
		} else if (status.equals("ExecuteMap.Second")) {
			orr = new Point2D.Double(x, y);
		} else if (status.equals("ExecuteMap.Third")) {
			frr = new Point2D.Double(x, y);
		} else if (status.equals("ExecuteMap.Fourth")) {
			ore = new Point2D.Double(x, y);
			firstPoint = ore;
		} else if (status.equals("ExecuteMap.Fiveth")) {
			fre = new Point2D.Double(x, y);

			double distrr = orr.distance(frr);
			double distre = ore.distance(fre);
			double escalado = distre / distrr;

			try {
				scale(escalado);
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}




        if (status.equals("ExecuteMap.Initial")) {
        	firstPoint = new Point2D.Double(x, y);
    		} else if (status.equals("ExecuteMap.First")) {
    			PluginServices.getMDIManager().setWaitCursor();
    			lastPoint = new Point2D.Double(x,y);

    			double w;
    			double h;
    			w = lastPoint.getX() - firstPoint.getX();
    			h = lastPoint.getY() - firstPoint.getY();

    			try {
    				getCadToolAdapter().getVectorialAdapter().startComplexRow();

    				for (int i = selection.nextSetBit(0); i >= 0;
    						i = selection.nextSetBit(i + 1)) {
    					DefaultFeature fea = (DefaultFeature)getCadToolAdapter().getVectorialAdapter().getRow(i).cloneRow();
    					UtilFunctions.rotateGeom(fea.getGeometry(), -Math.atan2(w, h) + (Math.PI / 2),
    						firstPoint.getX(), firstPoint.getY());
    					// fea.getGeometry().rotate(-Math.atan2(w, h) + (Math.PI / 2),
    					// 	firstPoint.getX(), firstPoint.getY());
    					getCadToolAdapter().getVectorialAdapter().modifyRow(i, fea);
    				}

    				getCadToolAdapter().getVectorialAdapter().endComplexRow();
    			} catch (DriverIOException e) {
    				e.printStackTrace();
    			} catch (IOException e1) {
    				e1.printStackTrace();
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
        ScaleCADToolState actualState = ((ScaleCADToolContext) _fsm).getState();
        String status = actualState.getName();
        VectorialEditableAdapter vea = getCadToolAdapter().getVectorialAdapter();
        FBitSet selection = vea.getSelection();
        Point2D currentPoint = new Point2D.Double(x, y);


        if (status.equals("ExecuteMap.First")) {
        	try {
				for (int i = 0; i < vea.getRowCount(); i++) {
					if (selection.get(i)) {
						IGeometry geometry = vea.getShape(i);
						double size=getCadToolAdapter().getMapControl().getViewPort().toMapDistance(getCadToolAdapter().getMapControl().getWidth());
						UtilFunctions.scaleGeom(geometry, firstPoint, 
								firstPoint.distance(currentPoint)/(size/40),
								firstPoint.distance(currentPoint)/(size/40));
						geometry.draw((Graphics2D) g,
							getCadToolAdapter().getMapControl().getViewPort(),
							CADTool.modifySymbol);
						drawLine((Graphics2D) g, firstPoint, currentPoint);
						PluginServices.getMainFrame().getStatusBar().setMessage("5","Factor = "+firstPoint.distance(currentPoint)/(size/40));
					}
				}
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (status.equals("ExecuteMap.Fiveth")) {
			try {
				for (int i = 0; i < vea.getRowCount(); i++) {
					if (selection.get(i)) {
						IGeometry geometry = vea.getShape(i);


						double distrr = orr.distance(frr);
						double distre = ore.distance(currentPoint);
						double escalado = distre / distrr;

						UtilFunctions.scaleGeom(geometry, scalePoint, escalado, escalado);
						// geometry.scale(scalePoint, escalado, escalado);
						geometry.draw((Graphics2D) g,
							getCadToolAdapter().getMapControl().getViewPort(),
							CADTool.modifySymbol);
						drawLine((Graphics2D) g, firstPoint,
							new Point2D.Double(x, y));
					}
				}
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }

    /**
     * Add a diferent option.
     *
     * @param s Diferent option.
     */
    public void addOption(String s) {
    	ScaleCADToolState actualState = (ScaleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
       if (status.equals("ExecuteMap.First")) {
			try {
				scale(2);
			} catch (DriverIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    	ScaleCADToolState actualState = (ScaleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        if (status.equals("ExecuteMap.First")) {
    			try {
    				scale(d);
    			} catch (DriverIOException e) {
    				e.printStackTrace();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}

    	}
    }
    private void scale(double scaleFactor) throws DriverIOException, IOException {
    		VectorialEditableAdapter vea=getCadToolAdapter().getVectorialAdapter();
    		vea.startComplexRow();
    		FBitSet selection=vea.getSelection();
    		for (int i = 0; i < vea.getRowCount(); i++) {
    			if (selection.get(i)) {
    				DefaultFeature df=vea.getRow(i).cloneRow();
    				UtilFunctions.scaleGeom(df.getGeometry(), scalePoint, scaleFactor, scaleFactor);
    				// df.getGeometry().scale(scalePoint, scaleFactor, scaleFactor);
    				vea.modifyRow(i, df);
    			}
    		}
    		vea.endComplexRow();
    	}
}
