package com.iver.cit.gvsig.fmap.rendering;

import java.util.BitSet;

public class LegendControl extends VectorialUniqueValueLegend {

    private BitSet isBlocked = new BitSet();
    private BitSet isActivated = new BitSet();
    private BitSet isDisabled = new BitSet();
    private BitSet isFilled = new BitSet();
    private int present = 0;

    public LegendControl() {
    }

    public boolean isBlocked(int i) {
	return isBlocked.get(i);
    }

    public boolean isActivated(int i) {
	return isActivated.get(i);
    }

    public boolean isDisabled(int i) {
	return isDisabled.get(i);
    }

    public boolean isFilled(int i) {
	return isFilled.get(i);
    }

    public void setBlocked(int i, boolean b) {
	isBlocked.set(i, b);
    }

    public void setActivated(int i, boolean b) {
	isActivated.set(i, b);
    }

    public void setDisabled(int i, boolean b) {
	isDisabled.set(i, b);
    }

    public void setFilled(int i, boolean b) {
	isFilled.set(i, b);
    }

    public void setPresent(int i) {
	present = i;
    }

    public int getPresent() {
	return present;
    }
}
