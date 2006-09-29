package com.iver.cit.gvsig.project.documents.view.toc.actions;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.GenericDlgPreferences;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.preferences.EditionPreferencePage;
import com.iver.cit.gvsig.gui.preferences.FlatnessPage;
import com.iver.cit.gvsig.gui.preferences.GridPage;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;

/**
 * Abre el diálogo de propiedades de edición.
 *
 * @author Vicente Caballero Navarro
 */
public class EditionPropertiesTocMenuEntry extends AbstractTocContextMenuAction {
	public String getGroup() {
		return "edition";
	}

	public int getGroupOrder() {
		return 60;
	}

	public int getOrder() {
		return 60;
	}

	public String getText() {
		return PluginServices.getText(this, "Edition_Properties");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		return true;
	}

	public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
		return (isTocItemBranch(item)) && (selectedItems.length == 1 && selectedItems[0] instanceof FLyrVect) && ((FLyrVect)selectedItems[0]).isEditing();
	}

	public void execute(ITocItem item, FLayer[] selectedItems) {
		EditionPreferencePage pref = new EditionPreferencePage();
		pref.setLayers(getMapContext().getLayers());
		GridPage gridPage=new GridPage();
		gridPage.setParentID(pref.getID());
		FlatnessPage flatnessPage=new FlatnessPage();
		flatnessPage.setParentID(pref.getID());


		GenericDlgPreferences dlg = new GenericDlgPreferences();
		dlg.addPreferencePage(pref);
		dlg.addPreferencePage(gridPage);
		dlg.addPreferencePage(flatnessPage);
		dlg.getWindowInfo().setTitle(PluginServices.getText(this, "Edition_Properties"));
		dlg.setActivePage(pref);
		PluginServices.getMDIManager().addWindow(dlg);
   }
}
