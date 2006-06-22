package com.iver.cit.gvsig.layers;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.StartEditing;
import com.iver.cit.gvsig.ViewCommandStackExtension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FGraphicUtilities;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerDrawEvent;
import com.iver.cit.gvsig.fmap.layers.LayerDrawingListener;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.gui.cad.snapping.ISnapper;
import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.vividsolutions.jts.geom.Geometry;

public class VectorialLayerEdited extends DefaultLayerEdited implements LayerDrawingListener{
	private ArrayList selectedHandler = new ArrayList();
	private ArrayList selectedRow = new ArrayList();
	private Point2D lastPoint;
	private Point2D firstPoint;
	private CADTool cadtool=null;

	private ArrayList snappers = new ArrayList();
	private ArrayList layersToSnap = new ArrayList();

	public VectorialLayerEdited(FLayer lyr)
	{
		super(lyr);
		lyr.getFMap().addLayerDrawingListener(this);
		// Por defecto, siempre hacemos snapping sobre la capa en edición.
		layersToSnap.add(lyr);
	}

	public ArrayList getSelectedHandler() {
		return selectedHandler;
	}

	public ArrayList getSelectedRow() {
		return selectedRow;
	}

	public void clearSelection() {
		selectedHandler.clear();
		selectedRow.clear();
		FBitSet selection=getVEA().getSelection();
		selection.clear();
	}
	/**
	 * @return Returns the selectedRow.
	 */
	public IFeature[] getSelectedRowsCache() {
		return (IFeature[]) selectedRow.toArray(new IFeature[0]);
	}
	public void selectWithPoint(double x, double y,boolean multipleSelection){
		firstPoint = new Point2D.Double(x, y);
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		if (!multipleSelection) {
			clearSelection();
		}
		// Se comprueba si se pincha en una gemometría
		ViewPort vp=getLayer().getFMap().getViewPort();
		double tam =vp.toMapDistance(SelectionCADTool.tolerance);
		Rectangle2D rect = new Rectangle2D.Double(firstPoint.getX() - tam,
				firstPoint.getY() - tam, tam * 2, tam * 2);

		String strEPSG = vp.getProjection().getAbrev().substring(5);
		IRowEdited[] feats;

		try {
			feats = vea.getFeatures(rect, strEPSG);
			selection.clear();
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < feats.length; i++) {
				IFeature feat = (IFeature) feats[i].getLinkedRow();
				IGeometry geom = feat.getGeometry();

				if (geom.intersects(rect)) { // , 0.1)){
					selection.set(feats[i].getIndex(), true);
					selectedRow.add(feats[i]);
					geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
					drawHandlers(geom.cloneGeometry(),gh,vp);
				}
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		} catch (DriverException e1) {
			e1.printStackTrace();
		}

	}
	public void selectWithSecondPoint(double x, double y) {
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		lastPoint = new Point2D.Double(x, y);
		ViewPort vp=getLayer().getFMap().getViewPort();
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

		String strEPSG = vp.getProjection().getAbrev().substring(5);
		IRowEdited[] feats;
		try {
			feats = vea.getFeatures(rect, strEPSG);
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < feats.length; i++) {
				IGeometry geom = ((IFeature) feats[i].getLinkedRow())
						.getGeometry();

				if (firstPoint.getX() < lastPoint.getX()) {
					if (rect.contains(geom.getBounds2D())) {
						selectedRow.add(feats[i]);
						selection.set(feats[i].getIndex(), true);
						geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
						drawHandlers(geom.cloneGeometry(),gh,vp);
					}
				} else {
					if (geom.intersects(rect)) { // , 0.1)){
						selectedRow.add(feats[i]);
						selection.set(feats[i].getIndex(), true);
						geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
						drawHandlers(geom.cloneGeometry(),gh,vp);
					}
				}
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		} catch (DriverException e) {
			e.printStackTrace();
		}

	}
	public void selectInsidePolygon(IGeometry polygon) {
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		ViewPort vp=getLayer().getFMap().getViewPort();
		selection.clear();
		selectedRow.clear();
		Rectangle2D rect = polygon.getBounds2D();

		String strEPSG = vp.getProjection().getAbrev().substring(5);
		IRowEdited[] feats;
		try {
			feats = vea.getFeatures(rect, strEPSG);
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < feats.length; i++) {
				IGeometry geom = ((IFeature) feats[i].getLinkedRow())
					.getGeometry();
					if (contains(polygon,geom)) {
						selectedRow.add(feats[i]);
						selection.set(feats[i].getIndex(), true);
						geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
						drawHandlers(geom.cloneGeometry(),gh,vp);
					}
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		} catch (DriverException e) {
			e.printStackTrace();
		}
	}

