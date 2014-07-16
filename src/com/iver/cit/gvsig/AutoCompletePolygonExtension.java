package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.AutoCompletePolygon;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class AutoCompletePolygonExtension extends BaseCADExtension {

    private static final String CAD_TOOL_KEY = "_autocompletepolygon";
    private static final String ICON_KEY = "edition-geometry-autocompletepolygon";
    private static final String ICON_PATH = "images/polygon_autocomplete.png";

    @Override
    public void initialize() {
	tool = new AutoCompletePolygon();
	CADExtension.addCADTool(CAD_TOOL_KEY, tool);
	registerIcon(ICON_KEY, ICON_PATH);
    }

    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals(CAD_TOOL_KEY)) {
	    CADExtension.setCADTool(CAD_TOOL_KEY, true);
	    View view = (View) PluginServices.getMDIManager().getActiveWindow();
	    MapControl mapControl = view.getMapControl();
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();

    }
}