package com.iver.cit.gvsig.gui.cad;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.snapping.ISnapper;
import com.iver.cit.gvsig.gui.cad.snapping.NearestPointSnapper;
import com.iver.cit.gvsig.gui.cad.snapping.SnappingVisitor;
import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.iver.cit.gvsig.layers.ILayerEdited;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.utiles.console.JConsole;
import com.vividsolutions.jts.geom.Envelope;

public class CADToolAdapter extends Behavior {

	public static final int ABSOLUTE = 0;

	public static final int RELATIVE_SCP = 1;

	public static final int RELATIVE_SCU = 2;

	public static final int POLAR_SCP = 3;

	public static final int POLAR_SCU = 4;

	private double[] previousPoint = null;

	private Stack cadToolStack = new Stack();

	// Para pasarle las coordenadas cuando se produce un evento textEntered
	private int lastX;

	private int lastY;

	private FSymbol symbol = new FSymbol(FConstant.SYMBOL_TYPE_POINT, Color.RED);

	private Point2D mapAdjustedPoint;

	private ISnapper usedSnap = null;

	private boolean questionAsked = false;

	private Point2D adjustedPoint;

	private boolean snapping = false;

	private boolean adjustSnapping = false;

	private CADGrid cadgrid = new CADGrid();

	/**
	 * Pinta de alguna manera especial las geometrias seleccionadas para la
	 * edición. En caso de que el snapping esté activado, pintará el efecto del
	 * mismo.
	 *
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawCursor(g);
		getGrid().drawGrid(g);
		if (adjustedPoint != null) {
			Point2D p = null;
			if (mapAdjustedPoint != null) {
				p = mapAdjustedPoint;
			} else {
				p = getMapControl().getViewPort().toMapPoint(adjustedPoint);
			}
			((CADTool) cadToolStack.peek())
					.drawOperation(g, p.getX(), p.getY());
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) throws BehaviorException {
		if (e.getButton() == MouseEvent.BUTTON3) {
			CADExtension.showPopup(e);
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) throws BehaviorException {
		clearMouseImage();
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) throws BehaviorException {
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) throws BehaviorException {
		if (e.getButton() == MouseEvent.BUTTON1) {
			ViewPort vp = getMapControl().getMapContext().getViewPort();
			Point2D p;

			if (mapAdjustedPoint != null) {
				p = mapAdjustedPoint;
			} else {
				p = vp.toMapPoint(adjustedPoint);
			}
			transition(new double[] { p.getX(), p.getY() }, e, ABSOLUTE);
		}
	}

	/**
	 * Ajusta un punto de la imagen que se pasa como parámetro al grid si éste
	 * está activo y devuelve la distancia de un punto al punto ajustado
	 *
	 * @param point
	 * @param mapHandlerAdjustedPoint
	 *            DOCUMENT ME!
	 *
	 * @return Distancia del punto que se pasa como
	 *  parámetro al punto ajustado. Si no hay ajuste,
	 *  devuelve Double.MAX_VALUE
	 */
	private double adjustToHandler(Point2D point,
			Point2D mapHandlerAdjustedPoint) {

		ILayerEdited aux = CADExtension.getEditionManager().getActiveLayerEdited();
		if (!(aux instanceof VectorialLayerEdited))
			return Double.MAX_VALUE;
		VectorialLayerEdited vle = (VectorialLayerEdited) aux;
		
		ArrayList snappers = vle.getSnappers();
		ArrayList layersToSnap = vle.getLayersToSnap();


		ViewPort vp = getMapControl().getViewPort();

		// TODO: PROVISIONAL. PONER ALGO COMO ESTO EN UN CUADRO DE DIALOGO
		// DE CONFIGURACIÓN DEL SNAPPING
		NearestPointSnapper defaultSnap = new NearestPointSnapper();
		snappers.clear();
		snappers.add(defaultSnap);

		double mapTolerance = vp.toMapDistance(SelectionCADTool.tolerance);
		double minDist = mapTolerance;
//		double rw = getMapControl().getViewPort().toMapDistance(5);
		Point2D mapPoint = point;
		Rectangle2D r = new Rectangle2D.Double(mapPoint.getX() - mapTolerance / 2,
				mapPoint.getY() - mapTolerance / 2, mapTolerance, mapTolerance);

		Envelope e = FConverter.convertRectangle2DtoEnvelope(r);
		
		usedSnap = null;
		Point2D lastPoint = null;
		if (previousPoint != null)
		{
			lastPoint = new Point2D.Double(previousPoint[0], previousPoint[1]);
		}
		for (int j = 0; j < layersToSnap.size(); j++)
		{
			FLyrVect lyrVect = (FLyrVect) layersToSnap.get(j);
			SpatialCache cache = lyrVect.getSpatialCache();
			if (lyrVect.isVisible())
			{
				for (int i = 0; i < snappers.size(); i++)
				{
					ISnapper theSnapper = (ISnapper) snappers.get(i);
		
					SnappingVisitor snapVisitor = new SnappingVisitor(theSnapper, point, mapTolerance, lastPoint);
					// System.out.println("Cache size = " + cache.size());
					cache.query(e, snapVisitor);
		
					if (snapVisitor.getSnapPoint() != null) {
						if (minDist > snapVisitor.getMinDist())
						{
							minDist = snapVisitor.getMinDist();
							usedSnap = theSnapper;
							mapHandlerAdjustedPoint.setLocation(snapVisitor.getSnapPoint());
						}
					}
				}
			} // visible
		}
		if (usedSnap != null)
			return minDist;
		return Double.MAX_VALUE;

	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) throws BehaviorException {
		getMapControl().repaint();
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) throws BehaviorException {
		lastX = e.getX();
		lastY = e.getY();

		calculateSnapPoint(e.getPoint());
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) throws BehaviorException {

		lastX = e.getX();
		lastY = e.getY();

		calculateSnapPoint(e.getPoint());

		getMapControl().repaint();
	}

