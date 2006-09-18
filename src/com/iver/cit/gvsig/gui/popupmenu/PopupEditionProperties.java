/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.gui.popupmenu;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.GenericDlgPreferences;
import com.iver.cit.gvsig.ViewControls;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.gui.preferences.EditionPreferencePage;
import com.iver.cit.gvsig.gui.preferences.FlatnessPage;
import com.iver.cit.gvsig.gui.preferences.GridPage;
import com.iver.cit.gvsig.project.documents.view.toc.TocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.gui.FPopupMenu;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class PopupEditionProperties extends TocMenuEntry {
	private JMenuItem propsMenuItem;

	FLayer lyr = null;

	public void initialize(FPopupMenu m) {
		super.initialize(m);

		if (isTocItemBranch()) {
			lyr = getNodeLayer();
			if (lyr.isEditing()) {
				propsMenuItem = new JMenuItem(PluginServices.getText(this,
						"edition_properties"));
				getMenu().add(propsMenuItem);
				propsMenuItem.setFont(FPopupMenu.theFont);
				getMenu().setEnabled(true);
				propsMenuItem.addActionListener(this);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		lyr = getNodeLayer();
		//ViewControls vc=(ViewControls)PluginServices.getExtension(ViewControls.class);
		EditionPreferencePage pref = new EditionPreferencePage();
		pref.setLayers(getMapContext().getLayers());
		//pref.setParentID(vc.getPreferencesPage().getID());
		GridPage gridPage=new GridPage();
		gridPage.setParentID(pref.getID());
		FlatnessPage flatnessPage=new FlatnessPage();
		flatnessPage.setParentID(pref.getID());



		//		ExtensionPoints extensionPoints =
//			ExtensionPointsSingleton.getInstance();
//		extensionPoints.add("AplicationPreferences",pref.getID(), pref);
//		extensionPoints.add("AplicationPreferences",gridPage.getID(), gridPage);
//		extensionPoints.add("AplicationPreferences",flatnessPage.getID(), flatnessPage);
		GenericDlgPreferences dlg = new GenericDlgPreferences();
		dlg.addPreferencePage(pref);
		dlg.addPreferencePage(gridPage);
		dlg.addPreferencePage(flatnessPage);
		dlg.getWindowInfo().setTitle(PluginServices.getText(this, "Edition_Properties"));
		dlg.setActivePage(pref);
		PluginServices.getMDIManager().addWindow(dlg);



	}

}
