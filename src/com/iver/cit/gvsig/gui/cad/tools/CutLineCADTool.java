/*
 * Copyright 2008 Deputación Provincial de A Coruña
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
 * http://www.cartolab.es
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
 */
package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
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
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.CutLineCADToolContext;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

/**
 * Extension to cut lines.
 *
 * @author Jose Ignacio Lamas [LBD]
 * @author Nacho Varela [Cartolab]
 * @author Pablo Sanxiao [CartoLab]
 */
public class CutLineCADTool extends InsertionCADTool {

	private CutLineCADToolContext _fsm;

	private IGeometry geometry; // Storing the geometry which contains the first point

	private Point2D cuttingPoint;

	private ArrayList oldPoints;

	private int cuttingPointIndex = -1;

	private boolean keepFirstPiece;

	private IRowEdited selectedRow;

	double PROXIMITY_THRESHOLD = 0.000001;

	// Storing the number of the multi geometry that we are modifying
	private int numberOfMultiGeomSelected;

	public void init() {
		// super.init();
		clear();
		_fsm = new CutLineCADToolContext(this);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      double, double)
	 */
	public void transition(double x, double y, InputEvent event) {
		// System.out.println("cortarLinea transicion x,y,event");
		_fsm.addPoint(x, y, event);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      double)
	 */
	public void transition(double d) {
		// _fsm.addValue(d);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet,
	 *      java.lang.String)
	 */
	public void transition(String s) throws CommandException {
		if (!super.changeCommand(s)) {
			// System.out.println("---->>>>>> from cutLine calling
			// transition with string " + s);
			_fsm.addOption(s);
		}
	}

	public void transition(InputEvent event) {
		// System.out.println("cutLine transicion event");
		if (cuttingPoint != null) {
			_fsm.removeCutPoint(event);
		}
	}

	public void addPoint(double x, double y, InputEvent event) {
		// this method will be executed after the pointInsideFeature
	}

	public void addValue(double d) {
		// TODO Auto-generated method stub
	}

	public void addOption(String s) {
		// TODO Auto-generated method stub
	}

	public void drawOperation(Graphics g, ArrayList pointsList) {
		Point2D pointAux = null;
		int sizePixels = 12;
		int half = sizePixels / 2;
		if (cuttingPoint != null) {
			pointAux = CADExtension.getEditionManager().getMapControl()
			.getViewPort().fromMapPoint(cuttingPoint);
			g.drawRect((int) (pointAux.getX() - (half - 2)), (int) (pointAux
					.getY() - (half - 2)), sizePixels - 4, sizePixels - 4);
			g.drawRect((int) (pointAux.getX() - half),
					(int) (pointAux.getY() - half), sizePixels, sizePixels);

			if (geometry != null) {
				IGeometry geom = getCuttedGeometry();
				// Repainting the line
				geom.draw((Graphics2D) g, CADExtension.getEditionManager()
						.getMapControl().getViewPort(),
						DefaultCADTool.drawingSymbol);

				// Painting the vertex
				AffineTransform at = CADExtension.getEditionManager().getMapControl().getViewPort().getAffineTransform();
				Handler[] h = geom.getHandlers(IGeometry.SELECTHANDLER);
				FGraphicUtilities.DrawHandlers((Graphics2D) g,
						at, h,DefaultCADTool.drawingSymbol);
			}
		}

	}

	public String getName() {
		return PluginServices.getText(this, "cut_line_");
	}

