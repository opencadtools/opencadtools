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
package com.iver.cit.gvsig;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.gui.View;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.planargraph.Node;
import com.vividsolutions.jts.planargraph.NodeMap;

/**
 * @author fjp
 * Primera prueba acerca de la creación de polígonos a partir de una
 * capa de líneas
 *
 */
public class TopologyExtension extends Extension {
	
	
	private class MyNode extends Node
	{
		public MyNode(Coordinate pt) {
			super(pt);
			occurrences = 1;
		}

		int occurrences;

		public int getOccurrences() {
			return occurrences;
		}

		public void setOccurrences(int occurrences) {
			this.occurrences = occurrences;
		}
		
	}
	
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mc = v.getMapControl();
		FLayer[] actives = mc.getMapContext().getLayers().getActives();
		for (int i = 0; i < actives.length; i++) {
			if (actives[i] instanceof FLyrVect) {
				FLyrVect lv = (FLyrVect) actives[i];
				if (s.compareTo("CLEAN") == 0)					
					doClean(lv);
				if (s.compareTo("SHOW_ERRORS") == 0)
					try {
						doShowNodeErrors(lv);
					} catch (DriverException e) {
						e.printStackTrace();
						NotificationManager.addError(e);
					} catch (DriverIOException e) {
						e.printStackTrace();
						NotificationManager.addError(e);
					}
				
			}
		}
		

	}


	/**
	 * @param lv FLayerVect de líneas para convertir a polígonos.
	 */
	private void doClean(FLyrVect lv) {
		Polygonizer polygonizer = new Polygonizer();
		
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mc = v.getMapControl();

		
		try {
			FBitSet bitSet = lv.getRecordset().getSelection();
			
			// First, we need to do "noding"
			// TODO: This step must be optional
			ArrayList lineStrings = new ArrayList();
			for(int i=bitSet.nextSetBit(0); i>=0; i=bitSet.nextSetBit(i+1)) {
				IGeometry g = lv.getSource().getShape(i);
				lineStrings.add(g.toJTSGeometry());				 
			}

			Geometry nodedLineStrings = (Geometry) lineStrings.get(0);
			for (int i = 1; i < lineStrings.size(); i++) {
				nodedLineStrings = nodedLineStrings.union((Geometry)lineStrings.get(i));
			}
			
			// FIN noding 
			

			polygonizer.add(nodedLineStrings);				 
			Collection polygons = polygonizer.getPolygons();
			Iterator it = polygons.iterator();
			GraphicLayer graphicLayer = mc.getMapContext().getGraphicsLayer();
			int idSymbolPol = graphicLayer.addSymbol(new FSymbol(FShape.POLYGON));
			int idSymbolCutEdge = graphicLayer.addSymbol(new FSymbol(FShape.LINE, Color.BLUE));
			int idSymbolDangle = graphicLayer.addSymbol(new FSymbol(FShape.LINE, Color.RED));
			while (it.hasNext())
			{
				Polygon pol = (Polygon) it.next();
				IGeometry gAux = FConverter.jts_to_igeometry(pol);
				FGraphic graphic = new FGraphic(gAux, idSymbolPol);
				graphicLayer.addGraphic(graphic);
			}
			
			// LINEAS QUE PARTEN POLIGONOS
			Collection cutEdges = polygonizer.getCutEdges(); 
			it = cutEdges.iterator(); 
			while (it.hasNext())
			{
				LineString lin = (LineString) it.next();
				IGeometry gAux = FConverter.jts_to_igeometry(lin);
				FGraphic graphic = new FGraphic(gAux, idSymbolCutEdge);
				graphicLayer.addGraphic(graphic);
			}
			
			// LINEAS COLGANTES, QUE NO FORMAN POLIGONO
			Collection dangles = polygonizer.getDangles(); 
			it = dangles.iterator(); 
			while (it.hasNext())
			{
				LineString lin = (LineString) it.next();
				IGeometry gAux = FConverter.jts_to_igeometry(lin);
				FGraphic graphic = new FGraphic(gAux, idSymbolDangle);
				graphicLayer.addGraphic(graphic);
			}
			
			
			mc.drawGraphics();
		} catch (DriverException e) {
			e.printStackTrace();
			NotificationManager.addError(e);
		} catch (DriverIOException e) {
			e.printStackTrace();
			NotificationManager.addError(e);
		}
		
	}
	
	/**
	 * We search for origin-endpoints in LineString. Each one will
	 * generate a Node. We also fill a map Node-numOccurrences.
	 * Dangle and Fuzzy nodes will be those that have an occurrence
	 * count = 1. (Node with degree cero in graph's language)
	 * @param lyr
	 * @throws DriverException 
	 * @throws DriverIOException 
	 */
	private void doShowNodeErrors(FLyrVect lv) throws DriverException, DriverIOException
	{
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mc = v.getMapControl();

//		ArrayList nodeErrors = new ArrayList();
		NodeMap nodeMap = new NodeMap();
		FBitSet bitSet = lv.getRecordset().getSelection();
		
		for(int i=bitSet.nextSetBit(0); i>=0; i=bitSet.nextSetBit(i+1)) {
			IGeometry g = lv.getSource().getShape(i);
			Geometry jtsG = g.toJTSGeometry();
			Coordinate[] coords = jtsG.getCoordinates();
		    if (jtsG.isEmpty()) 
		    	continue;
		    Coordinate[] linePts = CoordinateArrays.removeRepeatedPoints(coords);
		    Coordinate startPt = linePts[0];
		    Coordinate endPt = linePts[linePts.length - 1];

		    MyNode nStart = (MyNode) nodeMap.find(startPt);
		    MyNode nEnd = (MyNode) nodeMap.find(endPt);
		    if (nStart == null)
		    {
		    	nStart = new MyNode(startPt);
		    	nodeMap.add(nStart);
		    }
		    else
		    	nStart.setOccurrences(nStart.getOccurrences()+1);
		    if (nEnd == null)
		    {
		    	nEnd = new MyNode(endPt);
		    	nodeMap.add(nEnd);
		    }
		    else
		    	nEnd.setOccurrences(nEnd.getOccurrences()+1);

		}
		
		// Ahora recorremos todos los nodos y los que solo hayan sido
		// añadidos una vez, son dangle o fuzzy.
		// TODO: Poner una tolerancia para que las coordinate cercanas
		// formen un solo nodo.
		GraphicLayer graphicLayer = mc.getMapContext().getGraphicsLayer();
		int idSymbolPoint = graphicLayer.addSymbol(new FSymbol(FShape.POINT));
		
		Iterator it = nodeMap.iterator();
		while (it.hasNext())
		{
			MyNode node = (MyNode) it.next();
			if (node.getOccurrences() == 1)
			{				
				FPoint2D p = FConverter.coordinate2FPoint2D(node.getCoordinate());
				IGeometry gAux = ShapeFactory.createPoint2D(p);
				FGraphic graphic = new FGraphic(gAux, idSymbolPoint);
				graphicLayer.addGraphic(graphic);

				
			}
		}
		mc.drawGraphics();
	}
	

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mc = v.getMapControl();
		if (mc.getMapContext().getLayers().getActives().length > 0)
			return true;
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();
		if (v instanceof View)
			return true;
		return false;
	}

}


