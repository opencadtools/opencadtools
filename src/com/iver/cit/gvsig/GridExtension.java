package com.iver.cit.gvsig;

import java.io.IOException;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.Table;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class GridExtension extends Extension {
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		View vista = (View) f;
		if (s.equals("SHOWGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(
					vista.getMapControl());
			CADExtension.getCADToolAdapter().setGridVisibility(true);
		} else if (s.equals("HIDEGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(vista.getMapControl());
			CADExtension.getCADToolAdapter().setGridVisibility(false);
		} else if (s.equals("SETADJUSTGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(vista.getMapControl());
			CADExtension.getCADToolAdapter().setAdjustGrid(true);
		} else if (s.equals("NOTADJUSTGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(vista.getMapControl());
			CADExtension.getCADToolAdapter().setAdjustGrid(false);
		}

		PluginServices.getMainFrame().enableControls();

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		else
			return false;

	}
}
