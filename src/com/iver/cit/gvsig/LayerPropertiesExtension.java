package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LayerProperties;

public class LayerPropertiesExtension extends Extension{

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.IWindow view = PluginServices.getMDIManager().getActiveWindow();
		if (!(view instanceof View))
			return;
		View vista=(View)view;
		IProjectView model = vista.getModel();
		MapContext mapa = model.getMapContext();
		MapControl mapCtrl = vista.getMapControl();
		FLayer[] layers=mapa.getLayers().getActives();
		//layers[0].
		LayerProperties layerProperties=new LayerProperties(layers[0],((FLyrVect)layers[0]).getLegend());
		PluginServices.getMDIManager().addWindow(layerProperties);
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}

}
