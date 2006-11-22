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
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.SymmetryCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.SymmetryCADToolContext.SymmetryCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;


/**
 * Herramienta para crear una geometría simétrica a otra, con la posibilidad de
 * borrar la original.
 *
 * @author Vicente Caballero Navarro
 */
public class SymmetryCADTool extends DefaultCADTool {
    private SymmetryCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D secondPoint;

    /**
     * Crea un nuevo SymmetryCADTool.
     */
    public SymmetryCADTool() {
    }

    /**
     * Método de inicio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new SymmetryCADToolContext(this);
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
       ArrayList selectedRows=getSelectedRows();
        if (selectedRows.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "_symmetry");
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
        SymmetryCADToolState actualState = (SymmetryCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        if (status.equals("Symmetry.FirstPoint")) {
        	firstPoint = new Point2D.Double(x, y);
    	} else if (status.equals("Symmetry.SecondPoint")) {
    		secondPoint = new Point2D.Double(x,y);
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
        SymmetryCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if (status.equals("Symmetry.SecondPoint")) {
        	Point2D pAux=new Point2D.Double(x,y);
        	drawSymmetry(g,pAux);
		}else if (status.equals("Symmetry.CutOrCopy")){
			drawSymmetry(g,secondPoint);
		}
    }
private void drawSymmetry(Graphics g,Point2D pAux) {
	ArrayList selectedRow=getSelectedRows();
    VectorialLayerEdited vle=getVLE();
	ViewPort vp=vle.getLayer().getMapContext().getViewPort();

	GeneralPathX gpx=new GeneralPathX();
	gpx.moveTo(firstPoint.getX(),firstPoint.getY());
	gpx.lineTo(pAux.getX(),pAux.getY());
	ShapeFactory.createPolyline2D(gpx).draw((Graphics2D)g,vp,CADTool.modifySymbol);
	for (int i = 0; i < selectedRow.size(); i++) {
		DefaultRowEdited row=(DefaultRowEdited) selectedRow.get(i);
		DefaultFeature fea = (DefaultFeature) row.getLinkedRow();

		IGeometry geom = fea.getGeometry().cloneGeometry();
		Handler[] handlers = geom.getHandlers(IGeometry.SELECTHANDLER);

			for (int j = 0; j < handlers.length; j++) {
				Handler h = (Handler) handlers[j];
				Point2D p = h.getPoint();
				Point2D[] ps = UtilFunctions.getPerpendicular(firstPoint,
						pAux, p);
				Point2D inter = UtilFunctions.getIntersection(ps[0],
						ps[1], firstPoint, pAux);
				if (inter!=null) {
					Point2D dif = new Point2D.Double(inter.getX() -
							p.getX(), inter.getY() - p.getY());
					h.set(inter.getX() + dif.getX(),
							inter.getY() + dif.getY());
				}
			}
			geom.draw((Graphics2D)g,vp,CADTool.drawingSymbol);
	}
}

    /**
	 * Add a diferent option.
	 *
	 * @param s
	 *            Diferent option.
	 */
    public void addOption(String s) {
    	 SymmetryCADToolState actualState = (SymmetryCADToolState) _fsm
				.getPreviousState();
		String status = actualState.getName();
		ArrayList selectedRow = getSelectedRows();
		ArrayList selectedRowAux=new ArrayList();
		VectorialLayerEdited vle = getVLE();
		VectorialEditableAdapter vea = vle.getVEA();
		if (status.equals("Symmetry.CutOrCopy")) {
			PluginServices.getMDIManager().setWaitCursor();
			try {
				vea.startComplexRow();
				for (int i = 0; i < selectedRow.size(); i++) {
					DefaultRowEdited row = (DefaultRowEdited) selectedRow
							.get(i);
					DefaultFeature fea = (DefaultFeature) row.getLinkedRow()
							.cloneRow();

					IGeometry geom = fea.getGeometry();
					Handler[] handlers = geom
							.getHandlers(IGeometry.SELECTHANDLER);

					for (int j = 0; j < handlers.length; j++) {
						Handler h = (Handler) handlers[j];
						Point2D p = h.getPoint();
						Point2D[] ps = UtilFunctions.getPerpendicular(
								firstPoint, secondPoint, p);
						Point2D inter = UtilFunctions.getIntersection(ps[0],
								ps[1], firstPoint, secondPoint);
						Point2D dif = new Point2D.Double(inter.getX()
								- p.getX(), inter.getY() - p.getY());
						h.set(inter.getX() + dif.getX(), inter.getY()
								+ dif.getY());
					}

					if (s.equals(PluginServices.getText(this, "cut"))
							|| s.equals("s") || s.equals("S")) {
						vea.modifyRow(row.getIndex(), fea,
								getName(), EditionEvent.GRAPHIC);

						selectedRowAux.add(new DefaultRowEdited(fea,
								IRowEdited.STATUS_MODIFIED, row.getIndex()));
					} else {
						int index=addGeometry(geom,fea.getAttributes());
						selectedRowAux.add(new DefaultRowEdited(fea,
								IRowEdited.STATUS_ADDED, index));
					}

				}

				vea.endComplexRow(getName());
				vle.setSelectionCache(VectorialLayerEdited.SAVEPREVIOUS, selectedRowAux);
				//clearSelection();
				//selectedRow.addAll(selectedRowAux);
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			PluginServices.getMDIManager().restoreCursor();
		}
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
    public void addValue(double d) {

    }

	public String getName() {
		return PluginServices.getText(this,"symmetry_");
	}

	public String toString() {
		return "_symmetry";
	}

}
