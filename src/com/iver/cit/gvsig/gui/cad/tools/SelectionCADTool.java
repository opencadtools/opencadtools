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

import com.iver.andami.PluginServices;

import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext.SelectionCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class SelectionCADTool extends DefaultCADTool {
	public final static int tolerance = 4;

	private SelectionCADToolContext _fsm;

	private Point2D firstPoint;

	private Point2D lastPoint;

	private String tool = "selection";

	private String nextState;

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
		VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
				.getEditionManager().getActiveLayerEdited();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		// ArrayList selectedRowIndex = vle.getSelectedRowIndex();

		if (status.equals("Selection.FirstPoint")) {
		} else if (status.equals("Selection.SecondPoint")) {
			// selectByRectangle(x, y, selectedRow);

		} else if (status.equals("Selection.WithHandlers")) {
			for (int i = 0; i < selectedRow.size(); i++) {
				IRowEdited row = (IRowEdited) selectedRow.get(i);
				// int index = ((Integer) selectedRowIndex.get(i)).intValue();

				// Movemos los handlers que hemos seleccionado
				// previamente dentro del método select()
				for (int k = 0; k < selectedHandler.size(); k++) {
					Handler h = (Handler) selectedHandler.get(k);
					h.set(x, y);
				}

				modifyFeature(row.getIndex(), (IFeature) row.getLinkedRow());
			}
		}
	}

	/**
	 * Receives second point
	 * @param x
	 * @param y
	 * @return numFeatures selected
	 */
	public int selectWithSecondPoint(double x, double y) {
		VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
			.getEditionManager().getActiveLayerEdited();
		ArrayList selectedRow = vle.getSelectedRow();

		lastPoint = new Point2D.Double(x, y);
		FBitSet selection = getCadToolAdapter().getVectorialAdapter()
			.getSelection();
		selection.clear();
		selectedRow.clear();

		double x1;
		double y1;
		double w1;
		double h1;

		if (firstPoint.getX() < lastPoint.getX()) {
			x1 = firstPoint.getX();
			w1 = lastPoint.getX() - firstPoint.getX();
		} else {
			x1 = lastPoint.getX();
			w1 = firstPoint.getX() - lastPoint.getX();
		}

		if (firstPoint.getY() < lastPoint.getY()) {
			y1 = firstPoint.getY();
			h1 = lastPoint.getY() - firstPoint.getY();
		} else {
			y1 = lastPoint.getY();
			h1 = firstPoint.getY() - lastPoint.getY();
		}

		Rectangle2D rect = new Rectangle2D.Double(x1, y1, w1, h1);

		VectorialEditableAdapter vea = getCadToolAdapter()
				.getVectorialAdapter();
		String strEPSG = getCadToolAdapter().getMapControl().getViewPort()
				.getProjection().getAbrev().substring(5);
		IRowEdited[] feats;
		try {
			feats = vea.getFeatures(rect, strEPSG);

			for (int i = 0; i < feats.length; i++) {
				IGeometry geom = ((IFeature) feats[i].getLinkedRow())
						.getGeometry();

				if (firstPoint.getX() < lastPoint.getX()) {
					if (rect.contains(geom.getBounds2D())) {
						selectedRow.add(feats[i]);
						selection.set(feats[i].getIndex(), true);
					}
				} else {
					if (geom.intersects(rect)) { // , 0.1)){
						selectedRow.add(feats[i]);
						selection.set(feats[i].getIndex(), true);
					}
				}
			}
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
				.getEditionManager().getActiveLayerEdited();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		// ArrayList selectedRowIndex = vle.getSelectedRowIndex();

		/*
		 * if (selection.cardinality() == 0) { selectedRow.clear();
		 * selectedRowIndex.clear(); selectedHandler.clear(); }
		 */

		drawHandlers(g, selectedRow, getCadToolAdapter().getMapControl()
				.getViewPort().getAffineTransform());

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
					getCadToolAdapter().getMapControl().getViewPort(),
					CADTool.selectSymbol);
		} else if (status.equals("Selection.WithHandlers")) {
			// Movemos los handlers que hemos seleccionado
			// previamente dentro del método select()
			for (int k = 0; k < selectedHandler.size(); k++) {
				Handler h = (Handler) selectedHandler.get(k);
				h.set(x, y);
			}

			// Y una vez movidos los vértices (handles)
			// redibujamos la nueva geometría.
			for (int i = 0; i < selectedRow.size(); i++) {
				IRowEdited rowEd = (IRowEdited) selectedRow.get(i);
				IGeometry geom = ((IFeature) rowEd.getLinkedRow())
						.getGeometry().cloneGeometry();
				g.setColor(Color.gray);
				geom.draw((Graphics2D) g, getCadToolAdapter().getMapControl()
						.getViewPort(), CADTool.modifySymbol);
			}
		}
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
		// TODO Auto-generated method stub
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
		return "SELECCION";
	}

	public boolean selectFeatures(double x, double y) {
		SelectionCADToolState actualState = (SelectionCADToolState) _fsm
				.getState();

		String status = actualState.getName();
		VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
				.getEditionManager().getActiveLayerEdited();
		ArrayList selectedRow = vle.getSelectedRow();

		if ((status.equals("Selection.FirstPoint"))
				|| (status.equals("Selection.WithSelectedFeatures"))) {
			firstPoint = new Point2D.Double(x, y);

			FBitSet selection = getCadToolAdapter().getVectorialAdapter()
				.getSelection();
			ArrayList selectedHandler = vle.getSelectedHandler();
			selectedRow.clear();
			selectedHandler.clear();

			// ArrayList selectedRowIndex = vle.getSelectedRowIndex();

			// Se comprueba si se pincha en una gemometría
			PluginServices.getMDIManager().setWaitCursor();

			double tam = getCadToolAdapter().getMapControl().getViewPort()
					.toMapDistance(tolerance);
			Rectangle2D rect = new Rectangle2D.Double(firstPoint.getX() - tam,
					firstPoint.getY() - tam, tam * 2, tam * 2);
			VectorialEditableAdapter vea = getCadToolAdapter()
					.getVectorialAdapter();
			String strEPSG = getCadToolAdapter().getMapControl().getViewPort()
					.getProjection().getAbrev().substring(5);
			IRowEdited[] feats;

			try {
				feats = vea.getFeatures(rect, strEPSG);
				selection.clear();

				for (int i = 0; i < feats.length; i++) {
					IFeature feat = (IFeature) feats[i].getLinkedRow();
					IGeometry geom = feat.getGeometry();

					if (geom.intersects(rect)) { // , 0.1)){
						selection.set(feats[i].getIndex(), true);
						selectedRow.add(feats[i]);
					}
				}
			} catch (DriverException e1) {
				e1.printStackTrace();
			}
			PluginServices.getMDIManager().restoreCursor();
		}
		if (selectedRow.size() > 0) {
			nextState = "Selection.WithSelectedFeatures";
			return true;
		} else {
			// if (nextState == null)
			{
				nextState = "Selection.SecondPoint";
				return true;
			}
		}
		/* if (nextState.equals("Selection.SecondPoint"))
			nextState = "Selection.FirstPoint";
		else
			nextState = "Selection.SecondPoint";

		return true; // Truco de Vicente */
	}

	public int selectHandlers(double x, double y) {
		// firstPoint = new Point2D.Double(x, y);

		Point2D auxPoint = new Point2D.Double(x, y);

		VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
				.getEditionManager().getActiveLayerEdited();
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
