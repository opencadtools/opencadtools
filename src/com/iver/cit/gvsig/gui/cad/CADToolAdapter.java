package com.iver.cit.gvsig.gui.cad;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.prefs.Preferences;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiFrame.MainFrame;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.EditionManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener;
import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.iver.cit.gvsig.gui.preferences.SnapConfigPage;
import com.iver.cit.gvsig.layers.ILayerEdited;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.snapping.GeometriesSnappingVisitor;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapper;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapperGeometriesVectorial;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapperRaster;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapperVectorial;
import com.iver.cit.gvsig.project.documents.view.snapping.SnappingVisitor;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;
import com.iver.utiles.console.JConsole;
import com.vividsolutions.jts.geom.Envelope;

public class CADToolAdapter extends Behavior {
	private static HashMap namesCadTools = new HashMap();

	private EditionManager editionManager = new EditionManager();

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

	private ISymbol symbol = SymbologyFactory.createDefaultSymbolByShapeType(FConstant.SYMBOL_TYPE_POINT, Color.RED);

	private Point2D mapAdjustedPoint;

	private ISnapper usedSnap = null;

	private boolean questionAsked = false;

	private Point2D adjustedPoint;

	private boolean bRefent = true;

	private boolean bForceCoord = false;

	private CADGrid cadgrid = new CADGrid();

	private boolean bOrtoMode;

	private Color theTipColor = new Color(255, 255, 155);

	private Object lastQuestion;

