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
import com.iver.cit.gvsig.project.documents.view.gui.View;

//import es.udc.lbd.eiel.cad.util.GeometryTypes;
//import es.udc.lbd.eiel.giseiel.util.layer_manager.LayerDescriptor;
//import es.udc.lbd.eiel.giseiel.util.layer_manager.LayerManager;
import com.iver.cit.gvsig.gui.cad.tools.MultiPolylineCADTool;

/**
 * Extensión que gestiona la inserción de polilíneas en edición.
 *
 * @author Isabel Pérez-Urria Lage [LBD]
 * @author Javier Estévez [Cartolab]
 */
public class InsertMultiPolylineExtension extends Extension {
	private final String iconPath = "images/MultiPolyline.png";
	private final String iconCode = "edition-geometry-insert-multipolyline";
	private final String cadToolCode = "_insert_multipolyline";

	private View view;

	private MapControl mapControl;
	private MultiPolylineCADTool multiline;

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		multiline = new MultiPolylineCADTool();
		CADExtension.addCADTool(cadToolCode, multiline);
		registerIcon();
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		CADExtension.initFocus();
		if (s.equals(cadToolCode)) {
			CADExtension.setCADTool(cadToolCode,true);
			CADExtension.getEditionManager().setMapControl(mapControl);
		}
		CADExtension.getCADToolAdapter().configureMenu();
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {

		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
			view = (View) PluginServices.getMDIManager().getActiveWindow();
			mapControl = view.getMapControl();
			if (CADExtension.getEditionManager().getActiveLayerEdited()==null)
				return false;
			FLyrVect lv=(FLyrVect)CADExtension.getEditionManager().getActiveLayerEdited().getLayer();

				try {
					if (multiline.isApplicable(lv.getShapeType()))
						return true;
				} catch (ReadDriverException e) {
					NotificationManager.addError(e.getMessage(), e);
				}

//			LayerDescriptor ld = LayerManager.getLayerDescriptor(lv.getName());
//			String tipoGeom = ld.getLayerEditionDescriptor().getTipoGeom();
//			if (multilinea.newIsApplicable(ld)){
//				return true;
//			}
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

	private void registerIcon(){
		PluginServices.getIconTheme().registerDefault(
				iconCode,
				this.getClass().getClassLoader().getResource(iconPath)
			);
	}
}
