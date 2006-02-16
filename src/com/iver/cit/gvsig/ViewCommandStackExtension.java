package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.command.CommandStackDialog;
import com.iver.cit.gvsig.project.ProjectView;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ViewCommandStackExtension implements Extension {
	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void inicializar() {
	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String s) {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		View vista = (View) f;
		ProjectView model = vista.getModel();
		FMap mapa = model.getMapContext();
		FLayers layers = mapa.getLayers();
		if (s.equals("COMMANDSTACK")) {
			for (int i =0;i<layers.getLayersCount();i++){
				if (layers.getLayer(i) instanceof FLyrVect && ((FLyrVect)layers.getLayer(i)).isEditing()){
					CommandStackDialog csd=new CommandStackDialog(((IEditableSource)((FLyrVect)layers.getLayer(i)).getSource()).getCommandRecord());
					PluginServices.getMDIManager().addView(csd);
				}
			}


		}

		//PluginServices.getMainFrame().enableControls();

	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		else
			return false;

	}
}
