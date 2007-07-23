package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
/**
 * @author Vicente Caballero Navarro
 */
public class PointY extends GraphicOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public double process(Index index) throws ReadDriverException, ExpansionFileReadException {
		ReadableVectorial adapter = getLayer().getSource();
		IGeometry geom=adapter.getShape(index.get());
		Double[][] xsys=getXY(geom);
		return xsys[1][0].doubleValue();
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("pointY",this,PointY.class);
		interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"double y(){return pointY.process(indexRow);};");
	}
	public String toString() {
		return "y";
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
		return (getType()==IOperator.NUMBER && type==FShape.POINT);
	}
}
