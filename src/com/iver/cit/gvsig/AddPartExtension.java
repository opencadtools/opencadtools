package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.AddPartCADTool;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Copyright 2014. iCarto.
 * 
 */
public class AddPartExtension extends BaseCADExtension {

    private static final String CAD_TOOL_KEY = "_add_part";

    // private static final String ICON_PATH = "images/icons/add_part.jpg";
    // private static final String ICON_KEY = "edition-geometry-add-part-tool";

    @Override
    public void initialize() {
	tool = new AddPartCADTool();
	CADExtension.addCADTool(CAD_TOOL_KEY, tool);
	// registerIcon(ICON_KEY, ICON_PATH);
    }

    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	CADExtension.setCADTool(CAD_TOOL_KEY, true);
	View view = (View) PluginServices.getMDIManager().getActiveWindow();
	MapControl mapControl = view.getMapControl();
	CADExtension.getEditionManager().setMapControl(mapControl);
	CADExtension.getCADToolAdapter().configureMenu();

    }

    @Override
    protected boolean isCustomEnabled(VectorialLayerEdited vle) {
	// TODO: Maybe it will be acceptable that 'not edition mode selection'
	// was used
	return vle.getSelectedRow().size() == 1;
    }
}
