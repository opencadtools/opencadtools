package com.iver.cit.gvsig.project.documents.view.toc.actions;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.StopEditing;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;

/**
 * Termina la edición de la capa seleccionada.
 * 
 * @author Vicente Caballero Navarro
 */
public class StopEditingTocMenuEntry extends AbstractTocContextMenuAction {
    @Override
    public String getGroup() {
	return "edition";
    }

    @Override
    public int getGroupOrder() {
	return 1;
    }

    @Override
    public int getOrder() {
	return 2;
    }

    public String getText() {
	return PluginServices.getText(this, "stop_edition");
    }

    @Override
    public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
	return true;
    }

    @Override
    public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
	return (isTocItemBranch(item))
		&& (selectedItems.length == 1 && selectedItems[0].isAvailable() && selectedItems[0] instanceof FLyrVect)
		&& ((FLyrVect) selectedItems[0]).isEditing();
    }

    @Override
    public void execute(ITocItem item, FLayer[] selectedItems) {
	StopEditing stopEditind = (StopEditing) PluginServices
		.getExtension(StopEditing.class);
	stopEditind.execute("STOPEDITING");
	PluginServices.getMainFrame().enableControls();
    }
}
