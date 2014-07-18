package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.RemovePartCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class RemovePartExtension extends BaseCADExtension {

    private final static String CAD_TOOL_KEY = "remove_part";

    // private final static String ICON_KEY =
    // "edition-geometry-remove-part-tool";
    // private final static String ICON_PATH = "images/icons/add_part.jpg";

    @Override
    public void initialize() {
	tool = new RemovePartCADTool();
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
	CADExtension.clearMenu();
    }
}
