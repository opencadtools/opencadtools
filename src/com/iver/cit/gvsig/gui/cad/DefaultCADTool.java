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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FGraphicUtilities;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public abstract class DefaultCADTool implements CADTool {
    private CADToolAdapter cadToolAdapter;
    private String question;
    private String[] currentdescriptions;

    /**
     * DOCUMENT ME!
     */
    public void draw(IGeometry geometry) {
        if (geometry != null) {
            BufferedImage img = getCadToolAdapter().getMapControl().getImage();
            Graphics2D gImag = (Graphics2D) img.getGraphics();
            ViewPort vp = getCadToolAdapter().getMapControl().getViewPort();
            geometry.draw(gImag, vp, CADTool.drawingSymbol);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param cta DOCUMENT ME!
     */
    public void setCadToolAdapter(CADToolAdapter cta) {
        cadToolAdapter = cta;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public CADToolAdapter getCadToolAdapter() {
        return cadToolAdapter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param firstPoint DOCUMENT ME!
     * @param endPoint DOCUMENT ME!
     */
    public void drawLine(Graphics2D g, Point2D firstPoint, Point2D endPoint) {
        GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, 2);
        elShape.moveTo(firstPoint.getX(), firstPoint.getY());
        elShape.lineTo(endPoint.getX(), endPoint.getY());
        ShapeFactory.createPolyline2D(elShape).draw(g,
            getCadToolAdapter().getMapControl().getViewPort(),
            CADTool.drawingSymbol);
    }


    /**
     * DOCUMENT ME!
     *
     * @param geometry DOCUMENT ME!
     */
    public void addGeometry(IGeometry geometry) {
    	VectorialEditableAdapter vea = getCadToolAdapter().getVectorialAdapter(); 
        try {
        	if (vea.getShapeType() == FShape.POLYGON)
        	{
        		GeneralPathX gp = new GeneralPathX();
        		gp.append(geometry.getGeneralPathXIterator(), true);
        		geometry = ShapeFactory.createPolygon2D(gp);
        	}
        			
        	DefaultFeature df = new DefaultFeature(geometry, null);
            vea.addRow(df);
        } catch (DriverIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        draw(geometry.cloneGeometry());
    }
    /**
     * DOCUMENT ME!
     *
     * @param geometry DOCUMENT ME!
     */
    public void modifyFeature(int index,DefaultFeature row) {
    	try {
			getCadToolAdapter().getVectorialAdapter().modifyRow(index, row);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DriverIOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	draw(row.getGeometry().cloneGeometry());
    }
    /**
     * DOCUMENT ME!
     *
     * @param geometry DOCUMENT ME!
     * @param values DOCUMENT ME!
     */
    public void addGeometry(IGeometry geometry, Value[] values) {
    }

    /**
     * Devuelve la cadena que corresponde al estado en el que nos encontramos.
     *
     * @return Cadena para mostrar por consola.
     */
    public String getQuestion() {
        return question;
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
     * DOCUMENT ME!
     */
    public void refresh() {
        getCadToolAdapter().getMapControl().drawMap(false);
    }

	public void drawHandlers(Graphics g,FBitSet sel,AffineTransform at) throws DriverIOException{
		 for (int i = sel.nextSetBit(0); i >= 0;
         i = sel.nextSetBit(i + 1)) {
			IGeometry ig = getCadToolAdapter().getVectorialAdapter().getShape(i).cloneGeometry();
			if (ig == null) continue;
				Handler[] handlers=ig.getHandlers(IGeometry.SELECTHANDLER);
				FGraphicUtilities.DrawHandlers((Graphics2D)g,at,handlers);
		}
	}

	public void setDescription(String[] currentdescriptions) {
		this.currentdescriptions = currentdescriptions;
	}
	public String[] getDescriptions(){
		return currentdescriptions;
	}
	/* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#end()
     */
    public void end() {
    	CADExtension.setCADTool("selection");
    	PluginServices.getMainFrame().setSelectedTool("SELCAD");
    }

}