	/**
	 * It detects if the point is inside the outline of the selected geometry at this
	 * time and store the information related to the situation and the rest of te points
	 * of the geometry.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean pointInsideFeature(double x, double y) {

		boolean isInside = false;
		// System.out.println("------>>>>>> calling pointInsideFeature "+x+",
		// "+y);

		VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
		.getEditionManager().getActiveLayerEdited();

		vle.selectWithPoint(x, y, false);

		ArrayList selectedRows = getSelectedRows();
		IRowEdited row = null;
		IGeometry ig = null;
		if (selectedRows.size() > 0) {
			clear();

			boolean pointFound = false;

			row = (DefaultRowEdited) selectedRows.get(0);
			// row =
			// getCadToolAdapter().getVectorialAdapter().getRow(selection.nextSetBit(0));
			ig = ((IFeature) row.getLinkedRow()).getGeometry();

			geometry = ig;
			selectedRow = row;
			cuttingPoint = new Point2D.Double(x, y);

			// filling the pointsList

			Coordinate c = new Coordinate(x, y);

			PathIterator theIterator = geometry.getPathIterator(null,
					FConverter.FLATNESS);
			double[] theData = new double[6];
			Coordinate from = null, first = null;
			int index = 0;
			boolean terminate = false;
			int numberMultiActual = 0;
			while (!theIterator.isDone() && !terminate) {
				int theType = theIterator.currentSegment(theData);
				switch (theType) {
				case PathIterator.SEG_MOVETO:
					from = new Coordinate(theData[0], theData[1]);
					first = from;
					numberMultiActual++;

					if (numberOfMultiGeomSelected != 0) {
						terminate = true;

						// We must check if the intersection point is not the end of the line
						if (pointFound && cuttingPointIndex == index - 1) {
							pointFound = false;

						}

					} else {
						oldPoints = new ArrayList();
						index = 0;
						oldPoints.add(index, new Point2D.Double(theData[0],
								theData[1]));
					}
					// if(c.equals(from)){
					// indexPuntoCorte = index;
					// puntoEncontrado = true;
					// multiSeleccionada = numeroMultiActual;
					// retorno = true;
					// }
					break;

				case PathIterator.SEG_LINETO:

					// System.out.println("SEG_LINETO");
					Coordinate to = new Coordinate(theData[0], theData[1]);
					LineSegment line = new LineSegment(from, to);
					Coordinate closestPoint = line.closestPoint(c);
					double dist = c.distance(closestPoint);
					if (c.equals(to)) {
						cuttingPointIndex = index;
						pointFound = true;
						numberOfMultiGeomSelected = numberMultiActual;
						isInside = true;
					} else if (!c.equals(from)) {
						if (dist < PROXIMITY_THRESHOLD) {
							cuttingPointIndex = index;
							pointFound = true;
							cuttingPoint = new Point2D.Double(closestPoint.x,
									closestPoint.y);
							oldPoints.add(index, new Point2D.Double(
									closestPoint.x, closestPoint.y));
							index++;
							numberOfMultiGeomSelected = numberMultiActual;
							isInside = true;
						}
					}
					oldPoints.add(index, new Point2D.Double(theData[0],
							theData[1]));
					from = to;
					break;
				case PathIterator.SEG_CLOSE:
					line = new LineSegment(from, first);

					closestPoint = line.closestPoint(c);
					dist = c.distance(closestPoint);
					if (c.equals(first)) {
						//
						// indexPuntoCorte = index;
						// puntoEncontrado = true;
						// multiSeleccionada = numeroMultiActual;
						// retorno = true;
					} else if ((dist < PROXIMITY_THRESHOLD)
							&& (!c.equals(from))) {
						cuttingPointIndex = index;
						pointFound = true;
						cuttingPoint = new Point2D.Double(closestPoint.x,
								closestPoint.y);
						oldPoints.add(index, new Point2D.Double(
								closestPoint.x, closestPoint.y));
						index++;
						numberOfMultiGeomSelected = numberMultiActual;
						isInside = true;
					}
					oldPoints.add(index, new Point2D.Double(first.x,
							first.y));
					from = first;
					break;

				} // end switch
				index++;
				theIterator.next();
				if (theIterator.isDone() && pointFound
						&& cuttingPointIndex == index - 1) {
					pointFound = false;
				}
			}
			if (!pointFound) {
				clear();
				isInside = false;
			}
		}
		getCadToolAdapter().setPreviousPoint((double[]) null);
		return isInside;
	}

	public String toString() {
		// TODO Auto-generated method stub
		return "_cortar_linea";
	}

	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.LINE:
		case FShape.MULTI:
			return true;
		}
		return false;
	}

	public void clear() {
		keys.clear();
		geometry = null;
		keepFirstPiece = true;
		oldPoints = null;
		this.setMultiTransition(false);
		getCadToolAdapter().setPreviousPoint((double[]) null);
		selectedRow = null;
		numberOfMultiGeomSelected = 0;
		cuttingPoint = null;

	}

	public void removeIntersectionPointPuntoCorte(InputEvent event) {
		clear();
	}

	public void drawOperation(Graphics g, double x, double y) {
		ArrayList lista = new ArrayList();
		lista.add(new Point2D.Double(x, y));
		drawOperation(g, lista);
	}

	/**
	 * It stores the changes made to the geometry and ask the user if wants to create
	 * a new entity whit the rest of the line
	 */
	public void saveChanges() {

		if (selectedRow != null) {
			//if (checksOnEditionSinContinuidad(getGeometriaRestante(),
			//	getCurrentGeoid(), false)) {
			int resp = JOptionPane.NO_OPTION;
			resp = JOptionPane.showConfirmDialog((Component) PluginServices
					.getMainFrame(), PluginServices.getText(this,
					"cut_new_line_with_rest"), PluginServices
					.getText(this, "cut_line"),
					JOptionPane.YES_NO_OPTION);

			((IFeature) selectedRow.getLinkedRow()).setGeometry(getCuttedGeometry());
			modifyFeature(selectedRow.getIndex(),(IFeature) selectedRow.getLinkedRow());

			if (resp == JOptionPane.YES_OPTION) {
				addNewElement(getRemainingGeometry(), selectedRow);
			}

		} else {

			int resp = JOptionPane.NO_OPTION;
			resp = JOptionPane.showConfirmDialog((Component) PluginServices
					.getMainFrame(), PluginServices.getText(this,
					"cut_rest_of_the_line_outside"), PluginServices.getText(
							this, "cortar_linea"), JOptionPane.YES_NO_OPTION);
			if (resp != JOptionPane.YES_OPTION) { // CANCEL DELETE
			} else {
				// Saving the resulting geometry and discarding the rest
				((IFeature) selectedRow.getLinkedRow())
				.setGeometry(getCuttedGeometry());
				modifyFeature(selectedRow.getIndex(),
						(IFeature) selectedRow.getLinkedRow());
			}
		}
	}


