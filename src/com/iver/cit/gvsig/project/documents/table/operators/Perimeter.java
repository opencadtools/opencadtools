package com.iver.cit.gvsig.project.documents.table.operators;

import java.awt.geom.Point2D;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
/**
 * @author Vicente Caballero Navarro
 */
public class Perimeter extends GraphicOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public double process(Index index) throws ReadDriverException, ExpansionFileReadException {
		ReadableVectorial adapter = getLayer().getSource();
	   	IGeometry geom=adapter.getShape(index.get());
	   	Double[][] xsys=getXY(geom);
	    double dist = 0;
        double distAll = 0;

        ViewPort vp = getLayer().getMapContext().getViewPort();
        for (int i = 0; i < (xsys[0].length - 1); i++) {
            dist = 0;

            Point2D p = new Point2D.Double(xsys[0][i].doubleValue(), xsys[1][i].doubleValue());//vp.toMapPoint(new Point(event.getXs()[i].intValue(), event.getYs()[i].intValue()));
            Point2D p2 = new Point2D.Double(xsys[0][i + 1].doubleValue(), xsys[1][i + 1].doubleValue());//vp.toMapPoint(new Point(event.getXs()[i + 1].intValue(), event.getYs()[i + 1].intValue()));
            dist = vp.distanceWorld(p,p2);
            System.out.println("distancia parcial = "+dist);
            distAll += dist;
        }
        int distanceUnits=vp.getDistanceUnits();
		return distAll/MapContext.CHANGEM[distanceUnits];
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("perimeter",this,Perimeter.class);
		interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"double perimeter(){return perimeter.process(indexRow);};");
	}
	public String toString() {
		return "perimeter";
	}
	public boolean isEnable() {
		if (getLayer()==null)
			return false;
		ReadableVectorial adapter = getLayer().getSource();
		int type=FShape.POINT;
		try {
			type=adapter.getShapeType();
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		}
		return (getType()==IOperator.NUMBER && (type==FShape.POLYGON || type==FShape.LINE));
	}
}
