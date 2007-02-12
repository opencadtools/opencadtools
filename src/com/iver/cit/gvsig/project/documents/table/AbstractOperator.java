package com.iver.cit.gvsig.project.documents.table;
/**
 * @author Vicente Caballero Navarro
 */
public abstract class AbstractOperator implements IOperator{
	private int type;

	public void setType(int type) {
		this.type=type;
	}
	public int getType() {
		return type;
	}
	public abstract String toString();
}
