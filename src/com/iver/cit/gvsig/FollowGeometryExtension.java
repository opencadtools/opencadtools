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
import com.iver.cit.gvsig.gui.cad.CADStatus;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.tools.InsertionCADTool;
import com.iver.cit.gvsig.gui.cad.tools.RedigitalizeLineCADTool;
import com.iver.cit.gvsig.gui.cad.tools.RedigitalizePolygonCADTool;
import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.console.JConsole;

/**
 * Extensión que gestiona la inserción de poligonos en edición.
 * 
 * @author Isabel Pérez-Urria [LBD]
 * @author Francisco Puga <fpuga (at) cartolab.es>
 * @author Javier Estévez Valiñas
 */
public class FollowGeometryExtension extends Extension {

	public void initialize() {
        PluginServices.getIconTheme().registerDefault(
				"follow-geometry",
		this.getClass().getClassLoader()
			.getResource("images/icons/seg_geometria.png")
			);
	}

	public void execute(String s) {
		
		CADExtension.initFocus();
		if (s.equals("_follow")) {
	    CADStatus snappers = CADStatus.getCADStatus();
	    boolean activated = snappers.isFollowGeometryActivated();
			String message = new String();
			
			if (activated) {
		message = PluginServices
			.getText(this, "followGeom_deactivated");
			} else {
		message = PluginServices.getText(this, "followGeom_activated");
			}

	    snappers.setFollowGeometryActivated(!activated);
			//Printing in console if the forms are activated
			if (PluginServices.getMDIManager().getActiveWindow() instanceof View)
			{
				View vista = (View) PluginServices.getMDIManager().getActiveWindow();
				vista.getConsolePanel().addText("\n" +message, JConsole.INSERT);
			}
			
			//[lbd] repintamos para que se vuelva a ver la herramienta
			CADExtension.getEditionManager().getMapControl().repaint();
		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {

		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
			
			if (CADExtension.getEditionManager().getActiveLayerEdited()==null) {
				return false;
			}
			CADTool cadTool = CADExtension.getCADToolAdapter().getCadTool();			
			
			if (isInsertionTool(cadTool)){
				return true;
			}
			
		}

		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		return false;
	}
	
	private boolean isInsertionTool(CADTool cadTool){		
		
		if ((cadTool instanceof InsertionCADTool) ||
			 (cadTool instanceof RedigitalizeLineCADTool) ||
			 (cadTool instanceof RedigitalizePolygonCADTool) ||
			 (cadTool instanceof SelectionCADTool)) {
			
			return true;
			
		}
		
		return false;
	}
	
}