	private static boolean flatnessInitialized=false;
	private static Preferences prefs = Preferences.userRoot().node( "cadtooladapter" );
	private StatusBarListener sbl=null;
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.IBehavior#setMapControl(com.iver.cit.gvsig.fmap.MapControl)
	 */
	public void setMapControl(MapControl mc) {
		super.setMapControl(mc);
		sbl=new StatusBarListener(getMapControl());
	}
	/**
	 * Pinta de alguna manera especial las geometrias seleccionadas para la
	 * edición. En caso de que el snapping esté activado, pintará el efecto del
	 * mismo.
	 *
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (CADExtension.getCADToolAdapter()!=this)
			return;
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

		if (!isRefentEnabled())
			return Double.MAX_VALUE;

		ILayerEdited aux = CADExtension.getEditionManager().getActiveLayerEdited();
		if (!(aux instanceof VectorialLayerEdited))
			return Double.MAX_VALUE;
		VectorialLayerEdited vle = (VectorialLayerEdited) aux;

		ArrayList snappers = vle.getSnappers();
		ArrayList layersToSnap = vle.getLayersToSnap();


		ViewPort vp = getMapControl().getViewPort();

		snappers=SnapConfigPage.getActivesSnappers();

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
				// La lista de snappers está siempre ordenada por prioridad. Los de mayor
				// prioridad están primero.
				for (int i = 0; i < snappers.size(); i++)
				{
					ISnapper theSnapper = (ISnapper) snappers.get(i);

					if (usedSnap != null)
					{
						// Si ya tenemos un snap y es de alta prioridad, cogemos ese. (A no ser que en otra capa encontremos un snapper mejor)
						if (theSnapper.getPriority() < usedSnap.getPriority())
							break;
					}
					SnappingVisitor snapVisitor = null;
					Point2D theSnappedPoint = null;

					if (theSnapper instanceof ISnapperVectorial)
					{
						if (theSnapper instanceof ISnapperGeometriesVectorial) {
							snapVisitor=new GeometriesSnappingVisitor((ISnapperGeometriesVectorial) theSnapper,point,mapTolerance,lastPoint);
						}else {
							snapVisitor = new SnappingVisitor((ISnapperVectorial) theSnapper, point, mapTolerance, lastPoint);
						}
						// System.out.println("Cache size = " + cache.size());
						cache.query(e, snapVisitor);
						theSnappedPoint = snapVisitor.getSnapPoint();

					}
					if (theSnapper instanceof ISnapperRaster)
					{
						ISnapperRaster snapRaster = (ISnapperRaster) theSnapper;
						theSnappedPoint = snapRaster.getSnapPoint(getMapControl(), point, mapTolerance, lastPoint);
					}


					if (theSnappedPoint != null) {
						double distAux = theSnappedPoint.distance(point);
						if (minDist > distAux)
						{
							minDist = distAux;
							usedSnap = theSnapper;
							mapHandlerAdjustedPoint.setLocation(theSnappedPoint);
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

		showCoords(e.getPoint());

		getMapControl().repaint();
	}

	private void showCoords(Point2D pPix)
	{
		String[] axisText = new String[2];
		axisText[0] = "X = ";
		axisText[1] = "Y = ";
//		NumberFormat nf = NumberFormat.getInstance();
		MapControl mapControl = getMapControl();
		ViewPort vp = mapControl.getMapContext().getViewPort();
		IProjection iProj = vp.getProjection();

//		if (iProj.getAbrev().equals("EPSG:4326") || iProj.getAbrev().equals("EPSG:4230")) {
//			axisText[0] = "Lon = ";
//			axisText[1] = "Lat = ";
//			nf.setMaximumFractionDigits(8);
//		} else {
//			axisText[0] = "X = ";
//			axisText[1] = "Y = ";
//			nf.setMaximumFractionDigits(2);
//		}
		Point2D p;
		if (mapAdjustedPoint == null)
		{
			p = vp.toMapPoint(pPix);
		}
		else
		{
			p = mapAdjustedPoint;
		}
		sbl.setFractionDigits(p);
		axisText = sbl.setCoorDisplayText(axisText);
		MainFrame mF = PluginServices.getMainFrame();

		if (mF != null)
		{
            mF.getStatusBar().setMessage("units",
            		PluginServices.getText(this, FConstant.NAMES[vp.getDistanceUnits()]));
            mF.getStatusBar().setControlValue("scale",String.valueOf(mapControl.getMapContext().getScaleView()));
			mF.getStatusBar().setMessage("projection", iProj.getAbrev());

			String[] coords=sbl.getCoords(p);
			mF.getStatusBar().setMessage("x",
					axisText[0] + coords[0]);
			mF.getStatusBar().setMessage("y",
					axisText[1] + coords[1]);
		}
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

		// getMapControl().setToolTipText(null);
		if (adjustedPoint != null) {
			if (bForceCoord) {
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

					Graphics2D g2 = (Graphics2D) g;
			        FontMetrics metrics = g2.getFontMetrics();
			        int w = metrics.stringWidth(usedSnap.getToolTipText()) + 5;
			        int h = metrics.getMaxAscent() + 5;
			        int x = (int)p.getX()+9;
			        int y = (int)p.getY()- 7;

			        g2.setColor(theTipColor );
			        g2.fillRect(x, y-h, w, h);
			        g2.setColor(Color.BLACK);
			        g2.drawRect(x, y-h, w, h);
					g2.drawString(usedSnap.getToolTipText(), x+3, y-3);


					// getMapControl().setToolTipText(usedSnap.getToolTipText());
				}

				bForceCoord = false;
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
			bForceCoord = true;
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
			transition(PluginServices.getText(this,"cancel"));
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
				IWindow window = PluginServices.getMDIManager().getActiveWindow();

				if (window instanceof View) {
					((View)window).getConsolePanel().addText(
							"\n" + PluginServices.getText(this, "incorrect_option")
							+ " : " + option, JConsole.ERROR);
				}
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
	public void setGridVisibility(boolean value) {
		getGrid().setShowGrid(value);
		getGrid().setViewPort(getMapControl().getViewPort());
		getMapControl().repaint();
	}

	public void setRefentEnabled(boolean activated) {
		bRefent = activated;
	}

	public boolean isRefentEnabled()
	{
		return bRefent;
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
		if (PluginServices.getMDIManager().getActiveWindow() instanceof View)
		{
			View vista = (View) PluginServices.getMDIManager().getActiveWindow();
			String question=cadtool.getQuestion();
			if (lastQuestion==null || !(lastQuestion.equals(question)) || questionAsked) {
			vista.getConsolePanel().addText(
					"\n" + "#" + question + " > ", JConsole.MESSAGE);
			// ***PluginServices.getMainFrame().addTextToConsole("\n" +
			// cadtool.getQuestion());
			questionAsked = false;
			}
			lastQuestion=question;
		}

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
		try {
			FBitSet selection = vea.getSelection();
			int[] indexesToDel = new int[selection.cardinality()];
			int j = 0;
			for (int i = selection.nextSetBit(0); i >= 0; i = selection
					.nextSetBit(i + 1)) {
				indexesToDel[j++] = i;
				// /vea.removeRow(i);
			}

//			  ArrayList selectedRow = vle.getSelectedRow();
//
//			  int[] indexesToDel = new int[selectedRow.size()];
//			  for (int i = 0;i < selectedRow.size(); i++) {
//				  IRowEdited edRow = (IRowEdited) selectedRow.get(i);
//				  indexesToDel[i] = vea.getInversedIndex(edRow.getIndex());
//				  }
//
			for (int i = indexesToDel.length - 1; i >= 0; i--) {
				vea.removeRow(indexesToDel[i], PluginServices.getText(this,
						"deleted_feature"),EditionEvent.GRAPHIC);
			}
			System.out.println("clear Selection");
			selection.clear();
			vle.clearSelection(VectorialLayerEdited.NOTSAVEPREVIOUS);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (ExpansionFileReadException e) {
			NotificationManager.addError(e.getMessage(),e);
		} finally {
			String description=PluginServices.getText(this,"remove_geometry");
			vea.endComplexRow(description);
		}


		/*
		 * if (getCadTool() instanceof SelectionCADTool) { SelectionCADTool
		 * selTool = (SelectionCADTool) getCadTool(); selTool.clearSelection(); }
		 */
		refreshEditedLayer();
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
		if (CADExtension.getEditionManager().getActiveLayerEdited()== null) {
			return;
		}
		if (actionCommand.equals("eliminar")) {
			delete();
		} else if (actionCommand.equals("escape")) {
			if (getMapControl().getCurrentTool().equals("cadtooladapter")) {
				CADTool ct = (CADTool) cadToolStack.peek();
				ct.end();
				cadToolStack.clear();
				SelectionCADTool selCad = new SelectionCADTool();
				selCad.init();
				VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
						.getEditionManager().getActiveLayerEdited();
				try {
					vle.clearSelection(VectorialLayerEdited.NOTSAVEPREVIOUS);
				} catch (ReadDriverException e) {
					NotificationManager.addError(e.getMessage(),e);
				}

				pushCadTool(selCad);
				// getVectorialAdapter().getSelection().clear();

				refreshEditedLayer();


				PluginServices.getMainFrame().setSelectedTool("_selection");
				// askQuestion();
			} else {
				getMapControl().setPrevTool();
			}
		}

