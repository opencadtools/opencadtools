package com.iver.cit.gvsig.project.documents.table;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

/**
 * @author Vicente Caballero Navarro
 */
public interface IOperator {
	int NUMBER = 0;
	int STRING = 1;
	int DATE = 2;
	public String addText(String s);
	public String toString();
	public void eval(BSFManager interpreter) throws BSFException ;
	public boolean isEnable();
	public void setType(int fieldType);
}
