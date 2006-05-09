package com.iver.cit.gvsig;

import java.io.IOException;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
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
public class EditingExtension extends Extension {
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
		if (s.equals("CANCELEDITING")) {
			EditionManager editionManager = CADExtension.getEditionManager();
			VectorialLayerEdited vle = (VectorialLayerEdited) editionManager
					.getActiveLayerEdited();
			FLyrVect lv = (FLyrVect) vle.getLayer();
			com.iver.andami.ui.mdiManager.View[] views = PluginServices
				.getMDIManager().getAllViews();
			for (int j = 0; j < views.length; j++) {
				if (views[j] instanceof Table) {
					Table table = (Table) views[j];
					if (table.getModel().getAssociatedTable()!=null && table.getModel().getAssociatedTable().equals(lv)) {
						try {
							table.cancelEditing();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else if(views[j] instanceof View){
					View view=(View)views[j];
					FLyrVect layer=(FLyrVect)view.getMapControl().getMapContext().getLayers().getActives()[0];
					if (layer.equals(lv)){
						view.hideConsole();
					}
				}
			}
			vle.clearSelection();
			lv.setEditing(false);

			vista.getMapControl().setTool("zoomIn");


		} else if (s.equals("SHOWGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(
					vista.getMapControl());
			CADExtension.getCADToolAdapter().setGrid(true);
		} else if (s.equals("HIDEGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(vista.getMapControl());
			CADExtension.getCADToolAdapter().setGrid(false);
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
