package com.iver.cit.gvsig.gui.cad.tools;

import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext;//.LineCADToolState;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext.LineCADToolState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.IOException;

import java.util.BitSet;


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
public class LineCADTool extends DefaultCADTool {
    private LineCADToolContext _fsm;
    private VectorialEditableAdapter vea;
    private String question;
    private Point2D firstPoint;
    private Point2D lastPoint;
    private double angle;
	private double length;
public LineCADTool(){
	_fsm=new LineCADToolContext(this);
}
    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {

    }
    public void transition(java.util.BitSet sel, double x, double y){
		_fsm.addpoint(sel,x,y);
	}
    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addpoint(java.util.BitSet sel, double x, double y) {
       // _fsm.addpoint(sel, x, y);

        LineCADToolState actualState = (LineCADToolState)_fsm.getPreviousState();
      //  CADToolState previousState=(CADToolState)_fsm.getPreviousState();
        String status = actualState.getName();
       // String previousstatus=previousState.getName();

        if (status.equals("ExecuteMap.Initial")) {
        	firstPoint=new Point2D.Double(x,y);
        	System.out.println("Question : "+question);
        } else if (status == "ExecuteMap.First") {
			firstPoint=new Point2D.Double(x,y);
			System.out.println("Question : "+question);

		} else if (status == "ExecuteMap.Second") {
			System.out.println("Question : "+question);
			//firstPoint=lastPoint;
			lastPoint=new Point2D.Double(x,y);
			try {
				GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
						2);
				elShape.moveTo(firstPoint.getX(), firstPoint.getY());
				elShape.lineTo(lastPoint.getX(), lastPoint.getY());
				DefaultFeature df=new DefaultFeature(ShapeFactory.createPolyline2D(elShape),null);
				vea.addRow(df);
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			firstPoint=lastPoint;
		}

    }

    /**
     * Devuelve la cadena que corresponde al estado en el que nos encontramos.
     *
     * @return Cadena para mostrar por consola.
     */
    public String getQuestion() {
    	System.out.println("Question : "+question);
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
     * Método para dibujar la lo necesario para el estado en el que nos encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometrías seleccionadas.
     * @param x parámetro x del punto que se pase para dibujar.
     * @param y parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
    	LineCADToolState actualState = _fsm.getState();
    	 String status = actualState.getName();

		//if ((status == "ExecuteMap.Second")){// || (status == "5")) {
			if (firstPoint!=null)
			drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y));
		//}
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
    public void addoption(String s) {
        // TODO Auto-generated method stub
    }
    public void refresh(){

    }
	public void setVectorialAdapter(VectorialEditableAdapter vea) {
	this.vea=vea;

	}
}
