package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.AutoCompletePolygon;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class AutoCompletePolygonExtension extends Extension {
    private View view;

    private MapControl mapControl;

    private AutoCompletePolygon theTool;

    public void initialize() {
	theTool = new AutoCompletePolygon();
	CADExtension.addCADTool("_autocompletepolygon", theTool);
	registerIcons();
    }

    private void registerIcons() {
	PluginServices.getIconTheme().registerDefault(
		"edition-geometry-autocompletepolygon",
		this.getClass().getClassLoader()
			.getResource("images/polygon_autocomplete.png"));

    }

    public void execute(String actionCommand) {
	CADExtension.initFocus();
	if (actionCommand.equals("AUTOCOMPLETE_POLYGON")) {
	    CADExtension.setCADTool("_autocompletepolygon", true);
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();

    }

    public boolean isEnabled() {
	try {
	    if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
		view = (View) PluginServices.getMDIManager().getActiveWindow();
		mapControl = view.getMapControl();
		if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		    return false;
		}
		FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
			.getActiveLayerEdited().getLayer();
		if (theTool.isApplicable(lv.getShapeType())) {
		    return true;
		}
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
	return false;
    }

    public boolean isVisible() {
	if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
	    return true;
	}
	return false;
    }

}