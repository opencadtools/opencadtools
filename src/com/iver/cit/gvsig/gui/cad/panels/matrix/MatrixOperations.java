package com.iver.cit.gvsig.gui.cad.panels.matrix;

/**
 * Characteristics to create the matrix of geometries.
 * 
 * @author Vicente Caballero Navarro
 */
public class MatrixOperations {
    private boolean isRectangular = true;
    private double distRows = 1000;
    private double distColumns = 1000;
    private double rotation = 0;
    private int numRows = 5;
    private int numColumns = 5;
    private double positionX = 900000;
    private double positionY = 4500000;
    private int num = 5;
    private boolean accepted = false;
    private boolean rotateElements = false;

    /**
     * Returns the distance among the columns of the matrix in units of the map
     * 
     * @return Distance X.
     */
    public double getDistColumns() {
	return distColumns;
    }

    /**
     * Add the distance among the columns of the matrix in units of the map
     * 
     * @param distColumns
     *            Distance X
     */
    public void setDistColumns(double distColumns) {
	this.distColumns = distColumns;
    }

    /**
     * Returns the distance among the rows of the matrix in units of the map
     * 
     * @return Distance Y.
     */
    public double getDistRows() {
	return distRows;
    }

    /**
     * Add the distance among the rows of the matrix in units of the map
     * 
     * @param distColumns
     *            Distance Y
     */
    public void setDistRows(double distRows) {
	this.distRows = distRows;
    }

    /**
     * Returns true if the form to calculate the matrix is rectangular and false
     * it if is polar.
     * 
     * @return True if the form is Rectangular.
     */
    public boolean isRectangular() {
	return isRectangular;
    }

    /**
     * Add if the form to calulate the matrix is rectangular or polar.
     * 
     * @param isRectangular
     */
    public void setRectangular(boolean isRectangular) {
	this.isRectangular = isRectangular;
    }

    /**
     * Returns the number of geometries to show in polar form.
     * 
     * @return Number of geometries.
     */
    public int getNum() {
	return num;
    }

    /**
     * Add the number of geometries to show in polar form.
     * 
     * @param num
     *            Number of geometries.
     */
    public void setNum(int num) {
	this.num = num;
    }

    /**
     * Returns the number of columns in the matrix.
     * 
     * @return Number of columns.
     */
    public int getNumColumns() {
	return numColumns;
    }

    /**
     * Add the number of columns in the matrix.
     * 
     * @param numColumns
     *            Number of columns.
     */
    public void setNumColumns(int numColumns) {
	this.numColumns = numColumns;
    }

    /**
     * Returns the number of rows in the matrix.
     * 
     * @return Number of rows.
     */
    public int getNumRows() {
	return numRows;
    }

    /**
     * Add the number of rows in the matrix.
     * 
     * @param numRows
     *            Number of rows.
     */
    public void setNumRows(int numRows) {
	this.numRows = numRows;
    }

    /**
     * Returns the position x of polar form center.
     * 
     * @return Position x.
     */
    public double getPositionX() {
	return positionX;
    }

    /**
     * Add the position x of polar form center.
     * 
     * @param positionX
     *            Position x.
     */
    public void setPositionX(double positionX) {
	this.positionX = positionX;
    }

    /**
     * Returns the position y of polar form center.
     * 
     * @return Position y.
     */
    public double getPositionY() {
	return positionY;
    }

    /**
     * Add the position y of polar form center.
     * 
     * @param positionY
     *            Position y.
     */
    public void setPositionY(double positionY) {
	this.positionY = positionY;
    }

    /**
     * Returns de rotation of geometries.
     * 
     * @return Rotation.
     */
    public double getRotation() {
	return rotation;
    }

    /**
     * Add the rotation of geometries.
     * 
     * @param rotation
     *            Rotation.
     */
    public void setRotation(double rotation) {
	this.rotation = rotation;
    }

    /**
     * Returns true if all of properties are ready to be applied.
     * 
     * @return True if is ready.
     */
    public boolean isAccepted() {
	return this.accepted;
    }

    /**
     * Add true if all of properties are ready to be applied.
     * 
     * @param accepted
     *            True if is ready.
     */
    public void setAccepted(boolean accepted) {
	this.accepted = accepted;
    }

    /**
     * Introduces true if the geometries has to rotate also in the polar form.
     * 
     * @param b
     *            True if has to ratate.
     */
    public void setRotateElements(boolean b) {
	this.rotateElements = b;
    }

    /**
     * Returns true if the geometries has to rate also in the polar form.
     * 
     * @return True if has to rotate.
     */
    public boolean isRotateElements() {
	return rotateElements;
    }
}
