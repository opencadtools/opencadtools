package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.RemovePartCADTool;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class RemovePartExtension extends BaseCADExtension {

    private final static String CAD_TOOL_KEY = "remove_part";

    private final static String ICON_KEY = "geometry-modify-remove-part-tool";
    private final static String ICON_PATH = "images/icons/remove_part.png";

    @Override
    public void initialize() {
	tool = new RemovePartCADTool();
	CADExtension.addCADTool(CAD_TOOL_KEY, tool);
	registerIcon(ICON_KEY, ICON_PATH);
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

    @Override
    protected boolean isCustomEnabled(VectorialLayerEdited vle) {
	// TODO: Maybe it will be acceptable that 'not edition mode selection'
	// was used
	return vle.getSelectedRow().size() == 1;
    }
}
