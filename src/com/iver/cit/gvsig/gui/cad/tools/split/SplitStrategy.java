
/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */


/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
/* CVS MESSAGES:
*
* $Id: 
* $Log: 
*/
package com.iver.cit.gvsig.gui.cad.tools.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geomgraph.DirectedEdge;
import com.vividsolutions.jts.geomgraph.Node;


/**
 * This class is based in the AXIOS team work for UDIG project.
 * 
 * We have adapted it slightly for the gvsig project
 * (exchange the I18 system, etc)
 * 
 * @author Alvaro Zabala
 *
 */

/*
 * Performs a split of a LineString, MultiLineString, Polygon or MultiPolygon using a provided
 * LineString as cutting edge.
 * 
 * @author Gabriel Roldán, Axios Engineering
 * @author Mauricio Pazos, Axios Engineering
 * @since 1.1.0
 */
public class SplitStrategy {

    private final LineString splittingLine;

    private static final Map /* <Class, Class<? extends SpecificSplitOp> */strategies;
    static {
        Map knownStrategies = new HashMap();
        knownStrategies.put(LineString.class, LineStringSplitter.class);
        knownStrategies.put(MultiLineString.class, MultiLineStringSplitter.class);
        knownStrategies.put(Polygon.class, PolygonSplitter.class);
        knownStrategies.put(MultiPolygon.class, MultiPolygonSplitter.class);

        strategies = Collections.unmodifiableMap(knownStrategies);
    }

