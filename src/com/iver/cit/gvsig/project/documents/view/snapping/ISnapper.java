package com.iver.cit.gvsig.project.documents.view.snapping;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.iver.andami.ui.mdiManager.IWindow;

/**
 * @author fjp
 *
 */
public interface ISnapper {


	// void setSnapPoint(Point2D snapPoint);

	void draw(Graphics g, Point2D pPixels);

	String getToolTipText();

	/**
	 * Implement this if you need a Snapper more important than the others.
	 * Default value is 0 (no prority).
	 * @return
	 */
	int getPriority();

	boolean isEnabled();
	public void setEnabled(boolean enabled);
	public void setPriority(int priority);
	IWindow getConfigurator();
	
	/**
	 * Return a list of Point2D of special snappers
	 */
	public ArrayList getSnappedPoints();

}
