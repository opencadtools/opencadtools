package com.iver.cit.gvsig.project.documents.view.toc.actions;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.StartEditing;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;

/**
 * Comienza la edición de la capa seleccionada.
 * 
 * @author Vicente Caballero Navarro
 */
public class StartEditingTocMenuEntry extends AbstractTocContextMenuAction {
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
	return 1;
    }

    @Override
    public String getText() {
	return PluginServices.getText(this, "start_edition");
    }

    @Override
    public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
	StartEditing startEditind = (StartEditing) PluginServices
		.getExtension(StartEditing.class);
	return startEditind.isEnabled();
    }

    @Override
    public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
	StartEditing startEditind = (StartEditing) PluginServices
		.getExtension(StartEditing.class);
	return startEditind.isVisible();
    }

    @Override
    public void execute(ITocItem item, FLayer[] selectedItems) {
	StartEditing startEditind = (StartEditing) PluginServices
		.getExtension(StartEditing.class);
	startEditind.execute("STARTEDITING");
	PluginServices.getMainFrame().enableControls();
    }
}