    public SplitStrategy( final LineString splittingLine ) {
        if (splittingLine == null) {
            throw new NullPointerException();
        }
        this.splittingLine = splittingLine;
    }

    
    public static Geometry splitOp( Geometry geom, LineString splitLine ) {
        SplitStrategy op = new SplitStrategy(splitLine);
        Geometry splittedGeometries = op.split(geom);
        return splittedGeometries;
    }

    
    /**
     * @param splitee
     * @return a <code>Geometry</code> containing all the splitted parts as aggregates. Use
     *         {@link Geometry#getGeometryN(int) getGeometryN(int)} to get each part.
     * @throws NullPointerException if geom is null
     * @throws IllegalArgumentException if geom is not of an acceptable geometry type to be splitted
     *         (i.e. not a linestring, multilinestring, polygon or multipolygon)
     */
    public Geometry split( final Geometry splitee ) {
        if (splitee == null) {
            throw new NullPointerException();
        }

        Class spliteeClass = splitee.getClass();
        SpecificSplitOp splitOp = findSplitOp(spliteeClass);

        Geometry splitResult;

        splitOp.setSplitter(splittingLine);
        splitResult = splitOp.split(splitee);

        return splitResult;
    }

    
    private SpecificSplitOp findSplitOp( Class spliteeClass ) {
        if (!strategies.containsKey(spliteeClass)) {
            throw new IllegalArgumentException("La geometria especificada no soporta la operacion 'split'");
        }

        final Class splitOpClass = (Class) strategies.get(spliteeClass);
        SpecificSplitOp splitOp;
        try {

            splitOp = (SpecificSplitOp) splitOpClass.newInstance();

        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate " + splitOpClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw (RuntimeException) new RuntimeException("Illegal access exception for "
                    + splitOpClass.getName()).initCause(e);
        }
        return splitOp;
    }

    /**
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static interface SpecificSplitOp {
        public void setSplitter( LineString splitter );
        public Geometry split( Geometry splitee );
    }

    /**
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static abstract class AbstractSplitter implements SpecificSplitOp {

        protected LineString splitter;

        public void setSplitter( LineString splitter ) {
            this.splitter = splitter;
        }
    }

    /**
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static class LineStringSplitter extends AbstractSplitter {
        /**
         * No-op default constructor required to reflectively instantiate the class
         */
        public LineStringSplitter() {
            // no-op
        }
        /**
         * @param splitee the {@link LineString} to be splitted
         */
        public Geometry split( Geometry splitee ) {
            Geometry splitted = ((LineString) splitee).difference(splitter);
            return splitted;
        }

    }

    /**
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static abstract class AbstractGeometryCollectionSplitter implements SpecificSplitOp {

        private SpecificSplitOp singlePartSplitter;

        private AbstractGeometryCollectionSplitter( SpecificSplitOp singlePartSplitter ) {
            this.singlePartSplitter = singlePartSplitter;
        }

        public final void setSplitter( LineString splitter ) {
            singlePartSplitter.setSplitter(splitter);
        }

        public final Geometry split( final Geometry splitee ) {
            final GeometryCollection coll = (GeometryCollection) splitee;
            final int numParts = coll.getNumGeometries();

            List splittedParts = new ArrayList();

            for( int partN = 0; partN < numParts; partN++ ) {
                Geometry simplePartN = coll.getGeometryN(partN);
                Geometry splittedPart = singlePartSplitter.split(simplePartN);
                final int splittedPartsCount = splittedPart.getNumGeometries();
                for( int splittedPartN = 0; splittedPartN < splittedPartsCount; splittedPartN++ ) {
                    Geometry simpleSplittedPart = splittedPart.getGeometryN(splittedPartN);
                    splittedParts.add(simpleSplittedPart);
                }
            }

            GeometryFactory gf = splitee.getFactory();
            GeometryCollection splittedCollection = buildFromParts(gf, splittedParts);
            return splittedCollection;
        }

        protected abstract GeometryCollection buildFromParts( GeometryFactory gf, List parts );

    }

    /**
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static class MultiLineStringSplitter extends AbstractGeometryCollectionSplitter {

        public MultiLineStringSplitter() {
            super(new LineStringSplitter());
        }

        @Override
        protected GeometryCollection buildFromParts( GeometryFactory gf, List parts ) {
            LineString[] lines = (LineString[]) parts.toArray(new LineString[parts.size()]);
            MultiLineString result = gf.createMultiLineString(lines);
            return result;
        }
    }

    /**
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static class MultiPolygonSplitter extends AbstractGeometryCollectionSplitter {

        public MultiPolygonSplitter() {
            super(new PolygonSplitter());
        }

        @Override
        protected GeometryCollection buildFromParts( GeometryFactory gf, List parts ) {
            Polygon[] polygons = (Polygon[]) parts.toArray(new Polygon[parts.size()]);
            MultiPolygon result = gf.createMultiPolygon(polygons);
            return result;
        }
    }

    /**
     * Estrategia:
     * <ul>
     * <li> Construir un grafo con todos los edges y nodes de la intersección del polígono con la
     * línea
     * <li> Ponderar los nodos según la cantidad de edges incidentes. Nodos son solo las
     * intersecciones del boundary del poligono con el linestring y el punto inicial de cada parte
     * del boundary.
     * <li> Ponderar los edges según son shared (todos los del linestring) o non shared (todos los
     * del boundary del poligono). Almacenar la lista de coordenadas del edge en el edge.
     * <li> Comenzar a recorrer el grafo por un nodo cualquiera, empezando por su primer edge
     * <li> Recorrer siempre hacia el nodo siguiente, seleccionando el edge cuyo primer segmento de
     * su linestring presenta un menor angulo a la izquiera (CCW) con el ultimo segmento del
     * linestring del edge en curso.
     * <li> Eliminar del grafo los edges non-shared que se utilizaron
     * <li> Disminuir en 1 la ponderación de los nodos que se utilizaron
     * <li> Marcar los edges restantes que tengan algun nodo con ponderación < 3 como non-shared
     * <li> Eliminar del grafo los nodos con ponderación 1
     * </ul>
     * 
     * @author Gabriel Roldán, Axios Engineering
     * @author Mauricio Pazos, Axios Engineering
     * @since 1.1.0
     */
    private static class PolygonSplitter extends AbstractSplitter {

        /**
         * No-op default constructor required to reflectively instantiate the class
         */
        public PolygonSplitter() {
            // no-op
        }

        /**
         * 
         */
        public Geometry split( Geometry splitee ) {
            assert splitee instanceof Polygon;;

            final Polygon polygon = (Polygon) splitee;
            final Geometry splitted = splitPolygon(polygon);

            return splitted;
        }

        /**
         * 
         */
        private Geometry splitPolygon( final Polygon geom ) {
            SplitGraph graph = new SplitGraph(geom, splitter);

            final GeometryFactory gf = geom.getFactory();

            // sotore unsplitted holes for later addition
            List<LinearRing> unsplittedHoles = findUnsplittedHoles(graph, gf);

            List<List<SplitEdge>> allRings = findRings(graph);

            List<Polygon> resultingPolygons = buildSimplePolygons(allRings, unsplittedHoles, gf);

            Geometry result;
            if (resultingPolygons.size() == 1) {
                result = resultingPolygons.get(0);
            } else {
                Polygon[] array = resultingPolygons.toArray(new Polygon[resultingPolygons.size()]);
                result = gf.createMultiPolygon(array);
            }
            return result;
        }

        /**
         * Finds out and removes from the graph the edges that were originally holes in the polygon
         * and were not splitted by the splitting line.
         * 
         * @param graph
         * @param gf
         * @return
         */
        @SuppressWarnings("unchecked")
        private List<LinearRing> findUnsplittedHoles( SplitGraph graph, GeometryFactory gf ) {
            final List<LinearRing> unsplittedHoles = new ArrayList<LinearRing>(2);

            final List<SplitEdge> edges = new ArrayList<SplitEdge>();
            for( Iterator it = graph.getEdgeIterator(); it.hasNext(); ) {
                SplitEdge edge = (SplitEdge) it.next();
                edges.add(edge);
            }

            for( Iterator it = edges.iterator(); it.hasNext(); ) {
                SplitEdge edge = (SplitEdge) it.next();
                if (edge.isHoleEdge()) {
                    Coordinate[] coordinates = edge.getCoordinates();
                    Coordinate start = coordinates[0];
                    Coordinate end = coordinates[coordinates.length - 1];
                    boolean isLinearRing = start.equals2D(end);
                    if (isLinearRing) {
                        graph.remove(edge);
                        LinearRing ring = gf.createLinearRing(coordinates);
                        unsplittedHoles.add(ring);
                    }
                }
            }
            return unsplittedHoles;
        }

        private List<Polygon> buildSimplePolygons( List<List<SplitEdge>> allRings,
                                                   List<LinearRing> unsplittedHoles,
                                                   GeometryFactory gf ) {

            List<Polygon> polygons = new ArrayList<Polygon>(allRings.size());

            for( List<SplitEdge> edgeList : allRings ) {
                Polygon poly = buildPolygon(edgeList, gf);
                List<LinearRing> thisPolyHoles = new ArrayList<LinearRing>(unsplittedHoles.size());
                for( LinearRing holeRing : unsplittedHoles ) {
                    if (poly.covers(holeRing)) {
                        thisPolyHoles.add(holeRing);
                    }
                }
                unsplittedHoles.removeAll(thisPolyHoles);

                int numHoles = thisPolyHoles.size();
                if (numHoles > 0) {
                    LinearRing[] holes = thisPolyHoles.toArray(new LinearRing[numHoles]);
                    LinearRing shell = gf.createLinearRing(poly.getExteriorRing().getCoordinates());
                    poly = gf.createPolygon(shell, holes);
                }

                polygons.add(poly);
            }

            return polygons;
        }

        private Polygon buildPolygon( List<SplitEdge> edgeList, GeometryFactory gf ) {
            List<Coordinate> coords = new ArrayList<Coordinate>();
            Coordinate[] lastCoordinates = null;
            for( SplitEdge edge : edgeList ) {
                Coordinate[] coordinates = edge.getCoordinates();
                if (lastCoordinates != null) {
                    Coordinate endPoint = lastCoordinates[lastCoordinates.length - 1];
                    Coordinate startPoint = coordinates[0];
                    if (!endPoint.equals2D(startPoint)) {
                        coordinates = CoordinateArrays.copyDeep(coordinates);
                        CoordinateArrays.reverse(coordinates);
                    }
                }
                lastCoordinates = coordinates;
                for( int i = 0; i < coordinates.length; i++ ) {
                    Coordinate coord = coordinates[i];
                    coords.add(coord);
                }
            }
            Coordinate[] shellCoords = new Coordinate[coords.size()];
            coords.toArray(shellCoords);
            shellCoords = CoordinateArrays.removeRepeatedPoints(shellCoords);
            LinearRing shell = gf.createLinearRing(shellCoords);
            Polygon poly = gf.createPolygon(shell, (LinearRing[]) null);
            return poly;
        }

        /**
         * Builds a list of rings from the graph's edges
         * 
         * @param graph
         * @return
         */
        @SuppressWarnings("unchecked")
        private List<List<SplitEdge>> findRings( SplitGraph graph ) {
            final List<List<SplitEdge>> rings = new ArrayList<List<SplitEdge>>();

            DirectedEdge startEdge;
            // build each ring starting with the first edge belonging to the
            // shell found
            while( (startEdge = findShellEdge(graph)) != null ) {
                List<SplitEdge> ring = buildRing(graph, startEdge);
                rings.add(ring);
            }
            return rings;
        }

        private List<SplitEdge> buildRing( final SplitGraph graph, final DirectedEdge startEdge ) {
            // System.out.println("building ring edge list...");
            final List<SplitEdge> ring = new ArrayList<SplitEdge>();

            // follow this tessellation direction while possible,
            // switch to the opposite when not, and continue with
            // the same direction while possible.
            // Start travelling clockwise, as we start with a shell edge,
            // which is in clockwise order
            final int direction = CGAlgorithms.COUNTERCLOCKWISE;

            DirectedEdge currentDirectedEdge = startEdge;
            DirectedEdge nextDirectedEdge = null;

            while( nextDirectedEdge != startEdge ) {
                SplitEdge edge = (SplitEdge) currentDirectedEdge.getEdge();
                // System.out.println("adding " + edge);
                if (ring.contains(edge)) {
                    throw new IllegalStateException("trying to add edge twice: " + edge);
                }
                ring.add(edge);

                DirectedEdge sym = currentDirectedEdge.getSym();
                SplitGraphNode endNode = (SplitGraphNode) sym.getNode();
                SplitEdgeStar nodeEdges = (SplitEdgeStar) endNode.getEdges();
                nextDirectedEdge = nodeEdges.findClosestEdgeInDirection(sym, direction);

                assert nextDirectedEdge != null;

                currentDirectedEdge = nextDirectedEdge;
            }

            removeUnneededEdges(graph, ring);
            return ring;
        }

        /**
         * Removes from <code>graph</code> the edges in <code>ring</code> that are no more
         * needed
         * 
         * @param graph
         * @param ring
         */
        private void removeUnneededEdges( final SplitGraph graph, final List<SplitEdge> ring ) {
            for( SplitEdge edge : ring ) {
                if (!edge.isInteriorEdge()) {
                    graph.remove(edge);
                }
            }

            for( SplitEdge edge : ring ) {
                if (edge.isInteriorEdge()) {
                    Node node = graph.find(edge.getCoordinate());
                    int degree = node.getEdges().getDegree();
                    if (degree < 2) {
                        graph.remove(edge);
                    }
                }
            }
        }

        /**
         * Returns the first edge found that belongs to the shell (not an interior edge, not one of
         * a hole boundary)
         * <p>
         * This method relies on shell edges being labeled {@link Location#EXTERIOR exterior} to the
         * left and {@link Location#INTERIOR interior} to the right.
         * </p>
         * 
         * @param graph
         * @return the first shell edge found, or <code>null</code> if there are no more shell
         *         edges in <code>graph</code>
         */
        private DirectedEdge findShellEdge( SplitGraph graph ) {
            Iterator it = graph.getEdgeEnds().iterator();
            DirectedEdge firstShellEdge = null;
            while( it.hasNext() ) {
                DirectedEdge de = (DirectedEdge) it.next();
                SplitEdge edge = (SplitEdge) de.getEdge();
                if (edge.isShellEdge()) {
                    firstShellEdge = de;
                    break;
                }
            }
            return firstShellEdge;
        }
    }
}
