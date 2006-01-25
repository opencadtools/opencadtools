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

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.ArcCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.ArcCADToolContext.ArcCADToolState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ArcCADTool extends DefaultCADTool {
    private ArcCADToolContext _fsm;
    private Point2D p1;
    private Point2D p2;
    private Point2D p3;

    /**
     * Crea un nuevo LineCADTool.
     */
    public ArcCADTool() {
        _fsm = new ArcCADToolContext(this);
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
        _fsm = new ArcCADToolContext(this);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(FBitSet sel, double x, double y) {
        _fsm.addPoint(sel, x, y);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
     */
    public void transition(FBitSet sel, double d) {
        //_fsm.addValue(sel,d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(FBitSet sel, String s) {
        //_fsm.addOption(sel,s);
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como pará metro el
     * editableFeatureSource que ya estará creado.
     *
     * @param sel Bitset con las geometrías que estén seleccionadas.
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(FBitSet sel, double x, double y) {
        ArcCADToolState actualState = (ArcCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.Initial")) {
            p1 = new Point2D.Double(x, y);
        } else if (status.equals("ExecuteMap.First")) {
            p2 = new Point2D.Double(x, y);
        } else if (status.equals("ExecuteMap.Second")) {
            p3 = new Point2D.Double(x, y);

            IGeometry ig = ShapeFactory.createArc(p1, p2, p3);

            if (ig != null) {
                addGeometry(ig);
            }
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
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
        ArcCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if (status.equals("ExecuteMap.First")) {
            drawLine((Graphics2D) g, p1, new Point2D.Double(x, y));
        } else if (status.equals("ExecuteMap.Second")) {
            Point2D current = new Point2D.Double(x, y);
            IGeometry ig = ShapeFactory.createArc(p1, p2, current);

            if (ig != null) {
                ig.draw((Graphics2D) g,
                    getCadToolAdapter().getMapControl().getViewPort(),
                    CADTool.modifySymbol);
            }

            Point2D p = getCadToolAdapter().getMapControl().getViewPort()
                            .fromMapPoint(p1.getX(), p1.getY());
            g.drawRect((int) p.getX(), (int) p.getY(), 1, 1);
            p = getCadToolAdapter().getMapControl().getViewPort().fromMapPoint(p2.getX(),
                    p2.getY());
            g.drawRect((int) p.getX(), (int) p.getY(), 1, 1);
        }
    }

    /**
     * Add a diferent option.
     *
     * @param sel DOCUMENT ME!
     * @param s Diferent option.
     */
    public void addOption(FBitSet sel, String s) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(FBitSet sel, double d) {
    }
}
