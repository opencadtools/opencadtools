package com.iver.cit.gvsig.project.documents.view.snapping;

import java.awt.Color;
import java.util.ArrayList;

import com.iver.andami.ui.mdiManager.IWindow;

public abstract class AbstractSnapper implements ISnapper {

    // private Point2D snapPoint = null;
    private int sizePixels = 10;
    private Color color = Color.MAGENTA;
    private boolean enabled;
    private int priority = 10;

    // public void setSnapPoint(Point2D snapPoint) {
    // this.snapPoint = snapPoint;
    //
    // }

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

    @Override
    public IWindow getConfigurator() {
	// DefaultConfigurePanel configurePanel=new DefaultConfigurePanel();
	// return configurePanel;
	return null;
    }

    @Override
    public boolean isEnabled() {
	return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    @Override
    public ArrayList getSnappedPoints() {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#getPriority()
     */
    @Override
    public int getPriority() {
	return priority;
    }

    @Override
    public void setPriority(int priority) {
	this.priority = priority;
    }
}
