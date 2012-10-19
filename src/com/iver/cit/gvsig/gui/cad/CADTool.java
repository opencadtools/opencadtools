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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * DOCUMENT ME!
 * 
 * @author gvSIG
 * @author Laboratorio de Bases de Datos. Universidad de A Coruña
 */
public interface CADTool {

    public static ISymbol drawingSymbol = SymbologyFactory
	    .createDefaultSymbolByShapeType(FShape.MULTI, new Color(200, 0, 0,
		    100));
    public static ISymbol modifySymbol = SymbologyFactory
	    .createDefaultSymbolByShapeType(FShape.MULTI, new Color(100, 100,
		    100, 100));
    public static ISymbol selectSymbol = SymbologyFactory
	    .createDefaultSymbolByShapeType(FShape.MULTI, new Color(0, 0, 200,
		    100));

    public static int TOPGEOMETRY = 2000;

    public void init();

    public void end();

    public void transition(InputEvent event);

    public void transition(double x, double y, InputEvent event);

    public void transition(double d);

    public void transition(String s) throws CommandException;

    public void addPoint(double x, double y, InputEvent event);

    public void addValue(double d);

    public void addOption(String s);

    public void setQuestion(String s);

    // Methods to know if the Tool need more than one transition such Spetial
    // Snappers, mouse clicks...
    public boolean isMultiTransition();

    /**
     * Recibe un graphics en el que se encuentra dibujada la
     * EditableFeatureSource que se pasa como parámetro. En este método, la
     * herramienta ha de implementar el dibujado de la operación que se está
     * realizando dependiendo del estado. Por ejemplo al dibujar un círculo
     * mediante 3 puntos, cuando la herramienta se encuentre en el estado en el
     * que sólo falta un punto, se dibujará el círculo teniendo en cuenta como
     * tercer punto el puntero del ratón (pasado en los parámetros x e y). Este
     * método es invocado tras cada transición y cada vez que se mueve el ratón.
     * 
     * @param g
     *            DOCUMENT ME!
     * @param efs
     *            DOCUMENT ME!
     * @param selectedGeometries
     *            DOCUMENT ME!
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     */
    void drawOperation(Graphics g, double x, double y);

    /**
     * En este método, la herramienta ha de implementar el dibujado de la
     * operación que se está realizando dependiendo del estado. Tendra en cuenta
     * la lista de puntos que nos pueden devolver los snappers
     * 
     * @param g
     *            Graphic to draw
     * @param pointList
     *            List of points retrieved by the snappers tools
     */
    public void drawOperation(Graphics g, ArrayList pointList);

    /**
     * Obtiene la pregunta que saldrá en la consola relativa al estado en el que
     * se encuentra la herramienta
     * 
     * @return DOCUMENT ME!
     */
    String getQuestion();

    /**
     * DOCUMENT ME!
     * 
     * @param cta
     *            DOCUMENT ME!
     */
    public void setCadToolAdapter(CADToolAdapter cta);

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public CADToolAdapter getCadToolAdapter();

    public String[] getDescriptions();

    public void setDescription(String[] descriptions);

    public String getName();

    public VectorialLayerEdited getVLE();

    void clearSelection() throws ReadDriverException;

    public boolean isApplicable(int shapeType);

    public void setPreviosTool(DefaultCADTool tool);

    public void restorePreviousTool();

    public void endTransition(double x, double y, MouseEvent e);

    public void clear();
}
