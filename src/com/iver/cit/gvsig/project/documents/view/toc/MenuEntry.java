package com.iver.cit.gvsig.project.documents.view.toc;

import com.iver.cit.gvsig.project.documents.view.toc.actions.EditionPropertiesTocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.actions.StartEditingTocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.actions.StopEditingTocMenuEntry;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class MenuEntry {
    /**
     * DOCUMENT ME!
     */
    public static void register() {
    	ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
    	extensionPoints.add("View_TocActions","StartEditing",new StartEditingTocMenuEntry());
    	extensionPoints.add("View_TocActions","StopEditing",new StopEditingTocMenuEntry());
    	extensionPoints.add("View_TocActions","EditionProperties",new EditionPropertiesTocMenuEntry());

    }
}
