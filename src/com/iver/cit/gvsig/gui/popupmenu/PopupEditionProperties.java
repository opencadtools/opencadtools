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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.GenericDlgPreferences;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.gui.preferences.EditionPreferencePage;
import com.iver.cit.gvsig.gui.toc.FPopupMenu;
import com.iver.cit.gvsig.gui.toc.TocMenuEntry;

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
		EditionPreferencePage pref = new EditionPreferencePage();
		pref.setLayers(getMapContext().getLayers());
		
//		JDialog dlg = new JDialog((Frame) PluginServices.getMainFrame(),
//				PluginServices.getText(this, "Edition_Properties"));
//		dlg.getContentPane().setLayout(new BorderLayout());
//		dlg.getContentPane().add(pref, BorderLayout.CENTER);
//		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		dlg.pack();
//		dlg.setModal(true);
//		dlg.setVisible(true);

		
		GenericDlgPreferences dlg = new GenericDlgPreferences();
		dlg.getViewInfo().setTitle(PluginServices.getText(this, "Edition_Properties"));
		dlg.addPreferencePage(pref);
		dlg.setActivePage(pref);
		PluginServices.getMDIManager().addView(dlg);
		
		
		
	}

}
