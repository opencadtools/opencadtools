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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.CutLineCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extension to cut lines on edition
 * 
 * @author Jose Ignacio Lamas [LBD]
 * @author Nacho Varela [Cartolab]
 * @author Pablo Sanxiao [Cartolab]
 */
public class CutLineExtension extends Extension {

    private final String iconPath = "images/icons/cortar_linea.png";
    private final String iconCode = "edition-cut-line";
    private final String cadToolCode = "_cut_line";
    private View view;

    private MapControl mapControl;
    private CutLineCADTool line;

    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    @Override
    public void initialize() {
	line = new CutLineCADTool();
	CADExtension.addCADTool(cadToolCode, line);
	registerIcons();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals(cadToolCode)) {
	    CADExtension.setCADTool(cadToolCode, true);
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    @Override
    public boolean isEnabled() {

	if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
	    view = (View) PluginServices.getMDIManager().getActiveWindow();
	    mapControl = view.getMapControl();
	    if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		return false;
	    }
	    FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
		    .getActiveLayerEdited().getLayer();

	    try {
		if (line.isApplicable(lv.getShapeType())) {
		    return true;
		}
	    } catch (ReadDriverException e) {
		NotificationManager.addError(e.getMessage(), e);
	    }

	    // LayerDescriptor ld =
	    // LayerManager.getLayerDescriptor(lv.getName());
	    // String tipoGeom = ld.getLayerEditionDescriptor().getTipoGeom();
	    // if (linea.newIsApplicable(ld)){
	    // return true;
	    // }
	}

	return false;
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isVisible()
     */
    @Override
    public boolean isVisible() {
	if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
	    return true;
	}
	return false;
    }

    private void registerIcons() {
	PluginServices.getIconTheme().registerDefault(iconCode,
		this.getClass().getClassLoader().getResource(iconPath));
    }
}