	public void selectCrossPolygon(IGeometry polygon) {
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		ViewPort vp=getLayer().getFMap().getViewPort();
		selection.clear();
		selectedRow.clear();
		Rectangle2D rect = polygon.getBounds2D();

		String strEPSG = vp.getProjection().getAbrev().substring(5);
		IRowEdited[] feats;
		try {
			feats = vea.getFeatures(rect, strEPSG);
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < feats.length; i++) {
				IGeometry geom = ((IFeature) feats[i].getLinkedRow())
					.getGeometry();
					if (contains(polygon,geom) || intersects(polygon,geom)) {
						selectedRow.add(feats[i]);
						selection.set(feats[i].getIndex(), true);
						geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
						drawHandlers(geom.cloneGeometry(),gh,vp);
					}
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		} catch (DriverException e) {
			e.printStackTrace();
		}
	}

	public void selectOutPolygon(IGeometry polygon) {
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		ViewPort vp=getLayer().getFMap().getViewPort();
		selection.clear();
		selectedRow.clear();

		try {
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < vea.getRowCount(); i++) {
				IRowEdited rowEd=(IRowEdited)vea.getRow(i);
				IGeometry geom = ((IFeature)rowEd.getLinkedRow())
						.getGeometry();
					if (!contains(polygon,geom) && !intersects(polygon,geom)) {
						selectedRow.add(rowEd);
						selection.set(rowEd.getIndex(), true);
						geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
						drawHandlers(geom.cloneGeometry(),gh,vp);
					}
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		} catch (DriverIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void selectAll() {
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		ViewPort vp=getLayer().getFMap().getViewPort();
		selection.clear();
		selectedRow.clear();
		try {
			BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < vea.getRowCount(); i++) {
				IRowEdited rowEd=(IRowEdited)vea.getRow(i);
				IGeometry geom = ((IFeature)rowEd.getLinkedRow())
						.getGeometry();
				selectedRow.add(rowEd);
				selection.set(rowEd.getIndex(), true);
				geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
				drawHandlers(geom.cloneGeometry(),gh,vp);
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		} catch (DriverIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void refreshSelectionCache(Point2D firstPoint,CADToolAdapter cta){
		VectorialEditableAdapter vea = getVEA();
		FBitSet selection = vea.getSelection();
		double min = java.lang.Double.MAX_VALUE;
//		 Cogemos las entidades seleccionadas
		clearSelection();
		ViewPort vp=getLayer().getFMap().getViewPort();
		BufferedImage selectionImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gs = selectionImage.createGraphics();
		BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(), vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gh = handlersImage.createGraphics();
		for (int i = selection.nextSetBit(0); i >= 0; i = selection
				.nextSetBit(i + 1)) {
			Handler[] handlers = null;

			DefaultRowEdited dre = null;
			try {
				dre = (DefaultRowEdited)(vea.getRow(i));
				IFeature feat=(DefaultFeature)dre.getLinkedRow();
				IGeometry geom=feat.getGeometry();
				handlers = geom.getHandlers(IGeometry.SELECTHANDLER);
				selectedRow.add(dre);
				geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
				drawHandlers(geom.cloneGeometry(),gh,vp);
				// y miramos los handlers de cada entidad seleccionada
				min = cta.getMapControl().getViewPort()
						.toMapDistance(SelectionCADTool.tolerance);
				for (int j = 0; j < handlers.length; j++) {
					Point2D handlerPoint = handlers[j].getPoint();
					double distance = firstPoint.distance(handlerPoint);
					if (distance <= min) {
						min = distance;
						selectedHandler.add(handlers[j]);
					}
				}
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		vea.setSelectionImage(selectionImage);
		vea.setHandlersImage(handlersImage);
	}

	public void drawHandlers(IGeometry geom, Graphics2D gs, ViewPort vp) {
		if (!(getLayer() instanceof FLyrAnnotation)){
			Handler[] handlers = geom.getHandlers(IGeometry.SELECTHANDLER);
			FGraphicUtilities.DrawHandlers(gs, vp.getAffineTransform(), handlers);
		}
	}
	public Image getSelectionImage(){
		return getVEA().getSelectionImage();
	}
	public Image getHandlersImage() {
		return getVEA().getHandlersImage();
	}
	public VectorialEditableAdapter getVEA(){
		if (((FLyrVect)getLayer()).getSource() instanceof VectorialEditableAdapter)
			return (VectorialEditableAdapter)((FLyrVect)getLayer()).getSource();
		else
			return null;
	}

	public void beforeLayerDraw(LayerDrawEvent e) throws CancelationException {
		if (((FLyrVect) getLayer()).getSource() instanceof VectorialEditableAdapter) {
			VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) getLayer())
					.getSource();
			ViewPort vp = getLayer().getFMap().getViewPort();
			BufferedImage selectionImage = new BufferedImage(
					vp.getImageWidth(), vp.getImageHeight(),
					BufferedImage.TYPE_INT_ARGB);
			BufferedImage handlersImage = new BufferedImage(vp.getImageWidth(),
					vp.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = selectionImage.createGraphics();
			Graphics2D gh = handlersImage.createGraphics();
			for (int i = 0; i < selectedRow.size(); i++) {
				IFeature feat = (IFeature) ((IRowEdited) selectedRow.get(i))
						.getLinkedRow();
				IGeometry geom = feat.getGeometry();
				geom.cloneGeometry().draw(gs, vp, CADTool.drawingSymbol);
				drawHandlers(geom.cloneGeometry(), gh, vp);
			}
			vea.setSelectionImage(selectionImage);
			vea.setHandlersImage(handlersImage);
		}
	}

	public void afterLayerDraw(LayerDrawEvent e) throws CancelationException {
	}

	public void beforeGraphicLayerDraw(LayerDrawEvent e) throws CancelationException {
	}

	public void afterLayerGraphicDraw(LayerDrawEvent e) throws CancelationException {
	}
	private static boolean contains(IGeometry g1,IGeometry g2) {
		Geometry geometry1=g1.toJTSGeometry();
		Geometry geometry2=g2.toJTSGeometry();
		if (geometry1==null || geometry2==null)return false;
		return geometry1.contains(geometry2);
	}
	private static boolean intersects(IGeometry g1,IGeometry g2) {
		Geometry geometry1=g1.toJTSGeometry();
		Geometry geometry2=g2.toJTSGeometry();
		if (geometry1==null || geometry2==null)return false;
		return geometry1.intersects(geometry2);
	}

	public void activationGained(LayerEvent e) {
		if (ViewCommandStackExtension.csd!=null){
			ViewCommandStackExtension.csd.setModel(((IEditableSource) ((FLyrVect)getLayer()).getSource())
							.getCommandRecord());
		}

		if (cadtool!=null){
			CADExtension.getCADToolAdapter().setCadTool(cadtool);
			PluginServices.getMainFrame().setSelectedTool(cadtool.toString());
			StartEditing.startCommandsApplicable(null,(FLyrVect)getLayer());
			CADExtension.initFocus();
		}

	}

	public void activationLost(LayerEvent e) {
		try{
			cadtool=CADExtension.getCADTool();
		}catch (EmptyStackException e1) {
			cadtool=new SelectionCADTool();
			cadtool.init();
		}

	}

	public ArrayList getSnappers() {
		return snappers;
	}

	public ArrayList getLayersToSnap() {
		return layersToSnap;
	}

	public void setLayersToSnap(ArrayList layersToSnap) {
		this.layersToSnap = layersToSnap;

	}

	public void setSelectionCache(ArrayList selectedRowAux) {
		clearSelection();
		VectorialEditableAdapter vea=getVEA();
		FBitSet selection=vea.getSelection();
		selectedRow.addAll(selectedRowAux);
		for (int i = 0;i < selectedRow.size(); i++) {
			  IRowEdited edRow = (IRowEdited) selectedRow.get(i);
			  selection.set(edRow.getIndex());
		}
	}

}
