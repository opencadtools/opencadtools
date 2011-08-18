/*
 * Copyright 2008 Deputación Provincial de A Coruña
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
 * http://www.cartolab.es
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
 */
package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.console.JConsole;

/**
 * Extension to manage the activation and deactivation of showing forms when
 * a new feature has just been digitalized.
 * 
 * @author Pablo Sanxiao
 *
 */
public class ActivateFormsExtension extends Extension {
	
	private static boolean activated;

	public static boolean getActivated() {
		return activated;
	}
	
	public void execute(String actionCommand) {
		CADExtension.initFocus();

		if (actionCommand.equals("_forms")) {
			
			String message = new String();

			if (activated) {
				activated = false;
				message = PluginServices.getText(this, "forms_deactivated");
			} else {
				activated = true;
				message = PluginServices.getText(this, "forms_activated");
			}

			//Printing in console if the forms are activated
			if (PluginServices.getMDIManager().getActiveWindow() instanceof View)
			{
				View vista = (View) PluginServices.getMDIManager().getActiveWindow();
				vista.getConsolePanel().addText("\n" +message, JConsole.INSERT);
			}
		}
	}

	public void initialize() {
		activated = true;
        PluginServices.getIconTheme().registerDefault(
				"activate-forms",
				this.getClass().getClassLoader().getResource("images/forms.png")
			);
	}

	public boolean isEnabled() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
			if (CADExtension.getEditionManager().getActiveLayerEdited()==null){
				return false;
			}
			return true;
		}		
		return false;
	}

	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		return false;
	}

}
