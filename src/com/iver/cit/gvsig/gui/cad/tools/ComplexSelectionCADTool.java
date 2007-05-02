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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.ComplexSelectionCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.ComplexSelectionCADToolContext.ComplexSelectionCADToolState;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ComplexSelectionCADTool extends SelectionCADTool {
	//public final static int tolerance = 4;

	private ComplexSelectionCADToolContext _fsm;

	//private Point2D firstPoint;

	//private Point2D lastPoint;



	//private String nextState;
// Registros de los que se ha sleccionado algún handler.
	//private ArrayList rowselectedHandlers=new ArrayList();
	//private String type=PluginServices.getText(this,"inside_circle");
	//private ArrayList pointsPolygon=new ArrayList();
	/**
	 * Crea un nuevo ComplexSelectionCADTool.
	 */
	public ComplexSelectionCADTool() {
		type=PluginServices.getText(this,"inside_circle");
	}

	/**
	 * Método de incio, para poner el código de todo lo que se requiera de una
	 * carga previa a la utilización de la herramienta.
	 */
	public void init() {
		_fsm = new ComplexSelectionCADToolContext(this);
		setNextTool("complex_selection");

		setType(PluginServices.getText(this,"inside_circle"));
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
		if (event!=null && ((MouseEvent)event).getClickCount()==2){
			try {
				pointDoubleClick((MapControl)event.getComponent());
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
			return;
		}
		ComplexSelectionCADToolState actualState = (ComplexSelectionCADToolState) _fsm
				.getPreviousState();
		String status = actualState.getName();
		System.out.println("PREVIOUSSTATE =" + status); // + "ESTADO ACTUAL: " +
														// _fsm.getState());
		VectorialLayerEdited vle = getVLE();
		VectorialEditableAdapter vea=vle.getVEA();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ArrayList selectedRow = vle.getSelectedRow();
		System.out.println("STATUS ACTUAL = " + _fsm.getTransition());
		if (status.equals("Selection.FirstPoint")) {
			firstPoint=new Point2D.Double(x,y);
			pointsPolygon.add(firstPoint);
		} else if (status.equals("Selection.SecondPoint")) {
		} else if (status.equals("Selection.WithFeatures")) {
		} else if (status.equals("Selection.WithHandlers")) {
			vea.startComplexRow();
			ArrayList selectedRowsAux=new ArrayList();
			for (int i = 0; i < selectedRow.size(); i++) {
				IRowEdited row = (IRowEdited) selectedRow.get(i);
				IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
				// Movemos los handlers que hemos seleccionado
				// previamente dentro del método select()
				for (int k = 0; k < selectedHandler.size(); k++) {
					Handler h = (Handler) selectedHandler.get(k);
					h.set(x, y);
				}

				modifyFeature(row.getIndex(), feat);
				selectedRowsAux.add(new DefaultRowEdited(feat,IRowEdited.STATUS_MODIFIED,row.getIndex()));
			}
			firstPoint=new Point2D.Double(x,y);
			vle.setSelectionCache(VectorialLayerEdited.SAVEPREVIOUS, selectedRowsAux);
				String description=PluginServices.getText(this,"move_handlers");
				vea.endComplexRow(description);
		}else if (status.equals("Selection.NextPointPolygon")) {
			pointsPolygon.add(new Point2D.Double(x,y));
		}
	}

	/**
	 * Receives second point
	 * @param x
	 * @param y
	 * @return numFeatures selected
	 */
	public int selectWithSecondPointOutRectangle(double x, double y, InputEvent event) {
		Point2D lastPoint=new Point2D.Double(x,y);
		GeneralPathX gpx=new GeneralPathX();
		gpx.moveTo(firstPoint.getX(),firstPoint.getY());
		gpx.lineTo(lastPoint.getX(),firstPoint.getY());
		gpx.lineTo(lastPoint.getX(),lastPoint.getY());
		gpx.lineTo(firstPoint.getX(),lastPoint.getY());
		gpx.closePath();
		IGeometry rectangle=ShapeFactory.createPolygon2D(gpx);
		return selectWithPolygon(rectangle);
	}
	/**
	 * Receives second point
	 * @param x
	 * @param y
	 * @return numFeatures selected
	 */
	public int selectWithCircle(double x, double y, InputEvent event) {
		IGeometry circle=ShapeFactory.createCircle(firstPoint,new Point2D.Double(x,y));
		return selectWithPolygon(circle);
	}
	public int selectWithPolygon(IGeometry polygon) {
		VectorialLayerEdited vle = getVLE();
		PluginServices.getMDIManager().setWaitCursor();

		if (getType().equals(PluginServices.getText(this,"inside_circle")) || getType().equals(PluginServices.getText(this,"inside_polygon"))) {
			vle.selectInsidePolygon(polygon);
		} else if (getType().equals(PluginServices.getText(this,"cross_circle")) || getType().equals(PluginServices.getText(this,"cross_polygon"))) {
			vle.selectCrossPolygon(polygon);
		} else if (getType().equals(PluginServices.getText(this,"out_circle")) || getType().equals(PluginServices.getText(this,"out_polygon")) || getType().equals(PluginServices.getText(this,"out_rectangle"))) {
			vle.selectOutPolygon(polygon);
		}
		ArrayList selectedRow = vle.getSelectedRow();
		PluginServices.getMDIManager().restoreCursor();
		if (selectedRow.size() > 0) {
			nextState = "Selection.WithSelectedFeatures";
			end();
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
		ComplexSelectionCADToolState actualState = _fsm.getState();
		String status = actualState.getName();
		VectorialLayerEdited vle = getVLE();
		ArrayList selectedHandler = vle.getSelectedHandler();
		ViewPort vp=vle.getLayer().getMapContext().getViewPort();
		if (status.equals("Selection.SecondPoint") || status.equals("Selection.SecondPointOutRectangle")) {
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
					DefaultCADTool.selectSymbol);
			Image img = vle.getSelectionImage();
	        g.drawImage(img, 0, 0, null);
	        return;
		}if (status.equals("Selection.SecondPointCircle")) {
			IGeometry circle=ShapeFactory.createCircle(firstPoint,new Point2D.Double(x,y));
			GeneralPathX gpx=new GeneralPathX();
			gpx.append(circle.getInternalShape(),true);
			IGeometry circleSel=ShapeFactory.createPolyline2D(gpx);
			// Dibuja el círculo de selección
			circleSel.draw((Graphics2D) g,
					vp,
					DefaultCADTool.selectSymbol);
			Image img = vle.getSelectionImage();
	        g.drawImage(img, 0, 0, null);
	        return;
		}else if (status.equals("Selection.NextPointPolygon")) {
			// Dibuja el polígono de selección
			IGeometry polygon=getGeometryPolygon(new Point2D.Double(x,y));
			polygon.draw((Graphics2D) g,
					vp,
					DefaultCADTool.selectSymbol);
			Image img = vle.getSelectionImage();
	        g.drawImage(img, 0, 0, null);
	        return;
		}else if (status.equals("Selection.WithHandlers")) {
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
				geom.draw((Graphics2D) g, vp, DefaultCADTool.modifySymbol);
			}
			return;
		}else{
			if (!vle.getLayer().isVisible())
				return;
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
		ComplexSelectionCADToolState actualState = (ComplexSelectionCADToolState) _fsm
				.getPreviousState();
		String status = actualState.getName();
		System.out.println("PREVIOUSSTATE =" + status); // + "ESTADO ACTUAL: " +
		// _fsm.getState());
		System.out.println("STATUS ACTUAL = " + _fsm.getTransition());
		if (s.equals(PluginServices.getText(this,"cancel"))){
			init();
			return;
		}else if (s.equals(PluginServices.getText(this,"select_all"))){
			selectAll();
			init();
			return;
		}
		if (status.equals("Selection.FirstPoint")) {
			setType(s);
			return;
		}else if (status.equals("Selection.NextPointPolygon")){
			if (s.equals(PluginServices.getText(this,"end_polygon")) || s.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.end"))) {
			IGeometry polygon=getGeometryPolygon(null);
			GeneralPathX gpx=new GeneralPathX();
			gpx.append(polygon,true);
			if (gpx.isCCW()) {
				gpx.flip();
				polygon=ShapeFactory.createPolygon2D(gpx);
			}
			selectWithPolygon(polygon);
			pointsPolygon.clear();
			setType(PluginServices.getText(this,"inside_circle"));
			return;
			}
		}
		init();
	}
	private int selectAll() {
		VectorialLayerEdited vle = getVLE();
		PluginServices.getMDIManager().setWaitCursor();
		vle.selectAll();
		ArrayList selectedRow = vle.getSelectedRow();
		PluginServices.getMDIManager().restoreCursor();
		if (selectedRow.size() > 0) {
			nextState = "Selection.WithSelectedFeatures";
		} else
			nextState = "Selection.FirstPoint";
		end();
		return selectedRow.size();
	}

	private IGeometry getGeometryPolygon(Point2D p) {
		Point2D[] points = (Point2D[]) pointsPolygon.toArray(new Point2D[0]);
		GeneralPathX gpx = new GeneralPathX();
		for (int i = 0; i < points.length; i++) {
			if (i == 0) {
				gpx.moveTo(points[i].getX(), points[i].getY());
			} else {
				gpx.lineTo(points[i].getX(), points[i].getY());
			}
		}
		if (p!=null){
			gpx.lineTo(p.getX(),p.getY());
			gpx.closePath();
			IGeometry polyline = ShapeFactory.createPolyline2D(gpx);
			return polyline;
		}
		gpx.closePath();
		IGeometry polygon = ShapeFactory.createPolygon2D(gpx);
		return polygon;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
	public void addValue(double d) {
	}

	public void end() {
		if (!getNextTool().equals("complex_selection"))
			CADExtension.setCADTool(getNextTool(),false);
	}

	public String getName() {
		return PluginServices.getText(this,"complex_selection_");
	}

	public boolean selectFeatures(double x, double y, InputEvent event) {
		ComplexSelectionCADToolState actualState =_fsm
				.getState();

		String status = actualState.getName();
		VectorialLayerEdited vle = getVLE();


		if ((status.equals("Selection.FirstPoint"))
				|| (status.equals("Selection.WithSelectedFeatures"))) {
			PluginServices.getMDIManager().setWaitCursor();
			firstPoint = new Point2D.Double(x, y);
			vle.selectWithPoint(x,y,multipleSelection);
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.outrectangle"))){
			this.type=PluginServices.getText(this,"out_rectangle");
		}else if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.intropolygon"))){
			this.type=PluginServices.getText(this,"inside_polygon");
		}else if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.crosspolygon"))){
			this.type=PluginServices.getText(this,"cross_polygon");
		}else if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.outpolygon"))){
			this.type=PluginServices.getText(this,"out_polygon");
		}else if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.introcircle"))){
			this.type=PluginServices.getText(this,"inside_circle");
		}else if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.crosscircle"))){
			this.type=PluginServices.getText(this,"cross_circle");
		}else if (type.equalsIgnoreCase(PluginServices.getText(this,"ComplexSelectionCADTool.outcircle"))){
			this.type=PluginServices.getText(this,"out_circle");
		}else if (type.equals(PluginServices.getText(this,"select_all"))){
			selectAll();
			init();
		}else{
			this.type = type;
		}
		pointsPolygon.clear();
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
		try{
		_fsm.addPoint(x, y, event);
		}catch (Exception e) {
			init();
		}
		System.out.println("ESTADO ACTUAL: " + getStatus());

		FLyrVect lv=(FLyrVect)((VectorialLayerEdited)CADExtension.getEditionManager().getActiveLayerEdited()).getLayer();
		com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices.getMDIManager().getAllWindows();

		for (int i=0 ; i<views.length ; i++){
			if (views[i] instanceof Table){
				Table table=(Table)views[i];
				if (table.getModel().getAssociatedTable()!=null && table.getModel().getAssociatedTable().equals(lv))
					table.updateSelection();
			}
		}
	}
	public String getStatus() {
		try {
			ComplexSelectionCADToolState actualState = (ComplexSelectionCADToolState) _fsm
					.getPreviousState();
			String status = actualState.getName();

			return status;
		} catch (NullPointerException e) {
			return "Selection.FirstPoint";
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      java.lang.String)
	 */
	public void transition(String s) throws CommandException {
		if (!super.changeCommand(s)){

			_fsm.addOption(s);

    	}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      double)
	 */
	public void transition(double d) {
		_fsm.addValue(d);
	}

	public String toString() {
		return "_complex_selection";
	}
	public String getNextState() {
		return nextState;
	}

}
