package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.AutoCompletePolygon;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class AutoCompletePolygonExtension extends BaseCADExtension {

    @Override
    public void initialize() {
	tool = new AutoCompletePolygon();
	CADExtension.addCADTool("_autocompletepolygon", tool);
	registerIcon("edition-geometry-autocompletepolygon",
		"images/polygon_autocomplete.png");
    }

    @Override
    public void execute(String actionCommand) {
	CADExtension.initFocus();
	if (actionCommand.equals("AUTOCOMPLETE_POLYGON")) {
	    CADExtension.setCADTool("_autocompletepolygon", true);
	    View view = (View) PluginServices.getMDIManager().getActiveWindow();
	    MapControl mapControl = view.getMapControl();
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();

    }
}