	private void clearMouseImage() {
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = Toolkit.getDefaultToolkit()
				.createCustomCursor(image, new Point(0, 0), "invisiblecursor");

		getMapControl().setCursor(transparentCursor);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g
	 *            DOCUMENT ME!
	 */
	private void drawCursor(Graphics g) {

		Point2D p = adjustedPoint;

		if (p == null) {
			getGrid().setViewPort(getMapControl().getViewPort());

			return;
		}

		int size1 = 15;
		int size2 = 3;
		g.drawLine((int) (p.getX() - size1), (int) (p.getY()),
				(int) (p.getX() + size1), (int) (p.getY()));
		g.drawLine((int) (p.getX()), (int) (p.getY() - size1),
				(int) (p.getX()), (int) (p.getY() + size1));

		getMapControl().setToolTipText(null);
		if (adjustedPoint != null) {
			if (adjustSnapping) {
				/* g.setColor(Color.ORANGE);
				g.drawRect((int) (adjustedPoint.getX() - 6),
						(int) (adjustedPoint.getY() - 6), 12, 12);
				g.drawRect((int) (adjustedPoint.getX() - 3),
						(int) (adjustedPoint.getY() - 3), 6, 6);
				g.setColor(Color.MAGENTA);
				g.drawRect((int) (adjustedPoint.getX() - 4),
						(int) (adjustedPoint.getY() - 4), 8, 8); */
				if (usedSnap != null)
				{
					usedSnap.draw(g, adjustedPoint);
					g.drawString(usedSnap.getToolTipText(), (int)p.getX()+9, (int)p.getY()- 7);
					// getMapControl().setToolTipText(usedSnap.getToolTipText());
				}

				adjustSnapping = false;
			} else {
				g.drawRect((int) (p.getX() - size2), (int) (p.getY() - size2),
						(int) (size2 * 2), (int) (size2 * 2));
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param point
	 */
	private void calculateSnapPoint(Point point) {
		// Se comprueba el ajuste a rejilla

		Point2D gridAdjustedPoint = getMapControl().getViewPort().toMapPoint(
				point);
		double minDistance = Double.MAX_VALUE;
		CADTool ct = (CADTool) cadToolStack.peek();
		if (ct instanceof SelectionCADTool
				&& ((SelectionCADTool) ct).getStatus().equals(
						"Selection.FirstPoint")) {
			mapAdjustedPoint = gridAdjustedPoint;
			adjustedPoint = (Point2D) point.clone();
		} else {

			minDistance = getGrid().adjustToGrid(gridAdjustedPoint);
			if (minDistance < Double.MAX_VALUE) {
				adjustedPoint = getMapControl().getViewPort().fromMapPoint(
						gridAdjustedPoint);
				mapAdjustedPoint = gridAdjustedPoint;
			} else {
				mapAdjustedPoint = null;
			}
		}
		Point2D handlerAdjustedPoint = null;

		// Se comprueba el ajuste a los handlers
		if (mapAdjustedPoint != null) {
			handlerAdjustedPoint = (Point2D) mapAdjustedPoint.clone(); // getMapControl().getViewPort().toMapPoint(point);
		} else {
			handlerAdjustedPoint = getMapControl().getViewPort().toMapPoint(
					point);
		}

		Point2D mapPoint = new Point2D.Double();
		double distance = adjustToHandler(handlerAdjustedPoint, mapPoint);

		if (distance < minDistance) {
			adjustSnapping = true;
			adjustedPoint = getMapControl().getViewPort().fromMapPoint(mapPoint);
			mapAdjustedPoint = mapPoint;
			minDistance = distance;
		}

		// Si no hay ajuste
		if (minDistance == Double.MAX_VALUE) {
			adjustedPoint = point;
			mapAdjustedPoint = null;
		}

	}

	/**
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) throws BehaviorException {
		getMapControl().cancelDrawing();
		ViewPort vp = getMapControl().getViewPort();
		// Point2D pReal = vp.toMapPoint(e.getPoint());

		Point2D pReal = new Point2D.Double(vp.getAdjustedExtent().getCenterX(),
				vp.getAdjustedExtent().getCenterY());
		int amount = e.getWheelRotation();
		double nuevoX;
		double nuevoY;
		double factor;

		if (amount > 0) // nos acercamos
		{
			factor = 0.9;
		} else // nos alejamos
		{
			factor = 1.2;
		}
		Rectangle2D.Double r = new Rectangle2D.Double();
		if (vp.getExtent() != null) {
			nuevoX = pReal.getX()
					- ((vp.getExtent().getWidth() * factor) / 2.0);
			nuevoY = pReal.getY()
					- ((vp.getExtent().getHeight() * factor) / 2.0);
			r.x = nuevoX;
			r.y = nuevoY;
			r.width = vp.getExtent().getWidth() * factor;
			r.height = vp.getExtent().getHeight() * factor;

			vp.setExtent(r);
		}
	}

	/**
	 * Método que realiza las transiciones en las herramientas en función de un
	 * texto introducido en la consola
	 *
	 * @param text
	 *            DOCUMENT ME!
	 */
	public void textEntered(String text) {
		if (text == null) {
			transition("cancel");
		} else {
			/*
			 * if ("".equals(text)) { transition("aceptar"); } else {
			 */
			text = text.trim();
			int type = ABSOLUTE;
			String[] numbers = new String[1];
			numbers[0] = text;
			if (text.indexOf(",") != -1) {

				numbers = text.split(",");
				if (numbers[0].substring(0, 1).equals("@")) {
					numbers[0] = numbers[0].substring(1, numbers[0].length());
					type = RELATIVE_SCU;
					if (numbers[0].substring(0, 1).equals("*")) {
						type = RELATIVE_SCP;
						numbers[0] = numbers[0].substring(1, numbers[0]
								.length());
					}
				}
			} else if (text.indexOf("<") != -1) {
				type = POLAR_SCP;
				numbers = text.split("<");
				if (numbers[0].substring(0, 1).equals("@")) {
					numbers[0] = numbers[0].substring(1, numbers[0].length());
					type = POLAR_SCU;
					if (numbers[0].substring(0, 1).equals("*")) {
						type = POLAR_SCP;
						numbers[0] = numbers[0].substring(1, numbers[0]
								.length());
					}
				}
			}

			double[] values = null;

			try {
				if (numbers.length == 2) {
					// punto
					values = new double[] { Double.parseDouble(numbers[0]),
							Double.parseDouble(numbers[1]) };
					transition(values, null, type);
				} else if (numbers.length == 1) {
					// valor
					values = new double[] { Double.parseDouble(numbers[0]) };
					transition(values[0]);
				}
			} catch (NumberFormatException e) {
				transition(text);
			} catch (NullPointerException e) {
				transition(text);
			}
			// }
		}
		getMapControl().repaint();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void configureMenu() {
		String[] desc = ((CADTool) cadToolStack.peek()).getDescriptions();
		// String[] labels = ((CADTool)
		// cadToolStack.peek()).getCurrentTransitions();
		CADExtension.clearMenu();

		for (int i = 0; i < desc.length; i++) {
			if (desc[i] != null) {
				CADExtension
						.addMenuEntry(PluginServices.getText(this, desc[i]));// ,
				// labels[i]);
			}
		}

	}

	/**
	 * Recibe los valores de la transición (normalmente un punto) y el evento
	 * con el que se generó (si fue de ratón será MouseEvent, el que viene en el
	 * pressed) y si es de teclado, será un KeyEvent. Del evento se puede sacar
	 * información acerca de si estaba pulsada la tecla CTRL, o Alt, etc.
	 *
	 * @param values
	 * @param event
	 */
	private void transition(double[] values, InputEvent event, int type) {
		questionAsked = true;
		if (!cadToolStack.isEmpty()) {
			CADTool ct = (CADTool) cadToolStack.peek();

			switch (type) {
			case ABSOLUTE:
				ct.transition(values[0], values[1], event);
				previousPoint = values;
				break;
			case RELATIVE_SCU:
				// Comprobar que tenemos almacenado el punto anterior
				// y crear nuevo con coordenadas relativas a él.
				double[] auxSCU = values;
				if (previousPoint != null) {
					auxSCU[0] = previousPoint[0] + values[0];
					auxSCU[1] = previousPoint[1] + values[1];
				}
				ct.transition(auxSCU[0], auxSCU[1], event);

				previousPoint = auxSCU;
				break;
			case RELATIVE_SCP:
				// TODO de momento no implementado.
				ct.transition(values[0], values[1], event);
				previousPoint = values;
				break;
			case POLAR_SCU:
				// Comprobar que tenemos almacenado el punto anterior
				// y crear nuevo con coordenadas relativas a él.
				double[] auxPolarSCU = values;
				if (previousPoint != null) {
					Point2D point = UtilFunctions.getPoint(new Point2D.Double(
							previousPoint[0], previousPoint[1]), Math
							.toRadians(values[1]), values[0]);
					auxPolarSCU[0] = point.getX();
					auxPolarSCU[1] = point.getY();
					ct.transition(auxPolarSCU[0], auxPolarSCU[1], event);
				} else {
					Point2D point = UtilFunctions.getPoint(new Point2D.Double(
							0, 0), Math.toRadians(values[1]), values[0]);
					auxPolarSCU[0] = point.getX();
					auxPolarSCU[1] = point.getY();
					ct.transition(auxPolarSCU[0], auxPolarSCU[1], event);
				}
				previousPoint = auxPolarSCU;
				break;
			case POLAR_SCP:
				double[] auxPolarSCP = values;
				if (previousPoint != null) {
					Point2D point = UtilFunctions.getPoint(new Point2D.Double(
							previousPoint[0], previousPoint[1]), values[1],
							values[0]);
					auxPolarSCP[0] = point.getX();
					auxPolarSCP[1] = point.getY();
					ct.transition(auxPolarSCP[0], auxPolarSCP[1], event);
				} else {
					Point2D point = UtilFunctions.getPoint(new Point2D.Double(
							0, 0), values[1], values[0]);
					auxPolarSCP[0] = point.getX();
					auxPolarSCP[1] = point.getY();
					ct.transition(auxPolarSCP[0], auxPolarSCP[1], event);
				}
				previousPoint = auxPolarSCP;
				break;
			default:
				break;
			}
			askQuestion();
		}
		configureMenu();
		PluginServices.getMainFrame().enableControls();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param text
	 *            DOCUMENT ME!
	 * @param source
	 *            DOCUMENT ME!
	 * @param sel
	 *            DOCUMENT ME!
	 * @param values
	 *            DOCUMENT ME!
	 */
	private void transition(double value) {
		questionAsked = true;
		if (!cadToolStack.isEmpty()) {
			CADTool ct = (CADTool) cadToolStack.peek();
			ct.transition(value);
			askQuestion();
		}
		configureMenu();
		PluginServices.getMainFrame().enableControls();
	}

	public void transition(String option) {
		questionAsked = true;
		if (!cadToolStack.isEmpty()) {
			CADTool ct = (CADTool) cadToolStack.peek();
			try {
				ct.transition(option);
			} catch (Exception e) {
				View vista = (View) PluginServices.getMDIManager()
						.getActiveView();
				vista.getConsolePanel().addText(
						"\n" + PluginServices.getText(this, "incorrect_option")
								+ " : " + option, JConsole.ERROR);
			}
			askQuestion();
		}
		configureMenu();
		PluginServices.getMainFrame().enableControls();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value
	 *            DOCUMENT ME!
	 */
	public void setGrid(boolean value) {
		getGrid().setUseGrid(value);
		getGrid().setViewPort(getMapControl().getViewPort());
		getMapControl().drawMap(false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param activated
	 *            DOCUMENT ME!
	 */
	public void setSnapping(boolean activated) {
		snapping = activated;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#getListener()
	 */
	public ToolListener getListener() {
		return new ToolListener() {
			/**
			 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
			 */
			public Cursor getCursor() {
				return null;
			}

			/**
			 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
			 */
			public boolean cancelDrawing() {
				return false;
			}
		};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CADTool getCadTool() {
		return (CADTool) cadToolStack.peek();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param cadTool
	 *            DOCUMENT ME!
	 */
	public void pushCadTool(CADTool cadTool) {
		cadToolStack.push(cadTool);
		cadTool.setCadToolAdapter(this);
		// cadTool.initializeStatus();
		// cadTool.setVectorialAdapter(vea);
		/*
		 * int ret = cadTool.transition(null, editableFeatureSource, selection,
		 * new double[0]);
		 *
		 * if ((ret & Automaton.AUTOMATON_FINISHED) ==
		 * Automaton.AUTOMATON_FINISHED) { popCadTool();
		 *
		 * if (cadToolStack.isEmpty()) { pushCadTool(new
		 * com.iver.cit.gvsig.gui.cad.smc.gen.CADTool());//new
		 * SelectionCadTool());
		 * PluginServices.getMainFrame().setSelectedTool("selection"); }
		 *
		 * askQuestion();
		 *
		 * getMapControl().drawMap(false); }
		 */
	}

	/**
	 * DOCUMENT ME!
	 */
	public void popCadTool() {
		cadToolStack.pop();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void askQuestion() {
		CADTool cadtool = (CADTool) cadToolStack.peek();
		/*
		 * if (cadtool..getStatus()==0){
		 * PluginServices.getMainFrame().addTextToConsole("\n"
		 * +cadtool.getName()); }
		 */
		View vista = (View) PluginServices.getMDIManager().getActiveView();
		vista.getConsolePanel().addText(
				"\n" + "#" + cadtool.getQuestion() + " > ", JConsole.MESSAGE);
		// ***PluginServices.getMainFrame().addTextToConsole("\n" +
		// cadtool.getQuestion());
		questionAsked = true;

	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param cadTool
	 *            DOCUMENT ME!
	 */
	public void setCadTool(CADTool cadTool) {
		cadToolStack.clear();
		pushCadTool(cadTool);
		// askQuestion();
	}


	/**
	 * Elimina las geometrías seleccionadas actualmente
	 */
	private void delete() {
		ILayerEdited aux = CADExtension.getEditionManager().getActiveLayerEdited();
		if (!(aux instanceof VectorialLayerEdited))
			return;
		VectorialLayerEdited vle = (VectorialLayerEdited) aux;
		VectorialEditableAdapter vea = vle.getVEA();

		vea.startComplexRow();
		FBitSet selection = vea.getSelection();
		try {
			int[] indexesToDel = new int[selection.cardinality()];
			int j = 0;
			for (int i = selection.nextSetBit(0); i >= 0; i = selection
					.nextSetBit(i + 1)) {
				indexesToDel[j++] = i;
				// /vea.removeRow(i);
			}
			/*
			 * VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
			 * .getEditionManager().getActiveLayerEdited(); ArrayList
			 * selectedRow = vle.getSelectedRow();
			 *
			 * int[] indexesToDel = new int[selectedRow.size()]; for (int i = 0;
			 * i < selectedRow.size(); i++) { IRowEdited edRow = (IRowEdited)
			 * selectedRow.get(i); indexesToDel[i] = edRow.getIndex(); }
			 */
			for (int i = indexesToDel.length - 1; i >= 0; i--) {
				vea.removeRow(indexesToDel[i], PluginServices.getText(this,
						"deleted_feature"),EditionEvent.GRAPHIC);
			}
		} catch (DriverIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				vea.endComplexRow();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (DriverIOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("clear Selection");
		selection.clear();
		vle.clearSelection();
		/*
		 * if (getCadTool() instanceof SelectionCADTool) { SelectionCADTool
		 * selTool = (SelectionCADTool) getCadTool(); selTool.clearSelection(); }
		 */
		getMapControl().drawMap(false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param b
	 */
	public void setAdjustGrid(boolean b) {
		getGrid().setAdjustGrid(b);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param actionCommand
	 */
	public void keyPressed(String actionCommand) {
		if (actionCommand.equals("eliminar")) {
			delete();
		} else if (actionCommand.equals("escape")) {
			if (getMapControl().getTool().equals("cadtooladapter")) {
				CADTool ct = (CADTool) cadToolStack.peek();
				ct.end();
				cadToolStack.clear();
				SelectionCADTool selCad = new SelectionCADTool();
				selCad.init();
				VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
						.getEditionManager().getActiveLayerEdited();
				vle.clearSelection();

				pushCadTool(selCad);
				// getVectorialAdapter().getSelection().clear();
				getMapControl().drawMap(false);
				PluginServices.getMainFrame().setSelectedTool("_selection");
				// askQuestion();
			} else {
				getMapControl().setPrevTool();
			}
		}

		PluginServices.getMainFrame().enableControls();

	}

	public CADGrid getGrid() {
		return cadgrid;
	}


}
