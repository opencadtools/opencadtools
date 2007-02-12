package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.project.documents.table.AbstractOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;

/**
 * @author Vicente Caballero Navarro
 */
public class ToDate extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+")";
	}
	public String toString() {
		return "toDate";
	}
	public void eval(BSFManager interpreter) throws BSFException {

		interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"java.util.Date toDate(String value){return java.util.Date.parse(value);};");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}

}
