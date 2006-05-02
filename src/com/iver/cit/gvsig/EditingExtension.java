package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.ProjectView;

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
		ProjectView model = vista.getModel();
		FMap mapa = model.getMapContext();
		FLayers layers = mapa.getLayers();
		if (s.equals("CANCELEDITING")) {
			EditionManager editionManager = CADExtension.getEditionManager();

			for (int i = 0; i < layers.getLayersCount(); i++) {
				vista.hideConsole();
				if (layers.getLayer(i) instanceof FLyrVect
						&& layers.getLayer(i).isEditing()) {
					FLyrVect lv = (FLyrVect) layers.getLayer(i);
					// stopEditing(lv);
					// VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) layers
					// 		.getLayer(i)).getSource();
					// lv.setSource(vea.getOriginalAdapter());
					VectorialLayerEdited lyrEdited = (VectorialLayerEdited) editionManager.getLayerEdited(lv);
					lyrEdited.clearSelection();
					lv.setEditing(false);
					vista.getMapControl().setTool("zoomIn");
					return;
				}
			}
		} else if (s.equals("SHOWGRID")) {
			CADExtension.getCADToolAdapter().setMapControl(vista.getMapControl());
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
