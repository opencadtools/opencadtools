package com.iver.cit.gvsig.gui.cad.snapping;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

public abstract class AbstractSnapper implements ISnapper {

	// private Point2D snapPoint = null;
	private int sizePixels = 12;
	private Color color = Color.MAGENTA;
	private boolean enabled;
//	public void setSnapPoint(Point2D snapPoint) {
//		this.snapPoint = snapPoint;
//
//	}


	public int getSizePixels() {
		return sizePixels;
	}

	public void setSizePixels(int sizePixels) {
		this.sizePixels = sizePixels;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public JComponent getConfigurator()
	{
		return null; // Por defecto no se puede configurar un snapper.
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
