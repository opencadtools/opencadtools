package com.iver.cit.gvsig;


import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.panels.options.OptionsPanel;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class AdjustLayerExtension extends Extension {
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		CADExtension.initFocus();
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();
		if (f instanceof View) {
			FLyrVect lv = (FLyrVect)CADExtension.getEditionManager().getActiveLayerEdited().getLayer();
			VectorialEditableAdapter vea=(VectorialEditableAdapter)lv.getSource();
			OptionsPanel op=new OptionsPanel(vea);
			PluginServices.getMDIManager().addView(op);
		}
	}


	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveView();

		if (f == null) {
			return false;
		}

		FLayer[] selected = f.getModel().getMapContext().getLayers()
				.getActives();
		if (selected.length == 1 && selected[0] instanceof FLyrVect) {
			if (selected[0].isEditing())
				return true;
		}
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			return true;
		} else {
			return false;
		}
	}
}
