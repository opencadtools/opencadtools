/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.edition.AnnotationEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.SelectionCADToolContext.SelectionCADToolState;
import com.iver.cit.gvsig.gui.panels.TextFieldEdit;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class SelectionCADTool extends DefaultCADTool {
    public static int tolerance = 4;

    protected SelectionCADToolContext _fsm;

    protected Point2D firstPoint;

    // private Point2D lastPoint;

    protected String nextState;
    // Registros de los que se ha sleccionado alg�n handler.
    protected ArrayList rowselectedHandlers = new ArrayList();
    protected String type = PluginServices.getText(this, "simple");
    protected ArrayList pointsPolygon = new ArrayList();

    protected boolean multipleSelection = false;

    /**
     * Crea un nuevo SelectionCADTool.
     */
    public SelectionCADTool() {
    }

    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    @Override
    public void init() {
	_fsm = new SelectionCADToolContext(this);
	setNextTool("selection");
	setType(PluginServices.getText(this, "simple"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double, double)
     */
    public void transition(double x, double y, InputEvent event) {
	System.out.println("TRANSICION DESDE ESTADO " + _fsm.getState()
		+ " x= " + x + " y=" + y);
	try {
	    _fsm.addPoint(x, y, event);
	} catch (Exception e) {
	    init();
	    PluginServices.getMDIManager().restoreCursor();
	}
	System.out.println("ESTADO ACTUAL: " + getStatus());

	// ESTO LO QUITO POR AHORA, PERO PUEDE QUE LO NECESITEMOS VOLVER A
	// PONER.
	// Lo he quitado porque cuando seleccionas algo con CAD, molesta que
	// te hagan un redibujado.
	FLyrVect lv = (FLyrVect) ((VectorialLayerEdited) CADExtension
		.getEditionManager().getActiveLayerEdited()).getLayer();
	// lv.getSource().getRecordset().getSelectionSupport().fireSelectionEvents();
	com.iver.andami.ui.mdiManager.IWindow[] views = (com.iver.andami.ui.mdiManager.IWindow[]) PluginServices
		.getMDIManager().getAllWindows();

	for (int i = 0; i < views.length; i++) {
	    if (views[i] instanceof Table) {
		Table table = (Table) views[i];
		if (table.getModel().getAssociatedTable() != null
			&& table.getModel().getAssociatedTable().equals(lv)) {
		    table.updateSelection();
		}
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double)
     */
    public void transition(double d) {
	_fsm.addValue(d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, java.lang.String)
     */
    public void transition(String s) throws CommandException {
	if (!super.changeCommand(s)) {
	    _fsm.addOption(s);
	}
    }

    public String getNextState() {
	return nextState;
    }

    protected void pointDoubleClick(MapControl map) throws ReadDriverException {
	FLayer[] actives = map.getMapContext().getLayers().getActives();
	for (int i = 0; i < actives.length; i++) {
	    if (actives[i] instanceof FLyrAnnotation && actives[i].isEditing()) {
		FLyrAnnotation lyrAnnotation = (FLyrAnnotation) actives[i];
		lyrAnnotation.setSelectedEditing();
		lyrAnnotation.setInEdition(lyrAnnotation.getRecordset()
			.getSelection().nextSetBit(0));
		FLabel fl = lyrAnnotation
			.getLabel(lyrAnnotation.getInEdition());
		if (fl != null) {
		    View vista = (View) PluginServices.getMDIManager()
			    .getActiveWindow();
		    TextFieldEdit tfe = new TextFieldEdit(lyrAnnotation);
		    tfe.show(
			    vista.getMapControl().getViewPort()
				    .fromMapPoint(fl.getOrig()),
			    vista.getMapControl());
		}
	    }
	}
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como par� metro el
     * editableFeatureSource que ya estar� creado.
     * 
     * @param selection
     *            Bitset con las geometr�as que est�n seleccionadas.
     * @param x
     *            par�metro x del punto que se pase en esta transici�n.
     * @param y
     *            par�metro y del punto que se pase en esta transici�n.
     */
    public void addPoint(double x, double y, InputEvent event) {
	if (event != null && ((MouseEvent) event).getClickCount() == 2) {
	    try {
		pointDoubleClick((MapControl) event.getComponent());
	    } catch (ReadDriverException e) {
		NotificationManager.addError(e.getMessage(), e);
	    }
	    return;
	}
	SelectionCADToolState actualState = (SelectionCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();
	System.out.println("PREVIOUSSTATE =" + status); // + "ESTADO ACTUAL: " +
							// _fsm.getState());
	VectorialLayerEdited vle = getVLE();
	VectorialEditableAdapter vea = vle.getVEA();
	ArrayList selectedHandler = vle.getSelectedHandler();
	ArrayList selectedRow = vle.getSelectedRow();
	System.out.println("STATUS ACTUAL = " + _fsm.getTransition());
	if (status.equals("Selection.FirstPoint")) {
	    firstPoint = new Point2D.Double(x, y);
	    pointsPolygon.add(firstPoint);
	} else if (status.equals("Selection.SecondPoint")) {
	} else if (status.equals("Selection.WithFeatures")) {
	} else if (status.equals("Selection.WithHandlers")) {
	    vea.startComplexRow();
	    ArrayList selectedRowsAux = new ArrayList();
	    for (int i = 0; i < selectedRow.size(); i++) {
		IRowEdited row = (IRowEdited) selectedRow.get(i);
		IFeature feat = (IFeature) row.getLinkedRow().cloneRow();
		IGeometry ig = feat.getGeometry();
		if (vea instanceof AnnotationEditableAdapter) {
		    // Movemos la geometr�a
		    UtilFunctions.moveGeom(ig, x - firstPoint.getX(), y
			    - firstPoint.getY());
		} else {
		    // Movemos los handlers que hemos seleccionado
		    // previamente dentro del m�todo select()
		    Handler[] handlers = ig
			    .getHandlers(IGeometry.SELECTHANDLER);
		    for (int k = 0; k < selectedHandler.size(); k++) {
			Handler h = (Handler) selectedHandler.get(k);
			for (int j = 0; j < handlers.length; j++) {
			    if (h.getPoint().equals(handlers[j].getPoint())) {
				handlers[j].set(x, y);
			    }
			}
		    }
		}
		modifyFeature(row.getIndex(), feat);
		selectedRowsAux.add(new DefaultRowEdited(feat,
			IRowEdited.STATUS_MODIFIED, row.getIndex()));
	    }
	    firstPoint = new Point2D.Double(x, y);
	    vle.setSelectionCache(VectorialLayerEdited.SAVEPREVIOUS,
		    selectedRowsAux);
	    // clearSelection();
	    // selectedRow.addAll(selectedRowsAux);
	    String description = PluginServices.getText(this, "move_handlers");
	    vea.endComplexRow(description);
	}
    }

    /**
     * Receives second point
     * 
     * @param x
     * @param y
     * @return numFeatures selected
     */
    public int selectWithSecondPoint(double x, double y, InputEvent event) {
	VectorialLayerEdited vle = getVLE();
	PluginServices.getMDIManager().setWaitCursor();
	vle.selectWithSecondPoint(x, y);
	ArrayList selectedRow = vle.getSelectedRow();
	PluginServices.getMDIManager().restoreCursor();
	if (selectedRow.size() > 0) {
	    nextState = "Selection.WithSelectedFeatures";
	} else {
	    nextState = "Selection.FirstPoint";
	}
	return selectedRow.size();
    }

    /**
     * M�todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param selectedGeometries
     *            BitSet con las geometr�as seleccionadas.
     * @param x
     *            par�metro x del punto que se pase para dibujar.
     * @param y
     *            par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {
	SelectionCADToolState actualState = _fsm.getState();
	String status = actualState.getName();
	VectorialLayerEdited vle = getVLE();
	if (vle == null) {
	    return;
	}
	ArrayList selectedHandler = vle.getSelectedHandler();
	ViewPort vp = vle.getLayer().getMapContext().getViewPort();
	if (status.equals("Selection.SecondPoint")) {
	    // Dibuja el rect�ngulo de selecci�n
	    GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
		    4);
	    elShape.moveTo(firstPoint.getX(), firstPoint.getY());
	    elShape.lineTo(x, firstPoint.getY());
	    elShape.lineTo(x, y);
	    elShape.lineTo(firstPoint.getX(), y);
	    elShape.lineTo(firstPoint.getX(), firstPoint.getY());
	    ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g, vp,
		    DefaultCADTool.geometrySelectSymbol);
	    Image img = vle.getSelectionImage();
	    g.drawImage(img, 0, 0, null);
	    return;
	} else if (status.equals("Selection.WithHandlers")) {
	    // Movemos los handlers que hemos seleccionado
	    // previamente dentro del m�todo select()
	    double xPrev = 0;
	    double yPrev = 0;
	    for (int k = 0; k < selectedHandler.size(); k++) {
		Handler h = (Handler) selectedHandler.get(k);
		xPrev = h.getPoint().getX();
		yPrev = h.getPoint().getY();
		h.set(x, y);
	    }
	    // Y una vez movidos los v�rtices (handles)
	    // redibujamos la nueva geometr�a.
	    for (int i = 0; i < rowselectedHandlers.size(); i++) {
		IRowEdited rowEd = (IRowEdited) rowselectedHandlers.get(i);
		IGeometry geom = ((IFeature) rowEd.getLinkedRow())
			.getGeometry().cloneGeometry();
		g.setColor(Color.gray);
		geom.draw((Graphics2D) g, vp,
			DefaultCADTool.axisReferencesSymbol);
	    }
	    for (int k = 0; k < selectedHandler.size(); k++) {
		Handler h = (Handler) selectedHandler.get(k);
		h.set(xPrev, yPrev);
	    }
	    return;
	} else {
	    if (!vle.getLayer().isVisible()) {
		return;
	    }
	    try {
		Image imgSel = vle.getSelectionImage();
		if (imgSel != null) {
		    g.drawImage(imgSel, 0, 0, null);
		}
		Image imgHand = vle.getHandlersImage();
		if (imgHand != null) {
		    g.drawImage(imgHand, 0, 0, null);
		}
	    } catch (Exception e) {
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
	SelectionCADToolState actualState = (SelectionCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();
	System.out.println("PREVIOUSSTATE =" + status); // + "ESTADO ACTUAL: " +
	// _fsm.getState());
	System.out.println("STATUS ACTUAL = " + _fsm.getTransition());
	if (s.equals(PluginServices.getText(this, "cancel"))) {
	    init();
	    return;
	}
	if (status.equals("Selection.FirstPoint")) {
	    setType(s);
	    return;
	}
	init();
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
	    return "Selection.FirstPoint";
	}
    }

    @Override
    public void end() {
	if (!getNextTool().equals("selection")) {
	    CADExtension.setCADTool(getNextTool(), false);
	}
    }

    public String getName() {
	return PluginServices.getText(this, "selection_");
    }

    public boolean selectFeatures(double x, double y, InputEvent event) {
	SelectionCADToolState actualState = (SelectionCADToolState) _fsm
		.getState();

	String status = actualState.getName();
	VectorialLayerEdited vle = getVLE();
	multipleSelection = event.isControlDown();
	if ((status.equals("Selection.FirstPoint"))
		|| (status.equals("Selection.WithSelectedFeatures"))) {
	    PluginServices.getMDIManager().setWaitCursor();
	    firstPoint = new Point2D.Double(x, y);

	    vle.selectWithPoint(x, y, multipleSelection);
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

	// Se comprueba si se pincha en una gemometr�a
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
	if (type.equals("S") || type.equals("s")) {
	    this.type = PluginServices.getText(this, "simple");
	} else {
	    this.type = type;
	}
	pointsPolygon.clear();
    }

    @Override
    public String toString() {
	return "_selection";
    }

    public void multipleSelection(boolean b) {
	multipleSelection = b;

    }

    public void drawOperation(Graphics g, ArrayList pointList) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isMultiTransition() {
	// TODO Auto-generated method stub
	return false;
    }

    public void transition(InputEvent event) {
	// TODO Auto-generated method stub

    }

}
