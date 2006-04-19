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
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.core.v02.FGraphicUtilities;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.tokenmarker.ConsoleToken;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.utiles.console.JConsole;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public abstract class DefaultCADTool implements CADTool {
	private CADToolAdapter cadToolAdapter;

	private String question;

	private String[] currentdescriptions;

	private String tool = "selection";

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
	 * @param cta
	 *            DOCUMENT ME!
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
	public VectorialLayerEdited getVLE(){
		return (VectorialLayerEdited) CADExtension.getEditionManager().getActiveLayerEdited();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g
	 *            DOCUMENT ME!
	 * @param firstPoint
	 *            DOCUMENT ME!
	 * @param endPoint
	 *            DOCUMENT ME!
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
	 * @param geometry
	 *            DOCUMENT ME!
	 */
	public void addGeometry(IGeometry geometry) {
		VectorialEditableAdapter vea = getCadToolAdapter()
				.getVectorialAdapter();
		try {
			if (vea.getShapeType() == FShape.POLYGON) {
				GeneralPathX gp = new GeneralPathX();
				gp.append(geometry.getPathIterator(null), true);
				FConverter.getExteriorPolygon(gp);

				geometry = ShapeFactory.createPolygon2D(gp);
			}
			int numAttr = getCadToolAdapter().getVectorialAdapter()
					.getRecordset().getFieldCount();
			Value[] values = new Value[numAttr];
			for (int i = 0; i < numAttr; i++) {
				values[i] = ValueFactory.createNullValue();
			}
			DefaultFeature df = new DefaultFeature(geometry, values);
			int index = vea.addRow(df, getName());


			VectorialLayerEdited vle = getVLE();
			ArrayList selectedHandler = vle.getSelectedHandler();
			ArrayList selectedRow = vle.getSelectedRow();
			selectedHandler.clear();
			selectedRow.clear();

			ViewPort vp=vle.getLayer().getFMap().getViewPort();
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			selectedRow.add(new DefaultRowEdited(df, IRowEdited.STATUS_ADDED, index));
			IGeometry geom=df.getGeometry();
			geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
			vle.drawHandlers(geom.cloneGeometry(),gs,vp);
			vea.setSelectionImage(selectionImage);
		} catch (DriverIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		}

		draw(geometry.cloneGeometry());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param geometry
	 *            DOCUMENT ME!
	 */
	public void modifyFeature(int index, IFeature row) {
		try {
			getCadToolAdapter().getVectorialAdapter().modifyRow(index, row,
					getName());
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (DriverIOException e1) {
			e1.printStackTrace();
		}
		draw(row.getGeometry().cloneGeometry());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param geometry
	 *            DOCUMENT ME!
	 * @param values
	 *            DOCUMENT ME!
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
	 * @param s
	 *            Cadena que aparecerá en consola.
	 */
	public void setQuestion(String s) {
		question = s;
		//ConsoleToken.addQuestion(s);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void refresh() {
		getCadToolAdapter().getMapControl().drawMap(false);
	}

	/*public void drawHandlers(Graphics g, FBitSet sel, AffineTransform at)
			throws DriverIOException {
		for (int i = sel.nextSetBit(0); i >= 0; i = sel.nextSetBit(i + 1)) {
			IGeometry ig = getCadToolAdapter().getVectorialAdapter()
					.getShape(i).cloneGeometry();
			if (ig == null)
				continue;
			Handler[] handlers = ig.getHandlers(IGeometry.SELECTHANDLER);
			FGraphicUtilities.DrawHandlers((Graphics2D) g, at, handlers);
		}
	}
*/
	public void drawHandlers(Graphics g, ArrayList selectedRows,
			AffineTransform at) {
		for (int i = 0; i < selectedRows.size(); i++) {
			IRowEdited edRow = (IRowEdited) selectedRows.get(i);
			IFeature feat = (IFeature) edRow.getLinkedRow();
			// IFeature feat = (IFeature) selectedRows.get(i);
			IGeometry ig = feat.getGeometry().cloneGeometry();
			if (ig == null)
				continue;
			Handler[] handlers = ig.getHandlers(IGeometry.SELECTHANDLER);
			FGraphicUtilities.DrawHandlers((Graphics2D) g, at, handlers);
		}
	}

	public void setDescription(String[] currentdescriptions) {
		this.currentdescriptions = currentdescriptions;
	}

	public String[] getDescriptions() {
		return currentdescriptions;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#end()
	 */
	public void end() {
		CADExtension.setCADTool("selection",true);
		PluginServices.getMainFrame().setSelectedTool("SELCAD");
	}

	public void init() {
		CADTool.drawingSymbol.setOutlined(true);
		CADTool.drawingSymbol.setOutlineColor(Color.GREEN);

	}
	protected ArrayList getSelectedRows(){
		VectorialLayerEdited vle = getVLE();
    	ArrayList selectedRow = vle.getSelectedRow();
    	return selectedRow;
	}
	protected ArrayList getSelectedHandlers(){
		VectorialLayerEdited vle = getVLE();
    	ArrayList selectedHandlers = vle.getSelectedHandler();
    	return selectedHandlers;
	}
	public void clearSelection(){
		VectorialLayerEdited vle = getVLE();
		ArrayList selectedRow = vle.getSelectedRow();
    	ArrayList selectedHandlers = vle.getSelectedHandler();
    	selectedRow.clear();
    	selectedHandlers.clear();
    	VectorialEditableAdapter vea=vle.getVEA();
    	FBitSet selection = vea.getSelection();
    	selection.clear();
    	vea.setSelectionImage(null);
    	vea.setHandlersImage(null);

	}
	public String getNextTool() {
		return tool;
	}

	public void setNextTool(String tool) {
		this.tool = tool;
	}
	public boolean changeCommand(String name){
		CADTool[] cadtools=CADExtension.getCADTools();
		for (int i=0;i<cadtools.length;i++){
			CADTool ct=cadtools[i];
			if (name.equalsIgnoreCase(ct.getName())|| name.equalsIgnoreCase(ct.toString())){
				getCadToolAdapter().setCadTool(ct);
				ct.init();
				View vista = (View) PluginServices.getMDIManager().getActiveView();
				vista.getConsolePanel().addText("\n" + ct.getName(),JConsole.COMMAND);
				return true;
			}
		}
		return false;
	}
	public abstract String toString();
}
