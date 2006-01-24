package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.IOException;

import statemap.State;
import statemap.TransitionUndefinedException;

import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.CircleCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.CircleCADToolContext.CircleCADToolState;


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
public class CircleCADTool extends DefaultCADTool {
    private CircleCADToolContext _fsm;
    private VectorialEditableAdapter vea;
    private String question;
    private Point2D center;
    private Point2D firstPoint;
    private Point2D secondPoint;
    private Point2D thirdPoint;

    /**
     * Crea un nuevo LineCADTool.
     */
    public CircleCADTool() {
        _fsm = new CircleCADToolContext(this);
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#end()
     */
    public void end() {
    	 _fsm = new CircleCADToolContext(this);
    	 firstPoint=null;
    }

    /* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
	 */
    public void transition(FBitSet sel, double x, double y) throws TransitionUndefinedException {
     	_fsm.addpoint(sel, x, y);
    }
    /* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
	 */
	public void transition(FBitSet sel, double d) throws TransitionUndefinedException {
		_fsm.addvalue(sel,d);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
	 */
	public void transition(FBitSet sel, String s) throws TransitionUndefinedException{
		_fsm.addoption(sel,s);
	}
    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addpoint(FBitSet sel, double x, double y) {
        CircleCADToolState actualState = (CircleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        if (status.equals("ExecuteMap.Initial")) {
        	center = new Point2D.Double(x, y);
		} else if (status == "ExecuteMap.First") {
			 try {
	              DefaultFeature df = new DefaultFeature(ShapeFactory.createCircle(center,
							new Point2D.Double(x,y)), null);
	                vea.addRow(df);
	            } catch (DriverIOException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
		} else if (status == "ExecuteMap.Seventh") {
			firstPoint= new Point2D.Double(x, y);
		} else if (status == "ExecuteMap.Second") {
			secondPoint= new Point2D.Double(x, y);
		} else if (status == "ExecuteMap.Third") {
			thirdPoint= new Point2D.Double(x, y);
			 try {
	               DefaultFeature df = new DefaultFeature(ShapeFactory.createCircle(firstPoint,secondPoint,thirdPoint), null);
	                vea.addRow(df);
	            } catch (DriverIOException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
		} else if (status == "ExecuteMap.Sixth") {
			try {
	               DefaultFeature df = new DefaultFeature(ShapeFactory.createCircle(center,new Point2D.Double(x, y)), null);
	                vea.addRow(df);
	            } catch (DriverIOException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
		}
  }

    /**
     * Devuelve la cadena que corresponde al estado en el que nos encontramos.
     *
     * @return Cadena para mostrar por consola.
     */
    public String getQuestion() {
        System.out.println("Question : " + question);

        return question;
    }

    /**
     * Devuelve el nombre de la clase en la que nos encontramos.
     *
     * @return Nombre de la clase en la que nos encontramos.
     */
    public String getName() {
        return this.getClass().getName();
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
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
        CircleCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if ((status == "ExecuteMap.Initial")) { // || (status == "5")) {

            if (firstPoint != null) {
                drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y));
            }
        }
        if (status == "ExecuteMap.First") {
			Point2D currentPoint = new Point2D.Double(x, y);
			ShapeFactory.createCircle(center,currentPoint).draw((Graphics2D) g,
				getCadToolAdapter().getMapControl().getViewPort(),
				CADTool.modifySymbol);
		} else if (status == "ExecuteMap.Second") {
			drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y));
		} else if (status == "ExecuteMap.Third") {
			Point2D currentPoint = new Point2D.Double(x, y);
			//System.out.println("p2 = "+p2);
			//System.out.println("currentPoint = "+currentPoint);
			IGeometry geom=ShapeFactory.createCircle(firstPoint,secondPoint,currentPoint);
			if (geom!=null){
			geom.draw((Graphics2D) g,
					getCadToolAdapter().getMapControl().getViewPort(),
					CADTool.modifySymbol);
			}
		}
    }

    /**
     * Actualiza la cadena que corresponde al estado actual.
     *
     * @param s Cadena que aparecerá en consola.
     */
    public void setQuestion(String s) {
        question = s;
    }

    /**
     * Add a diferent option.
     *
     * @param s Diferent option.
     */
    public void addoption(FBitSet sel,String s) {
    	CircleCADToolState actualState = (CircleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
    	if (status == "ExecuteMap.Initial") {
    		if (s.equals("3p")|| s.equals("3P")){
    			//Opción correcta.
    		}
    	}
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
    	getCadToolAdapter().getMapControl().drawMap(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param vea DOCUMENT ME!
     */
    public void setVectorialAdapter(VectorialEditableAdapter vea) {
        this.vea = vea;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addvalue(FBitSet sel,double d) {
        CircleCADToolState actualState = (CircleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status == "ExecuteMap.Fiveth") {
			try {
	               DefaultFeature df = new DefaultFeature(ShapeFactory.createCircle(center,d), null);
	                vea.addRow(df);
	            } catch (DriverIOException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
		}
    }


}
