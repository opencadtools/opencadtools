package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.iver.cit.gvsig.project.documents.table.AbstractOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;

/**
 * @author Vicente Caballero Navarro
 */
public class MoreThan extends AbstractOperator{

	public String addText(String s) {
		return s.concat(toString());
	}
	public void eval(BSFManager interpreter) throws BSFException {

	}
	public String toString() {
		return ">";
	}
	public boolean isEnable() {
		return (getType()==IOperator.DATE || getType()==IOperator.NUMBER);
	}
}
