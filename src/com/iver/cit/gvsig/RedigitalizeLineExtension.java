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
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.RedigitalizeLineCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extension to redigitalize lines on edition
 * 
 * @author Jose Ignacio Lamas [LBD]
 * @author Pablo Sanxiao [Cartolab]
 */
public class RedigitalizeLineExtension extends BaseCADExtension {
    private final String iconPath = "images/icons/redigit_linea.png";
    private final String iconCode = "edition-geometry-redigitalize-tool";
    private final String cadToolCode = "_redigitalize_line";

    @Override
    public void initialize() {
	tool = new RedigitalizeLineCADTool();
	CADExtension.addCADTool(cadToolCode, tool);
	registerIcon(iconCode, iconPath);
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals("_redigitalize_line")) {
	    CADExtension.setCADTool("_redigitalize_line", true);
	    View view = (View) PluginServices.getMDIManager().getActiveWindow();
	    MapControl mapControl = view.getMapControl();
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();
    }
}
