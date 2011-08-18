
package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

import es.udc.cartolab.gvsig.navtable.NavTable;

/**
 * Extension to open a form in order to fill data for a new feature that
 * has just been digitalized
 * 
 * @author Javier Estévez [Cartolab]
 */

public class OpenFormsExtension {
	
	public static void openForm(VectorialLayerEdited vle) {
		FLayer layer = vle.getLayer();
		NavTable navTable = new NavTable((FLyrVect)layer);
		if (navTable.init()) {
			PluginServices.getMDIManager().addCentredWindow(navTable);
		}
		navTable.last();
		}
	}
