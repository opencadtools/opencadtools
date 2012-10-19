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
package com.iver.cit.gvsig.project.documents.view.snapping;

import java.awt.Graphics;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

//se añaden dentro del metodo CADToolAdapter.adjustToHandler
/**
 * Este a clase implementa un snapper a las aristas de la geometria que ademas
 * permite recuperar la lista de puntos que hay dentro de la geometría desde el
 * último pulsado y el anterior
 * 
 * @author Jose Ignacio Lamas Fonte [LBD]
 * @author Nacho Varela [Cartolab]
 * 
 */
public class EIELNearestPointSnapper extends AbstractSnapper implements
	ISnapperVectorial {

    public static double UMBRAL_CERCANIA = 0.000001;

    private ArrayList listaPuntos;

    public EIELNearestPointSnapper() {
    }

    /**
     * Devuelve el punto, dentro de las aristas la geometría 'geom', más proximo
     * al punto 'point' siempre que la cercanía sea menor o igual a la toleracia
     * 'tolerance'. Ademas actualiza la lista de puntos que hay entre el
     * 'lastPointEntered' y el punto resultado.
     */
    public Point2D getSnapPoint(Point2D point, IGeometry geom,
	    double tolerance, Point2D lastPointEntered) {

	Point2D resul = null;

	Coordinate cUltimo = null;
	Coordinate cPoint = new Coordinate(point.getX(), point.getY());
	if (lastPointEntered != null) {
	    cUltimo = new Coordinate(lastPointEntered.getX(),
		    lastPointEntered.getY());
	}

	// aqui almacenaremos los indices del ultimo punto encontrado y del
	// punto mas pequeño que forme
	// parte del segmento en el que esta el nuevo punto encontrado
	int indiceUltimoPunto = -1;
	int indicePuntoEncontrado = -1;

	// encontramos el punto mas cercano al puntero del raton dentro de la
	// geometria
	// y ademas miramos si el ultimo punto esta tambien dentro de algun
	// segmento
	PathIterator theIterator = geom.getPathIterator(null,
		FConverter.FLATNESS); // polyLine.getPathIterator(null,
				      // flatness);
	double[] theData = new double[6];
	double minDist = tolerance;
	Coordinate from = null, first = null;

	// limpio la lista de puntos
	// listaPuntos = new ArrayList();

	// añado esto para ver si consigo tratar de forma coherente las
	// multigeometrias
	ArrayList listaPuntosAux = new ArrayList();
	boolean puntoEncontrado = false;
	boolean multiGeometriaTerminada = false;

	// cons esta variable controlaremos que se este haciendo snapin sobre
	// una liena abierta
	// o un poligono cerrado, ya que sobre la linea no podemos dar la vuelta
	// a los puntos
	// para buscar el camino mas corto
	boolean geometriaCerrada = false;

	while ((!theIterator.isDone()) && (!multiGeometriaTerminada)) {
	    // while not done
	    int theType = theIterator.currentSegment(theData);

	    switch (theType) {
	    case PathIterator.SEG_MOVETO:
		from = new Coordinate(theData[0], theData[1]);
		first = from;
		if (!puntoEncontrado) {
		    geometriaCerrada = false;
		    listaPuntosAux = new ArrayList();
		    indiceUltimoPunto = -1;
		    indicePuntoEncontrado = -1;
		    listaPuntosAux.add(new Point2D.Double(theData[0],
			    theData[1]));
		    // ahora comprobamos que no sean los puntos el primero
		    if (cPoint.distance(from) < minDist) {
			indicePuntoEncontrado = listaPuntosAux.size() - 1;
			resul = new Point2D.Double(theData[0], theData[1]);
			puntoEncontrado = true;
		    }
		    if (cUltimo != null) {
			if (cUltimo.equals(from)) {
			    indiceUltimoPunto = listaPuntosAux.size() - 1;
			}
		    }
		} else {
		    multiGeometriaTerminada = true;
		}
		break;

	    case PathIterator.SEG_LINETO:

		// System.out.println("SEG_LINETO");
		Coordinate to = new Coordinate(theData[0], theData[1]);
		LineSegment line = new LineSegment(from, to);
		Coordinate ultimoPuntoCercano = null;

		// comprobamos que el ultimo punto este dentro de la linea de
		// este tramo
		if (cUltimo != null) {
		    if (!cUltimo.equals(to)) {
			// metemos primero esto por si añade un nuevo punto
			ultimoPuntoCercano = line.closestPoint(cUltimo);
			if ((cUltimo.distance(ultimoPuntoCercano) < UMBRAL_CERCANIA)
				&& (!cUltimo.equals(from))) {
			    // añadimos el punto extra a la lista de puntos
			    listaPuntosAux
				    .add(new Point2D.Double(
					    ultimoPuntoCercano.x,
					    ultimoPuntoCercano.y));
			    indiceUltimoPunto = listaPuntosAux.size() - 1;
			}
		    }
		}
		// ahora debamos comprobar que el punto encontrado este dentro
		// de la linea de este tramo
		if ((!(cPoint.distance(to) < minDist))
			&& (!(cPoint.distance(from) < minDist))) {
		    ultimoPuntoCercano = line.closestPoint(cPoint);
		    if ((cPoint.distance(ultimoPuntoCercano) < minDist)) {
			listaPuntosAux.add(new Point2D.Double(
				ultimoPuntoCercano.x, ultimoPuntoCercano.y));
			indicePuntoEncontrado = listaPuntosAux.size() - 1;
			resul = new Point2D.Double(ultimoPuntoCercano.x,
				ultimoPuntoCercano.y);
			puntoEncontrado = true;
		    }
		}

		// ahora pasamos a comprobar si los puntos estan en los vertices
		if (cPoint.distance(to) < minDist) {
		    // le ponemos el indice como un numero mas ya que añadiremos
		    // luego el punto
		    indicePuntoEncontrado = listaPuntosAux.size();
		    resul = new Point2D.Double(theData[0], theData[1]);
		    puntoEncontrado = true;
		}
		if (cUltimo != null && cUltimo.equals(to)) {
		    // le ponemos el indice como un numero mas ya que añadiremos
		    // luego el punto
		    indiceUltimoPunto = listaPuntosAux.size();
		}
		listaPuntosAux.add(new Point2D.Double(theData[0], theData[1]));

		from = to;
		break;
	    case PathIterator.SEG_CLOSE:
		line = new LineSegment(from, first);
		geometriaCerrada = true;
		// metemos primero esto por si añade un nuevo punto
		if (cUltimo != null) {
		    // metemos primero esto por si añade un nuevo punto
		    ultimoPuntoCercano = line.closestPoint(cUltimo);
		    if ((cUltimo.distance(ultimoPuntoCercano) < UMBRAL_CERCANIA)
			    && (!cUltimo.equals(from))) {
			// añadimos el punto extra a la lista de puntos
			listaPuntosAux.add(new Point2D.Double(
				ultimoPuntoCercano.x, ultimoPuntoCercano.y));
			indiceUltimoPunto = listaPuntosAux.size() - 1;
		    }
		}
		// ahora debamos comprobar que el punto encontrado este dentro
		// de la linea de este tramo
		if (!(cPoint.distance(from) < minDist)) {
		    ultimoPuntoCercano = line.closestPoint(cPoint);
		    if ((cPoint.distance(ultimoPuntoCercano) < minDist)) {
			listaPuntosAux.add(new Point2D.Double(
				ultimoPuntoCercano.x, ultimoPuntoCercano.y));
			indicePuntoEncontrado = listaPuntosAux.size() - 1;
			resul = new Point2D.Double(ultimoPuntoCercano.x,
				ultimoPuntoCercano.y);
			puntoEncontrado = true;
		    }
		}
		from = first;
		break;

	    } // end switch

	    theIterator.next();
	}

	// ahora comprobaremos si el primer y el ultimo punto son el mismo
	// en cuyo caso borraremos el ultimo y actualizaremos los indices que
	// correspondan
	if (listaPuntosAux != null && listaPuntosAux.size() > 1) {
	    Point2D primerPunto = (Point2D) listaPuntosAux.get(0);
	    Point2D ultimoPunto = (Point2D) listaPuntosAux.get(listaPuntosAux
		    .size() - 1);
	    if (primerPunto.equals(ultimoPunto)) {
		if (indicePuntoEncontrado == listaPuntosAux.size() - 1) {
		    indicePuntoEncontrado = 0;
		}
		if (indiceUltimoPunto == listaPuntosAux.size() - 1) {
		    indiceUltimoPunto = 0;
		}
		listaPuntosAux.remove(listaPuntosAux.size() - 1);
		geometriaCerrada = true;
	    }
	}

	if (resul != null) {
	    if (indiceUltimoPunto >= 0 && indicePuntoEncontrado >= 0
		    && indicePuntoEncontrado != indiceUltimoPunto
		    && Math.abs(indicePuntoEncontrado - indiceUltimoPunto) > 1) {
		listaPuntos = new ArrayList();
		if (indiceUltimoPunto > indicePuntoEncontrado) {
		    if (((indiceUltimoPunto - indicePuntoEncontrado) <= (((listaPuntosAux
			    .size() - 1) - indiceUltimoPunto) + indicePuntoEncontrado))
			    || !geometriaCerrada) {
			for (int i = indiceUltimoPunto - 1; i >= indicePuntoEncontrado; i--) {
			    listaPuntos.add((Point2D) listaPuntosAux.get(i));
			}
		    } else {
			for (int i = indiceUltimoPunto + 1; i < listaPuntosAux
				.size(); i++) {
			    listaPuntos.add((Point2D) listaPuntosAux.get(i));
			}
			for (int i = 0; i <= indicePuntoEncontrado; i++) {
			    listaPuntos.add((Point2D) listaPuntosAux.get(i));
			}
		    }
		} else {
		    if (((indicePuntoEncontrado - indiceUltimoPunto) <= (((listaPuntosAux
			    .size() - 1) - indicePuntoEncontrado) + indiceUltimoPunto))
			    || !geometriaCerrada) {
			for (int i = indiceUltimoPunto + 1; i <= indicePuntoEncontrado; i++) {
			    listaPuntos.add((Point2D) listaPuntosAux.get(i));
			}
		    } else {
			for (int i = indiceUltimoPunto - 1; i >= 0; i--) {
			    listaPuntos.add((Point2D) listaPuntosAux.get(i));
			}
			for (int i = listaPuntosAux.size() - 1; i >= indicePuntoEncontrado; i--) {
			    listaPuntos.add((Point2D) listaPuntosAux.get(i));
			}
		    }
		}
	    }
	}
	return resul;
    }

    public String getToolTipText() {
	// return PluginServices.getText(this, "final_point");
	return "Arista seguimiento";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics,
     * java.awt.geom.Point2D)
     */
    public void draw(Graphics g, Point2D pPixels) {
	g.setColor(getColor());

	// para pintar puntos de las ccordenadas de mapa en pantalla usar esto
	// getMapControl().getViewPort().fromMapPoint(punto);

	// if(listaPuntos!=null && listaPuntos.size()>0){
	// for(int i=0; i<listaPuntos.size()-1; i++){
	// Point2D actual = (Point2D)listaPuntos.get(i);
	// actual = viewPort.fromMapPoint(actual);
	// // ahora pintamos el putno actual
	// int half = getSizePixels() / 2;
	// g.drawRect((int) (actual.getX() - half),
	// (int) (actual.getY() - half),
	// getSizePixels(), getSizePixels());
	// }
	//
	// // ahora pintamos el ultimo punto de manera distinta
	// Point2D actual = (Point2D)listaPuntos.get(listaPuntos.size()-1);
	// actual = viewPort.fromMapPoint(actual);
	// // ahora pintamos el putno actual
	// int half = getSizePixels() / 2;
	//
	// int x1 = (int) (actual.getX() - half);
	// int x2 = (int) (actual.getX() + half);
	// int y1 = (int) (actual.getY() - half);
	// int y2 = (int) (actual.getY() + half);
	//
	// g.drawLine(x1, y1, x2, y1); // abajo
	// g.drawLine(x1, y2, x2, y2); // arriba
	// g.drawLine(x1, y1, x2, y2); // abajo - arriba
	// g.drawLine(x1, y2, x2, y1); // arriba - abajo
	//
	// }else{
	int half = getSizePixels() / 2;
	int x1 = (int) (pPixels.getX() - half);
	int x2 = (int) (pPixels.getX() + half);
	int y1 = (int) (pPixels.getY() - half);
	int y2 = (int) (pPixels.getY() + half);

	g.drawLine(x1, y1, x2, y1); // abajo
	g.drawLine(x1, y2, x2, y2); // arriba
	g.drawLine(x1, y1, x2, y2); // abajo - arriba
	g.drawLine(x1, y2, x2, y1); // arriba - abajo
	// }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getPriority()
     */
    @Override
    public int getPriority() {
	return 3;
    }

    /**
     * Devuelve la lista de puntos que hay entre el ultimo y el anterior pulsado
     * dentro de la geometria
     */
    @Override
    public ArrayList getSnappedPoints() {
	ArrayList retorno = listaPuntos;
	listaPuntos = null;

	return retorno;
    }
}