	public IGeometry getCuttedGeometry() {

		return getPieceOfGeometry();

	}


	/**
	 * Return the resulting geometry after cutting the selected point.
	 * @return
	 */
	public IGeometry getPieceOfGeometry() {

		GeneralPathX gpx = new GeneralPathX();

		PathIterator theIterator = geometry.getPathIterator(null,
				FConverter.FLATNESS);
		double[] theData = new double[6];
		int numberMultiActual = 0;
		while (!theIterator.isDone()) {
			int theType = theIterator.currentSegment(theData);

			switch (theType) {
			case PathIterator.SEG_MOVETO:
				double x = theData[0];
				double y = theData[1];
				numberMultiActual++;
				if (numberOfMultiGeomSelected == numberMultiActual) {
					if (keepFirstPiece) {
						for (int i = 0; i <= cuttingPointIndex; i++) {
							Point2D pointAux = (Point2D) oldPoints.get(i);
							if (i == 0) {
								gpx.moveTo(pointAux.getX(), pointAux.getY());
							} else {
								gpx.lineTo(pointAux.getX(), pointAux.getY());
							}
						}
					} else {
						//covering the old points from intersection point to the end
						boolean firstPointDigitalized = false;
						for (int i = cuttingPointIndex; i < oldPoints.size(); i++) {
							Point2D pointAux = (Point2D) oldPoints.get(i);
							if (!firstPointDigitalized) {
								gpx.moveTo(pointAux.getX(), pointAux.getY());
								firstPointDigitalized = true;
							} else {
								gpx.lineTo(pointAux.getX(), pointAux.getY());
							}
						}
					}
				} else {
					gpx.moveTo(x, y);
				}
				break;

			case PathIterator.SEG_LINETO:

				x = theData[0];
				y = theData[1];
				if (numberOfMultiGeomSelected == numberMultiActual) {

				} else {
					gpx.lineTo(x, y);
				}
				break;
			case PathIterator.SEG_CLOSE:
				if (numberOfMultiGeomSelected == numberMultiActual) {

				} else {
					gpx.closePath();
				}
				break;

			} // end switch
			theIterator.next();
		}
		IGeometry geom = ShapeFactory.createPolyline2D(gpx);
		return geom;

	}


