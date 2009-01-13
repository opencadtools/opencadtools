package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.PolylineCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.PolylineCADToolContext.PolylineCADToolState;
import com.vividsolutions.jts.geom.Geometry;

public class AutoCompletePolygon extends PolylineCADTool {

	@Override
	 /**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometrías seleccionadas.
     * @param x parámetro x del punto que se pase para dibujar.
     * @param y parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x,
        double y) {
        IGeometry geom=getGeometry();
        if (geom.getHandlers(IGeometry.SELECTHANDLER).length > 2) {
        	GeneralPathX gpxGeom=new GeneralPathX();
        	gpxGeom.moveTo(x,y);
        	gpxGeom.append(geom.getPathIterator(null,FConverter.FLATNESS), true);

        	gpxGeom.closePath();
        	IGeometry newGeom = autoComplete(ShapeFactory.createPolygon2D(gpxGeom));
        	newGeom.draw((Graphics2D) g,
        			getCadToolAdapter().getMapControl().getViewPort(),
        			DefaultCADTool.geometrySelectSymbol);

        	Handler handler1 = geom.getHandlers(IGeometry.SELECTHANDLER)[0];
        	GeneralPathX gpx = new GeneralPathX();
        	gpx.moveTo(x, y);
        	Point2D p1 = handler1.getPoint();
        	gpx.lineTo(p1.getX(), p1.getY());
        	ShapeFactory.createPolyline2D(gpx).draw((Graphics2D) g,
        			getCadToolAdapter().getMapControl().getViewPort(),
        			DefaultCADTool.geometrySelectSymbol);
        }
    }


	private IGeometry autoComplete(IGeometry geom) {
		IGeometry digitizedGeom = geom;
		Geometry jtsGeom = digitizedGeom.toJTSGeometry();
		FLyrVect lyrVect = (FLyrVect) getVLE().getLayer();
		// Se supone que debe ser rápido, ya que está indexado
		try {
			FBitSet selected = lyrVect.queryByShape(digitizedGeom,
					DefaultStrategy.INTERSECTS);
			for (int i = selected.nextSetBit(0); i >= 0; i = selected
					.nextSetBit(i + 1)) {
				IGeometry aux = lyrVect.getSource().getShape(i);
				Geometry jtsAux = aux.toJTSGeometry();
				jtsGeom = jtsGeom.difference(jtsAux);
			}

		} catch (ReadDriverException e) {
			e.printStackTrace();
			NotificationManager.addError(e);
		} catch (VisitorException e) {
			e.printStackTrace();
			NotificationManager.addError(e);
		} catch (Exception e) {

		}
		return FConverter.jts_to_igeometry(jtsGeom);
	}

	@Override
	public void addGeometry(IGeometry geometry) {
		IGeometry newGeom = autoComplete(geometry);
		super.addGeometry(newGeom);
	}
}