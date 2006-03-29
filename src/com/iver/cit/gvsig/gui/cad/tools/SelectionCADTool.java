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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext.SelectionCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class SelectionCADTool extends DefaultCADTool {
	public final static int tolerance = 4;

	private SelectionCADToolContext _fsm;

	private Point2D firstPoint;

	//private Point2D lastPoint;

	private String tool = "selection";

	private String nextState;
// Registros de los que se ha sleccionado algún handler.
	private ArrayList rowselectedHandlers=new ArrayList();

	/**
	 * Crea un nuevo LineCADTool.
	 */
	public SelectionCADTool() {
	}

	/**
	 * Método de incio, para poner el código de todo lo que se requiera de una
	 * carga previa a la utilización de la herramienta.
	 */
	public void init() {
		_fsm = new SelectionCADToolContext(this);
		setNextTool("selection");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      double, double)
	 */
	public void transition(double x, double y, InputEvent event) {
		System.out.println("TRANSICION DESDE ESTADO " + _fsm.getState()
				+ " x= " + x + " y=" + y);
		_fsm.addPoint(x, y, event);
		System.out.println("ESTADO ACTUAL: " + getStatus());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      double)
	 */
	public void transition(double d) {
		// _fsm.addValue(sel,d);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      java.lang.String)
	 */
	public void transition(String s) {
		_fsm.addOption(s);
	}

	public String getNextState() {
		return nextState;
	}

	/**
	 * Equivale al transition del prototipo pero sin pasarle como pará metro el
	 * editableFeatureSource que ya estará creado.
	 *
	 * @param selection
	 *            Bitset con las geometrías que estén seleccionadas.
	 * @param x
	 *            parámetro x del punto que se pase en esta transición.
	 * @param y
	 *            parámetro y del punto que se pase en esta transición.
	 */
	public void addPoint(double x, double y, InputEvent event) {
		SelectionCADToolState actualState = (SelectionCADToolState) _fsm
				.getPreviousState();
		String status = actualState.getName();
		System.out.println("PREVIOUSSTATE =" + status); // + "ESTADO ACTUAL: " +
														// _fsm.getState());
		/*
		 * FBitSet selection = getCadToolAdapter().getVectorialAdapter()
		 * .getSelection();
		 */
		VectorialLayerEdited vle = getVLE();
		VectorialEditableAdapter vea=vle.getVEA();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		// ArrayList selectedRowIndex = vle.getSelectedRowIndex();
		System.out.println("STATUS ACTUAL = " + _fsm.getTransition());
		if (status.equals("Selection.FirstPoint")) {
		} else if (status.equals("Selection.SecondPoint")) {
			// selectByRectangle(x, y, selectedRow);
		} else if (status.equals("Selection.WithFeatures")) {


		} else if (status.equals("Selection.WithHandlers")) {
			vea.startComplexRow();
			for (int i = 0; i < selectedRow.size(); i++) {
				IRowEdited row = (IRowEdited) selectedRow.get(i);
				// int index = ((Integer) selectedRowIndex.get(i)).intValue();

				// Movemos los handlers que hemos seleccionado
				// previamente dentro del método select()
				for (int k = 0; k < selectedHandler.size(); k++) {
					Handler h = (Handler) selectedHandler.get(k);
					h.set(x, y);
				}

				modifyFeature(row.getIndex(), (IFeature) row.getLinkedRow().cloneRow());
			}
			try {
				vea.endComplexRow();
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
	 * Receives second point
	 * @param x
	 * @param y
	 * @return numFeatures selected
	 */
	public int selectWithSecondPoint(double x, double y, InputEvent event) {
		VectorialLayerEdited vle = getVLE();
		PluginServices.getMDIManager().setWaitCursor();
		vle.selectWithSecondPoint(x,y);
		ArrayList selectedRow = vle.getSelectedRow();
		PluginServices.getMDIManager().restoreCursor();
		if (selectedRow.size() > 0) {
			nextState = "Selection.WithSelectedFeatures";
		} else
			nextState = "Selection.FirstPoint";
		return selectedRow.size();
	}

	/**
	 * Método para dibujar la lo necesario para el estado en el que nos
	 * encontremos.
	 *
	 * @param g
	 *            Graphics sobre el que dibujar.
	 * @param selectedGeometries
	 *            BitSet con las geometrías seleccionadas.
	 * @param x
	 *            parámetro x del punto que se pase para dibujar.
	 * @param y
	 *            parámetro x del punto que se pase para dibujar.
	 */
	public void drawOperation(Graphics g, double x, double y) {
		SelectionCADToolState actualState = _fsm.getState();
		String status = actualState.getName();
		/*
		 * FBitSet selection = getCadToolAdapter().getVectorialAdapter()
		 * .getSelection();
		 */
		VectorialLayerEdited vle = getVLE();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		ViewPort vp=vle.getLayer().getFMap().getViewPort();
		// ArrayList selectedRowIndex = vle.getSelectedRowIndex();

		/*
		 * if (selection.cardinality() == 0) { selectedRow.clear();
		 * selectedRowIndex.clear(); selectedHandler.clear(); }
		 */

		//drawHandlers(g, selectedRow, getCadToolAdapter().getMapControl()
		//		.getViewPort().getAffineTransform());

		if (status.equals("Selection.SecondPoint")) {
			// Dibuja el rectángulo de selección
			GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
					4);
			elShape.moveTo(firstPoint.getX(), firstPoint.getY());
			elShape.lineTo(x, firstPoint.getY());
			elShape.lineTo(x, y);
			elShape.lineTo(firstPoint.getX(), y);
			elShape.lineTo(firstPoint.getX(), firstPoint.getY());
			ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g,
					vp,
					CADTool.selectSymbol);
			Image img = vle.getSelectionImage();
	        g.drawImage(img, 0, 0, null);
	        return;
		} else if (status.equals("Selection.WithHandlers")) {
			// Movemos los handlers que hemos seleccionado
			// previamente dentro del método select()
			for (int k = 0; k < selectedHandler.size(); k++) {
				Handler h = (Handler) selectedHandler.get(k);
				h.set(x, y);
			}

			// Y una vez movidos los vértices (handles)
			// redibujamos la nueva geometría.
			for (int i = 0; i < rowselectedHandlers.size(); i++) {
				IRowEdited rowEd = (IRowEdited) rowselectedHandlers.get(i);
				IGeometry geom = ((IFeature) rowEd.getLinkedRow())
						.getGeometry().cloneGeometry();
				g.setColor(Color.gray);
				geom.draw((Graphics2D) g, vp, CADTool.modifySymbol);
			}
			return;
		}else{
			try{
			Image imgSel = vle.getSelectionImage();
	        if (imgSel!=null)
			g.drawImage(imgSel, 0, 0, null);
	        Image imgHand = vle.getHandlersImage();
	        if (imgHand!=null)
			g.drawImage(imgHand, 0, 0, null);
			}catch (Exception e) {
			}
		}
//		if (firstPoint != null) {
//			int dx = vp.fromMapDistance(x - firstPoint.getX());
//			int dy = -vp.fromMapDistance(y - firstPoint.getY());
//			Image img = vle.getImage();
//			g.drawImage(img, 0, 0, null);
//		}
	}

	/**
	 * Add a diferent option.
	 *
	 * @param sel
	 *            DOCUMENT ME!
	 * @param s
	 *            Diferent option.
	 */
	public void addOption(String s) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
	public void addValue(double d) {
	}

	public String getStatus() {
		try {
			SelectionCADToolState actualState = (SelectionCADToolState) _fsm
					.getPreviousState();
			String status = actualState.getName();

			return status;
		} catch (NullPointerException e) {
			return "ExecuteMap.Initial";
		}
	}

	public String getTool() {
		return tool;
	}

	public void setNextTool(String tool) {
		this.tool = tool;
	}

	public void end() {
		if (!tool.equals("selection"))
			CADExtension.setCADTool(getTool());
	}

	public String getName() {
		return PluginServices.getText(this,"selection_");
	}

	public boolean selectFeatures(double x, double y, InputEvent event) {
		SelectionCADToolState actualState = (SelectionCADToolState) _fsm
				.getState();

		String status = actualState.getName();
		VectorialLayerEdited vle = getVLE();


		if ((status.equals("Selection.FirstPoint"))
				|| (status.equals("Selection.WithSelectedFeatures"))) {
			PluginServices.getMDIManager().setWaitCursor();
			firstPoint = new Point2D.Double(x, y);
			vle.selectWithPoint(x,y);
			PluginServices.getMDIManager().restoreCursor();
		}
		ArrayList selectedRow = vle.getSelectedRow();
		if (selectedRow.size() > 0) {
			nextState = "Selection.WithSelectedFeatures";
			return true;
		} else {
			{
				nextState = "Selection.SecondPoint";
				return true;
			}
		}
	}

	public int selectHandlers(double x, double y, InputEvent event) {
		Point2D auxPoint = new Point2D.Double(x, y);

		VectorialLayerEdited vle = getVLE();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		System.out.println("DENTRO DE selectHandlers. selectedRow.size= "
				+ selectedRow.size());
		selectedHandler.clear();

		// Se comprueba si se pincha en una gemometría
		PluginServices.getMDIManager().setWaitCursor();

		double tam = getCadToolAdapter().getMapControl().getViewPort()
				.toMapDistance(tolerance);

		Handler[] handlers = null;
		rowselectedHandlers.clear();
		for (int i = 0; i < selectedRow.size(); i++) {
			IRowEdited rowEd = (IRowEdited) selectedRow.get(i);

			IFeature fea = (IFeature) rowEd.getLinkedRow();
			handlers = fea.getGeometry().getHandlers(IGeometry.SELECTHANDLER);
			// y miramos los handlers de cada entidad seleccionada
			double min = tam;
			// int hSel = -1;

			for (int j = 0; j < handlers.length; j++) {
				Point2D handlerPoint = handlers[j].getPoint();
				double distance = auxPoint.distance(handlerPoint);
				if (distance <= min) {
					min = distance;
					// hSel = j;
					selectedHandler.add(handlers[j]);
					rowselectedHandlers.add(rowEd);
				}
			}
		}
		PluginServices.getMDIManager().restoreCursor();

		int numHandlesSelected = selectedHandler.size();

		/*
		 * if (numHandlesSelected == 0) selectFeatures(x,y);
		 */

		return numHandlesSelected;
	}
}