		PluginServices.getMainFrame().enableControls();

	}

	/**
	 * Provoca un repintado "soft" de la capa activa en edición.
	 * Las capas por debajo de ella no se dibujan de verdad, solo
	 * se dibuja la que está en edición y las que están por encima
	 * de ella en el TOC.
	 */
	public void refreshEditedLayer()
	{
		ILayerEdited edLayer = CADExtension.getEditionManager().getActiveLayerEdited();
		if (edLayer != null)
		{
			edLayer.getLayer().setDirty(true);
			getMapControl().rePaintDirtyLayers();
		}

	}

	public CADGrid getGrid() {
		return cadgrid;
	}

	public boolean isOrtoMode() {
		return bOrtoMode;
	}

	public void setOrtoMode(boolean b) {
		bOrtoMode = b;
	}

	public static void addCADTool(String name, CADTool c) {
		namesCadTools.put(name, c);

	}
	public static CADTool[] getCADTools() {
		return (CADTool[]) CADToolAdapter.namesCadTools.values().toArray(new CADTool[0]);
	}
	public CADTool getCADTool(String text) {
		CADTool ct = (CADTool) namesCadTools.get(text);
		return ct;
	}

	public EditionManager getEditionManager() {
		return editionManager;
	}

	public void initializeFlatness() {
		if (!flatnessInitialized){
			flatnessInitialized=true;
			Preferences prefs = Preferences.userRoot().node( "cadtooladapter" );
			double flatness = prefs.getDouble("flatness",FConverter.FLATNESS);
			FConverter.FLATNESS=flatness;
		}
	}
	public void initializeGrid(){
		boolean showGrid = prefs.getBoolean("grid.showgrid",getGrid().isShowGrid());
		boolean adjustGrid = prefs.getBoolean("grid.adjustgrid",getGrid().isAdjustGrid());

		double dx = prefs.getDouble("grid.distancex",getGrid().getGridSizeX());
		double dy = prefs.getDouble("grid.distancey",getGrid().getGridSizeY());

		setGridVisibility(showGrid);
		setAdjustGrid(adjustGrid);
		getGrid().setGridSizeX(dx);
		getGrid().setGridSizeY(dy);
	}
	/**
	* Returns the type of active layer.
	**/
	public int getActiveLayerType() {
		int type=FShape.MULTI;
		try {
			type=((FLyrVect)CADExtension.getEditionManager().getActiveLayerEdited().getLayer()).getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}
		return type;
	}
}
