package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
/**
 * @author Vicente Caballero Navarro
 */
public class Geometry extends GraphicOperator{

	public String addText(String s) {
		return s.concat(toString()+"()");
	}
	public double process(Index index) throws DriverIOException {
		return 0;
	}
	public IGeometry getGeometry(Index index) throws DriverIOException {
		ReadableVectorial adapter = getLayer().getSource();
	   	IGeometry geom=adapter.getShape(index.get());
	   	return geom;
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean("geometry",this,Geometry.class);
		interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"java.lang.Object geometry(){return geometry.getGeometry(indexRow);};");
	}
	public String toString() {
		return "geometry";
	}
	public boolean isEnable() {
		return false;
	}
}