	public IGeometry getRemainingGeometry() {

		keepFirstPiece = !keepFirstPiece;
		return getPieceOfGeometry();

	}


	/**
	 * It alternates the part of the geometry thah we will keep.
	 */
	public void changePieceOfGeometry() {
		keepFirstPiece = !keepFirstPiece;
		CADExtension.getEditionManager().getMapControl().repaint();
	}


	/**
	 * Copied from NewTankCADTool
	 *
	 * [NACHOV] Maybe it must be placed on DefaultCADTools
	 *
	 * @param geometry
	 *            DOCUMENT ME!
	 */
	public void addNewElement(IGeometry geometry, IRowEdited row) {

		//TODO [NACHOV] Maybe this method must be placed on DefaultCADTools
		VectorialLayerEdited vle= getVLE();
		VectorialEditableAdapter vea = getVLE().getVEA();

			int numAttr;
			try {
				numAttr = vea.getRecordset().getFieldCount();

				Value[] values = new Value[numAttr];
				//	for (int i = 0; i < numAttr; i++) {
				values = row.getAttributes();
				//values[i] = ValueFactory.createNullValue();
				//}
				String newFID;

				newFID = vea.getNewFID();
				DefaultFeature df = new DefaultFeature(geometry, values, newFID);
				int index;

				index = vea.addRow(df, getName(), EditionEvent.GRAPHIC);

				clearSelection();
				ArrayList selectedRow = vle.getSelectedRow();

				ViewPort vp = vle.getLayer().getMapContext().getViewPort();
				BufferedImage selectionImage = new BufferedImage(vp
						.getImageWidth(), vp.getImageHeight(),
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D gs = selectionImage.createGraphics();
				int inversedIndex=vea.getInversedIndex(index);
				selectedRow.add(new DefaultRowEdited(df,
						IRowEdited.STATUS_ADDED, inversedIndex ));
				vea.getSelection().set(inversedIndex);
				IGeometry geom = df.getGeometry();
				geom.cloneGeometry().draw(gs, vp, DefaultCADTool.selectionSymbol);
				vle.drawHandlers(geom.cloneGeometry(), gs, vp);
				vea.setSelectionImage(selectionImage);
				} catch (ExpansionFileWriteException e) {
					NotificationManager.addError(e.getMessage(), e);
				} catch (ValidateRowException e) {
					NotificationManager.addError(e.getMessage(), e);
				} catch (ReadDriverException e) {
					NotificationManager.addError(e.getMessage(), e);
				}


		draw(geometry.cloneGeometry());
	}





	/**
	 * Accion that will be executed when the insertion form is canceled.
	 * It deletes the last row added to the VectorialEditableAdapter.
	 */
	public void cancelInsertion() {
		getCadToolAdapter().delete(virtualIndex.intValue());
	}

	public boolean isMultiTransition() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMultiTransition(boolean condicion) {
		// TODO Auto-generated method stub

	}

	public void setPreviousTool(DefaultCADTool tool) {
		// TODO Auto-generated method stub

	}

}
