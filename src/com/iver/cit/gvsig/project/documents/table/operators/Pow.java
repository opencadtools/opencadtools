package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.project.documents.table.AbstractOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;

/**
 * @author Vicente Caballero Navarro
 */
public class Pow extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+", \"\")";
	}

	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"double pow(double value1,double value2){return java.lang.Math.pow(value1,value2);};");
	}
	public String toString() {
		return "pow";
	}
	public boolean isEnable() {
		return (getType()==IOperator.NUMBER);
	}
}
