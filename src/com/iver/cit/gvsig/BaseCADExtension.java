package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;

public abstract class BaseCADExtension extends Extension {

    protected DefaultCADTool tool;

    protected void registerIcon(String key, String path) {
	PluginServices.getIconTheme().registerDefault(key,
		this.getClass().getClassLoader().getResource(path));
    }

    // public boolean isEnabled() {
    // if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
    // return false;
    // }
    // FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
    // .getActiveLayerEdited().getLayer();
    // try {
    // return selection.isApplicable(lv.getShapeType());
    // } catch (ReadDriverException e) {
    // NotificationManager.addError(e.getMessage(), e);
    // }
    // return false;
    // }

    @Override
    public boolean isEnabled() {
	try {
	    if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
		if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		    return false;
		}
		FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
			.getActiveLayerEdited().getLayer();
		if (tool.isApplicable(lv.getShapeType())) {
		    return true;
		}
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
	return false;
    }

    @Override
    public boolean isVisible() {
	return EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE;
    }

}
