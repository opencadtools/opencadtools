package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;

import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.PointCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.LineCADToolContext.LineCADToolState;
import com.iver.cit.gvsig.gui.cad.tools.smc.PointCADToolContext.PointCADToolState;


/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
public class PointCADTool extends DefaultCADTool {
    private PointCADToolContext _fsm;
    private VectorialEditableAdapter vea;
    private String question;
    private IGeometry first;
    private double angle;
	private double length;
public PointCADTool(){
	_fsm=new PointCADToolContext(this);
}
    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    public void init() {

    }
    public void transition(FBitSet sel, double x, double y){
		_fsm.addpoint(sel,x,y);
	}
    /**
     * Equivale al transition del prototipo pero sin pasarle como par� metro el
     * editableFeatureSource que ya estar� creado.
     *
     * @param sel Bitset con las geometr�as que est�n seleccionadas.
     * @param x par�metro x del punto que se pase en esta transici�n.
     * @param y par�metro y del punto que se pase en esta transici�n.
     */
    public void addpoint(FBitSet sel, double x, double y) {
       // _fsm.addpoint(sel, x, y);

        PointCADToolState actualState = (PointCADToolState)_fsm.getPreviousState();
      //  CADToolState previousState=(CADToolState)_fsm.getPreviousState();
        String status = actualState.getName();
       // String previousstatus=previousState.getName();

        if (status.equals("ExecuteMap.Initial") ||
        	(status == "ExecuteMap.First"))
        {
			System.out.println("Question : "+question);
			first = ShapeFactory.createGeometry(new FPoint2D(x,y));
			DefaultFeature df=new DefaultFeature(ShapeFactory.createPoint2D(x,y),null);
			try {
				vea.addRow(df);
			} catch (DriverIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
     * M�todo para dibujar la lo necesario para el estado en el que nos encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometr�as seleccionadas.
     * @param x par�metro x del punto que se pase para dibujar.
     * @param y par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
    	PointCADToolState actualState = _fsm.getState();
    	 String status = actualState.getName();

    	 if ((status == "ExecuteMap.First")){// || (status == "5")) {
			first.draw((Graphics2D) g, getCadToolAdapter().
					getMapControl().getViewPort(), CADTool.drawingSymbol);
    	 }
	}

    /**
     * Actualiza la cadena que corresponde al estado actual.
     *
     * @param s Cadena que aparecer� en consola.
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
