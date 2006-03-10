package com.iver.cit.gvsig.layers;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;

public class VectorialLayerEdited extends DefaultLayerEdited {
	private ArrayList selectedHandler = new ArrayList();
	private ArrayList selectedRow = new ArrayList();
	private ArrayList selectedRowIndex = new ArrayList();

	public VectorialLayerEdited(FLayer lyr)
	{
		super(lyr);
	}

	public ArrayList getSelectedHandler() {
		return selectedHandler;
	}

	public ArrayList getSelectedRow() {
		return selectedRow;
	}

	public ArrayList getSelectedRowIndex() {
		return selectedRowIndex;
	}

	public void clearSelection() {
		selectedHandler.clear();
		selectedRow.clear();
		selectedRowIndex.clear();
	}
	/**
	 * @return Returns the selectedRow.
	 */
	public IFeature[] getSelectedRowsCache() {
		return (IFeature[]) selectedRow.toArray(new IFeature[0]);
	}
	public void refreshSelectionCache(Point2D firstPoint,CADToolAdapter cta){

		FBitSet selection = cta.getVectorialAdapter()
		.getSelection();
		double min = Double.MAX_VALUE;
//		 Cogemos las entidades seleccionadas
		for (int i = selection.nextSetBit(0); i >= 0; i = selection
				.nextSetBit(i + 1)) {
			Handler[] handlers = null;

			DefaultFeature fea = null;
			try {
				fea = (DefaultFeature) cta
						.getVectorialAdapter().getRow(i).getLinkedRow();
				/* clonedGeometry = fea.getGeometry().cloneGeometry();
				handlers = clonedGeometry
						.getHandlers(IGeometry.SELECTHANDLER);
				selectedRow.add(new DefaultFeature(clonedGeometry, fea
						.getAttributes())); */
				handlers = fea.getGeometry().getHandlers(IGeometry.SELECTHANDLER);
				selectedRow.add(fea);
				selectedRowIndex.add(new Integer(i));
				// y miramos los handlers de cada entidad seleccionada
				min = cta.getMapControl().getViewPort()
						.toMapDistance(SelectionCADTool.tolerance);
				// int hSel = -1;
				for (int j = 0; j < handlers.length; j++) {
					Point2D handlerPoint = handlers[j].getPoint();
					double distance = firstPoint.distance(handlerPoint);
					if (distance <= min) {
						min = distance;
						//hSel = j;
						selectedHandler.add(handlers[j]);
					}
				}
				// Se añade un solo handler por
				// cada geometría seleccionada
				// if (hSel != -1) {
				// 	selectedHandler.add(handlers[hSel]);
				// 	System.out.println("Handler seleccionado: " + hSel);
				// }

			} catch (DriverIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
