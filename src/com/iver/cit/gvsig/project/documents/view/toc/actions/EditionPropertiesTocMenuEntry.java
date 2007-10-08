package com.iver.cit.gvsig.project.documents.view.toc.actions;

import java.util.Iterator;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.GenericDlgPreferences;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.preferences.EditionPreferencePage;
import com.iver.cit.gvsig.gui.preferences.FlatnessPage;
import com.iver.cit.gvsig.gui.preferences.GridPage;
import com.iver.cit.gvsig.gui.preferences.SnapConfigPage;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * Abre el diálogo de propiedades de edición.
 *
 * @author Vicente Caballero Navarro
 */
public class EditionPropertiesTocMenuEntry extends AbstractTocContextMenuAction {
	public Object create() {
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
    	extensionPoints.add("cad_editing_properties_pages","grid",GridPage.class);
    	extensionPoints.add("cad_editing_properties_pages","flatness",FlatnessPage.class);
    	extensionPoints.add("cad_editing_properties_pages","snapping",SnapConfigPage.class);
    	return super.create();
	}
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
		return (isTocItemBranch(item)) && (selectedItems.length == 1 && selectedItems[0].isAvailable() && selectedItems[0] instanceof FLyrVect) && ((FLyrVect)selectedItems[0]).isEditing();
	}

	public void execute(ITocItem item, FLayer[] selectedItems) {
		EditionPreferencePage pref = new EditionPreferencePage();

		pref.setMapContext(getMapContext());
//		GridPage gridPage=new GridPage();
//		gridPage.setParentID(pref.getID());
//		FlatnessPage flatnessPage=new FlatnessPage();
//		flatnessPage.setParentID(pref.getID());


		GenericDlgPreferences dlg = new GenericDlgPreferences();
		dlg.addPreferencePage(pref);

		 ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
	        ExtensionPoint extensionPoint =(ExtensionPoint)extensionPoints.get("cad_editing_properties_pages");
	        Iterator iterator = extensionPoint.keySet().iterator();
	        while (iterator.hasNext()) {
	            try {
	            	AbstractPreferencePage  app = (AbstractPreferencePage )extensionPoint.create((String)iterator.next());
	            	app.setParentID(pref.getID());
	            	dlg.addPreferencePage(app);
	            } catch (InstantiationException e) {
	            	NotificationManager.addError(e.getMessage(),e);
	            } catch (IllegalAccessException e) {
	            	NotificationManager.addError(e.getMessage(),e);
	            } catch (ClassCastException e) {
	            	NotificationManager.addError(e.getMessage(),e);
	            }
	        }

//		dlg.addPreferencePage(gridPage);
//		dlg.addPreferencePage(flatnessPage);
//		dlg.addPreferencePage(fieldExpresionPage);
		dlg.getWindowInfo().setTitle(PluginServices.getText(this, "Edition_Properties"));
		dlg.setActivePage(pref);
		PluginServices.getMDIManager().addWindow(dlg);
   }
}
