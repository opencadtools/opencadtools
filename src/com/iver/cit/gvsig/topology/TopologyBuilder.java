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
package com.iver.cit.gvsig.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.util.LinearComponentExtracter;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.planargraph.NodeMap;

/**
 * @author fjp
 * 
 * This class do its job thanks to the work of Martin Davis in
 * JTS (Java Topology Suite).
 * The code is extracted from JCS (Java Conflation Suite), made by
 * Vivid Solutions (www.vividsolutions.com)
 *
 */
public class TopologyBuilder {
	private double fuzzyTolerance;
	private double dangleTolerance;
	private FLyrVect inputLayer;
	private FLyrVect outputLayer;
	private String statusMessage;
	private GeometryFactory fact = new GeometryFactory();
	private boolean nodeInputLines = false;
	private Polygonizer polygonizer = new Polygonizer();
	
	
	public TopologyBuilder(double fuzzy, double dangle)
	{
		fuzzyTolerance = fuzzy;
		dangleTolerance = dangle;
	}
	
	private Collection getLines(FLyrVect lv) throws DriverException, DriverIOException
	  {
	    List linesList = new ArrayList();
	    LinearComponentExtracter lineFilter = new LinearComponentExtracter(linesList);
		FBitSet bitSet = lv.getRecordset().getSelection();
		
		for(int i=bitSet.nextSetBit(0); i>=0; i=bitSet.nextSetBit(i+1)) {
			IGeometry g = lv.getSource().getShape(i);
			Geometry jtsGeom = g.toJTSGeometry();
			jtsGeom.apply(lineFilter);
		}

	    return linesList;
	  }

	  private Collection nodeLines(List lines)
	  {

	    Geometry linesGeom = fact.createMultiLineString(GeometryFactory.toLineStringArray(lines));
	    Geometry empty = fact.createMultiLineString(null);
	    Geometry noded = linesGeom.union(empty);
	    List nodedList = new ArrayList();
	    nodedList.add(noded);
	    return nodedList;
	  }

	
	public Collection buildPolygons() throws DriverException
	{
	    // monitor.report("Polygonizing...");
		if (inputLayer == null)
			throw new RuntimeException("Please, set inputLayer before buildPolygons");
		
	    statusMessage = "Polygonizing...";

	    Collection lines;
		try {
			lines = getLines(inputLayer);
	
		    Collection nodedLines = lines;
		    if (nodeInputLines) {
		    	statusMessage = "Noding input lines";
		    	nodedLines = nodeLines((List) lines);
		    }
	
		    for (Iterator i = nodedLines.iterator(); i.hasNext(); ) {
		      Geometry g = (Geometry) i.next();
		      polygonizer.add(g);
		    }		    
		    return polygonizer.getPolygons(); 
		} catch (DriverIOException e) {
			e.printStackTrace();
			throw new DriverException(e);
		}
	}
	public void buildLines()
	{
		
	}
	
	public List getErrorNodes() throws DriverException
	{
		NodeMap nodeMap = new NodeMap();
		FBitSet bitSet = inputLayer.getRecordset().getSelection();
		
		statusMessage = "Creando grafo...";
		
		for(int i=bitSet.nextSetBit(0); i>=0; i=bitSet.nextSetBit(i+1)) {
			statusMessage = "Procesando registro " + i;
			IGeometry g;
			try {
				g = inputLayer.getSource().getShape(i);
			} catch (DriverIOException e) {
				e.printStackTrace();
				throw new DriverException(e);
			}
			Geometry jtsG = g.toJTSGeometry();
			Coordinate[] coords = jtsG.getCoordinates();
		    if (jtsG.isEmpty()) 
		    	continue;
		    Coordinate[] linePts = CoordinateArrays.removeRepeatedPoints(coords);
		    Coordinate startPt = linePts[0];
		    Coordinate endPt = linePts[linePts.length - 1];

		    NodeError nStart = (NodeError) nodeMap.find(startPt);
		    NodeError nEnd = (NodeError) nodeMap.find(endPt);
		    if (nStart == null)
		    {
		    	nStart = new NodeError(startPt);
		    	nodeMap.add(nStart);
		    }
		    else
		    	nStart.setOccurrences(nStart.getOccurrences()+1);
		    if (nEnd == null)
		    {
		    	nEnd = new NodeError(endPt);
		    	nodeMap.add(nEnd);
		    }
		    else
		    	nEnd.setOccurrences(nEnd.getOccurrences()+1);

		}
		
		// Ahora recorremos todos los nodos y los que solo hayan sido
		// añadidos una vez, son dangle o fuzzy.
		// TODO: Poner una tolerancia para que las coordinate cercanas
		// formen un solo nodo.
		statusMessage = "Extrayendo fuzzy y dangle nodes...";
		ArrayList nodeErrors = new ArrayList();
		Iterator it = nodeMap.iterator();
		while (it.hasNext())
		{
			NodeError node = (NodeError) it.next();
			if (node.getOccurrences() == 1)
			{				
				FPoint2D p = FConverter.coordinate2FPoint2D(node.getCoordinate());
				IGeometry gAux = ShapeFactory.createPoint2D(p);
				nodeErrors.add(gAux);

				
			}
		}
		return nodeErrors;

	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public boolean isNodeInputLines() {
		return nodeInputLines;
	}

	public void setNodeInputLines(boolean nodeInputLines) {
		this.nodeInputLines = nodeInputLines;
	}

	public Collection getCutEdges() {
		return polygonizer.getCutEdges();
	}

	public Collection getDangles() {
		return polygonizer.getDangles();
	}

	public FLyrVect getInputLayer() {
		return inputLayer;
	}

	public void setInputLayer(FLyrVect inputLayer) {
		this.inputLayer = inputLayer;
	}

	public FLyrVect getOutputLayer() {
		return outputLayer;
	}

	public void setOutputLayer(FLyrVect outputLayer) {
		this.outputLayer = outputLayer;
	}

}


