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
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FGraphicUtilities;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.StretchCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.StretchCADToolContext.StretchCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * Herramienta para estirar los handlers que seleccionemos previamente.
 *
 * @author Vicente Caballero Navarro
 */
public class StretchCADTool extends DefaultCADTool {
    private StretchCADToolContext _fsm;
    private Point2D selfirstPoint;
	private Point2D sellastPoint;
	private Point2D movefirstPoint;
	private Point2D movelastPoint;
	private Rectangle2D rect = null;

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public StretchCADTool() {
    }

    /**
     * Método de incio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new StretchCADToolContext(this);
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
        ArrayList rowSelected=getSelectedRows();
        if (rowSelected.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "_stretch");
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
        StretchCADToolState actualState = (StretchCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Stretch.SelFirstPoint")) {
        	selfirstPoint = new Point2D.Double(x, y);
		} else if (status.equals("Stretch.SelLastPoint")) {
			sellastPoint = new Point2D.Double(x,y);

			double x1;
			double y1;
			double w1;
			double h1;

			if (selfirstPoint.getX() < sellastPoint.getX()) {
				x1 = selfirstPoint.getX();
				w1 = sellastPoint.getX() - selfirstPoint.getX();
			} else {
				x1 = sellastPoint.getX();
				w1 = selfirstPoint.getX() - sellastPoint.getX();
			}

			if (selfirstPoint.getY() < sellastPoint.getY()) {
				y1 = selfirstPoint.getY();
				h1 = sellastPoint.getY() - selfirstPoint.getY();
			} else {
				y1 = sellastPoint.getY();
				h1 = selfirstPoint.getY() - sellastPoint.getY();
			}

			rect = new Rectangle2D.Double(x1, y1, w1, h1);
		} else if (status.equals("Stretch.MoveFirstPoint")) {
			movefirstPoint = new Point2D.Double(x, y);
		} else if (status.equals("Stretch.MoveLastPoint")) {
			VectorialLayerEdited vle=getVLE();
	    	VectorialEditableAdapter vea=vle.getVEA();
	    	vea.startComplexRow();
	    	ArrayList selectedRow=getSelectedRows();
	    	ArrayList selectedRowAux=new ArrayList();
	    	PluginServices.getMDIManager().setWaitCursor();
			movelastPoint = new Point2D.Double(x, y);

			Handler[] handlers = null;

			//for (int i = selectedGeometries.nextSetBit(0); i >= 0;
			//		i = selectedGeometries.nextSetBit(i + 1)) {
			try {
			for (int i =0;i<selectedRow.size(); i++) {
				IRowEdited edRow = (IRowEdited) selectedRow.get(i);
	    		DefaultFeature fea = (DefaultFeature) edRow.getLinkedRow().cloneRow();
				IGeometry geometry = null;
				geometry = fea.getGeometry();

				handlers = geometry.getHandlers(IGeometry.STRETCHINGHANDLER);

				for (int j = 0; j < handlers.length; j++) {
					if (rect.contains(handlers[j].getPoint())) {
						handlers[j].move(movelastPoint.getX() -
							movefirstPoint.getX(),
							movelastPoint.getY() - movefirstPoint.getY());
					}
				}
				vea.modifyRow(edRow.getIndex(),fea,getName(),EditionEvent.GRAPHIC);
				selectedRowAux.add(new DefaultRowEdited(fea,IRowEdited.STATUS_MODIFIED,edRow.getIndex()));
			}
			vea.endComplexRow(getName());
	    	vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS, selectedRowAux);

			PluginServices.getMDIManager().restoreCursor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		StretchCADToolState actualState = ((StretchCADToolContext) _fsm).getState();
		String status = actualState.getName();
		ArrayList selectedRow = getSelectedRows();

		if (status.equals("Stretch.SelLastPoint")) {
			GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
					4);
			elShape.moveTo(selfirstPoint.getX(), selfirstPoint.getY());
			elShape.lineTo(x, selfirstPoint.getY());
			elShape.lineTo(x, y);
			elShape.lineTo(selfirstPoint.getX(), y);
			elShape.lineTo(selfirstPoint.getX(), selfirstPoint.getY());
			ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g,
				getCadToolAdapter().getMapControl().getViewPort(),
				modifySymbol);
		} else if (status.equals("Stretch.MoveFirstPoint")) {
			Handler[] handlers = null;
			for (int i = 0;i<selectedRow.size();i++) {
				IRowEdited edRow = (IRowEdited) selectedRow.get(i);
	    		DefaultFeature fea = (DefaultFeature) edRow.getLinkedRow().cloneRow();
				IGeometry geometry = null;
					geometry = fea.getGeometry();

				handlers = geometry.getHandlers(IGeometry.STRETCHINGHANDLER);

				for (int j = 0; j < handlers.length; j++) {
					if (rect.contains(handlers[j].getPoint())) {
						FGraphicUtilities.DrawHandlers((Graphics2D) g,getCadToolAdapter().getMapControl().getViewPort().getAffineTransform(),new Handler[] {handlers[j]});
					}
				}
			}
		} else if (status.equals("Stretch.MoveLastPoint")) {
			Handler[] handlers = null;

			for (int i = 0;i<selectedRow.size();i++) {
				IRowEdited edRow = (IRowEdited) selectedRow.get(i);
	    		DefaultFeature fea = (DefaultFeature) edRow.getLinkedRow().cloneRow();
				IGeometry geometry = null;
					geometry = fea.getGeometry();

				handlers = geometry.getHandlers(IGeometry.STRETCHINGHANDLER);

				for (int j = 0; j < handlers.length; j++) {
					if (rect.contains(handlers[j].getPoint())) {
						handlers[j].move(x - movefirstPoint.getX(),
							y - movefirstPoint.getY());
					}
				}
				geometry.draw((Graphics2D) g,
					getCadToolAdapter().getMapControl().getViewPort(),
					modifySymbol);
			}
		}
	}

    /**
	 * Add a diferent option.
	 *
	 * @param s
	 *            Diferent option.
	 */
    public void addOption(String s) {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    }


	public String getName() {
		return PluginServices.getText(this,"stretch_");
	}

	public String toString() {
		return "_stretch";
	}
	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.POINT:
		case FShape.MULTIPOINT:
			return false;
		}
		return true;
	}
}
