package com.iver.cit.gvsig.fmap.rendering;

import java.util.ArrayList;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.core.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.XMLException;

public class EditionManagerLegend implements EditionLegend{
	private ArrayList rules=new ArrayList();
	private VectorialLegend vectorialLegend;
	private VectorialLegend originalVectorialLegend;
	public EditionManagerLegend(VectorialLegend vl) {
		originalVectorialLegend=vl;
		vectorialLegend=vl;//(VectorialLegend)vl.cloneLegend();
	}
	public Value getValue(int i) {
		Value value=null;
		if (vectorialLegend instanceof VectorialUniqueValueLegend) {
			value=(Value)((VectorialUniqueValueLegend)vectorialLegend).getValues()[i];
		}else if (vectorialLegend instanceof VectorialIntervalLegend) {
			value=(Value)((VectorialIntervalLegend)vectorialLegend).getValues()[i];
		}else {
			value=ValueFactory.createValue(PluginServices.getText(this,"todos_los_valores"));
		}
		return value;
	}
	public ISymbol getSymbol(int i) {
		ISymbol symbol=null;
			symbol=((UniqueValueLegend)vectorialLegend).getSymbolByValue(getValue(i));
		return symbol;
	}
	public boolean isActived(int i) {
		return true;
	}
	public boolean isBlocked(int i) {
		return false;
	}
	public boolean isDisable(int i) {
		return false;
	}
	public boolean isFilled(int i) {
		return true;
	}
	public boolean isPresent(int i) {
		return true;
	}
	public void setActived(int i,boolean b) {
	}
	public void setBlocked(int i,boolean b) {
	}
	public void setDisable(int i, boolean b) {
	}
	public void setFilled(int i,boolean b) {
	}
	public void setPresent(int i, boolean b) {
	}
	public int getRowCount() {
		if (vectorialLegend instanceof VectorialUniqueValueLegend || vectorialLegend instanceof VectorialIntervalLegend) {
    		VectorialUniqueValueLegend vuvl=(VectorialUniqueValueLegend)vectorialLegend;
    		return vuvl.getValues().length;
    	}
        return 1;
	}
	public void setValue(int i, Object value) {
		Value previousValue=getValue(i);
		ISymbol previousSymbol=getSymbol(i);
		Value clave;
	    ISymbol theSymbol=null;
	    int numRow=getRowCount();
	    // Borramos las anteriores listas:
	    //((UniqueValueLegend)vectorialLegend).clear();

	    boolean bRestoValores = false; // PONERLO EN UN CHECKBOX
	    int hasta;
	    hasta = getRowCount();
//	    for (int row = 0; row < numRow; row++) {
//	        clave = getValue(row);
//	    	if (row==i)
	    if (!value.equals(previousValue)) {
	    	((UniqueValueLegend)vectorialLegend).delSymbol(previousValue);
	    	clave=(Value)value;
	        ((UniqueValueLegend)vectorialLegend).addSymbol(value, previousSymbol);
	        System.out.println(value);
//	    }
	    }
	    if (bRestoValores) {
	      	theSymbol = getSymbol(hasta);
	       	vectorialLegend.setDefaultSymbol(theSymbol);
	    }
	}
	public void setSymbol(int row, Object value) {
	}
}
