package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FPolyline2D.PointSelHandler;

public class GeomEdited {
	IGeometry geom;
	
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class PointHandler extends AbstractHandler {
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public PointHandler(int i,double x, double y) {
			point = new Point2D.Double(x, y);
			index=i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {
			/* gp.pointCoords[index*2]+=x;
			gp.pointCoords[index*2+1]+=y; */
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
			/* gp.pointCoords[index*2]=x;
			gp.pointCoords[index*2+1]=y; */
		}
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	class PointSelHandler extends AbstractHandler {
		/**
		 * Crea un nuevo PointHandler.
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 */
		public PointSelHandler(int i,double x, double y) {
			point = new Point2D.Double(x, y);
			index=i;
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @param x DOCUMENT ME!
		 * @param y DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public void move(double x, double y) {
			/* gp.pointCoords[index*2]+=x;
			gp.pointCoords[index*2+1]+=y; */
		}

		/**
		 * @see com.iver.cit.gvsig.fmap.core.Handler#set(double, double)
		 */
		public void set(double x, double y) {
		/* 	gp.pointCoords[index*2]=x;
			gp.pointCoords[index*2+1]=y; */
		}
	}
	
	
	public GeomEdited(IGeometry geom)
	{
		this.geom = geom;
	}
	
	public IGeometry getGeometry()
	{
		return geom;
	}
	public void setGeometry(IGeometry geom)
	{
		this.geom = geom;
	}
	
	Handler[] getStretchingHandlers()
	{
		ArrayList handlers = new ArrayList();
		GeneralPathXIterator gpi = null;
		gpi = geom.getGeneralPathXIterator();

		double[] theData = new double[6];
		int i=0;
		while (!gpi.isDone()) {
			int theType = gpi.currentSegment(theData);
			//g.fillRect((int)(theData[0]-3),(int)(theData[1]-3),6,6);
			handlers.add(new PointSelHandler(i,theData[0], theData[1]));
			i++;
			gpi.next();
		}

		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	Handler[] getSelectionHandlers()
	{
		ArrayList handlers = new ArrayList();
		GeneralPathXIterator gpi = null;
		gpi = geom.getGeneralPathXIterator();
	
		double[] theData = new double[6];
		int i=0;
		while (!gpi.isDone()) {
			int theType = gpi.currentSegment(theData);
			//g.fillRect((int)(theData[0]-3),(int)(theData[1]-3),6,6);
			handlers.add(new PointSelHandler(i,theData[0], theData[1]));
			i++;
			gpi.next();
		}
	
		return (Handler[]) handlers.toArray(new Handler[0]);
	}
	
}
