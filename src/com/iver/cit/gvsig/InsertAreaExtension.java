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
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.AreaCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extension that manage the isertion of polygons.
 * 
 * @author Isabel Pérez-Urria Lage [LBD]
 * @author Pablo Sanxiao [CartoLab]
 */
public class InsertAreaExtension extends Extension {
    private View view;

    private MapControl mapControl;
    private AreaCADTool area;

    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    @Override
    public void initialize() {
	area = new AreaCADTool();
	CADExtension.addCADTool("_area", area);
	PluginServices.getIconTheme().registerDefault(
		"insert-area",
		this.getClass().getClassLoader()
			.getResource("images/icons/multipoligono.png"));
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals("_area")) {
	    CADExtension.setCADTool(s, true);
	}
	CADExtension.getEditionManager().setMapControl(mapControl);
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
	    if (CADExtension.getEditionManager().getActiveLayerEdited() == null)
		return false;
	    FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
		    .getActiveLayerEdited().getLayer();
	    try {
		if (area.isApplicable(lv.getShapeType()))
		    return true;
	    } catch (ReadDriverException e) {
		e.printStackTrace();
	    }

	    // LayerDescriptor ld =
	    // LayerManager.getLayerDescriptor(lv.getName());
	    // String tipoGeom = ld.getLayerEditionDescriptor().getTipoGeom();
	    // if (area.newIsApplicable(ld)){
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
	if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
	    return true;
	return false;
    }
}
