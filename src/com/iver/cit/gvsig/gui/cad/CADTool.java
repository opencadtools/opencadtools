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
package com.iver.cit.gvsig.gui.cad;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.BitSet;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public interface CADTool {
	public static FSymbol drawingSymbol = new FSymbol(FConstant.SYMBOL_TYPE_POINT,
			Color.RED);
	public static FSymbol modifySymbol = new FSymbol(FConstant.SYMBOL_TYPE_POINT,
			Color.GRAY);
	public static FSymbol selectSymbol = new FSymbol(FConstant.SYMBOL_TYPE_POINT,
			Color.ORANGE);

	public void init();
	public void transition(java.util.BitSet sel, double x, double y);
	public void setVectorialAdapter(VectorialEditableAdapter vea);
	public void addpoint(BitSet sel,double x,double y);
	public void addoption(String s);
	public void setQuestion(String s);
	//public void updateState(String s);

	/**
	 * Recibe un graphics en el que se encuentra dibujada la
	 * EditableFeatureSource que se pasa como parámetro. En este método, la
	 * herramienta ha de implementar el dibujado de la operación que se está
	 * realizando dependiendo del estado. Por ejemplo al dibujar un círculo
	 * mediante 3 puntos, cuando la herramienta se encuentre en el estado en
	 * el que sólo falta un punto, se dibujará el círculo teniendo en cuenta
	 * como tercer punto el puntero del ratón (pasado en los parámetros x e
	 * y). Este método es invocado tras cada transición y cada vez que se
	 * mueve el ratón.
	 *
	 * @param g DOCUMENT ME!
	 * @param efs DOCUMENT ME!
	 * @param selectedGeometries DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	void drawOperation(Graphics g,FBitSet selectedGeometries, double x, double y);

	/**
	 * Obtiene la pregunta que saldrá en la consola relativa al estado en el
	 * que se encuentra la herramienta
	 *
	 * @return DOCUMENT ME!
	 */
	String getQuestion();

	/**
	 * DOCUMENT ME!
	 *
	 * @param cta DOCUMENT ME!
	 */
	public void setCadToolAdapter(CADToolAdapter cta);

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CADToolAdapter getCadToolAdapter();

	/**
	 * Devuelve el nombre de la herramienta cad.
	 *
	 * @return Nombre de la herramienta.
	 */
	public String getName();
}
