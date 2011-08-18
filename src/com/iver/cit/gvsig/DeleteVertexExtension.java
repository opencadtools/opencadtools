/*
 * 
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
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.DeleteVertexCADTool;

/**
 * Extension to delete a vertex on a geometry of a layer in edition. Layer's geometry can not be a point or
 * multipoint
 *
 * @author Nacho Uve
 * @author fpuga <fpuga (at) cartolab.es>
 */
public class DeleteVertexExtension extends Extension {
   private DeleteVertexCADTool deleteVertex;

   private final String iconPath = "images/DeleteVertex.gif";
   private final String iconCode = "edition-geometry-delete-vertex";
   private final String cadToolCode = "_deleteVertex";



   /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    public void initialize() {
        deleteVertex = new DeleteVertexCADTool();
        CADExtension.addCADTool(cadToolCode, deleteVertex);
        registerIcons();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    public void execute(String s) {
    	CADExtension.initFocus();
       	CADExtension.setCADTool(cadToolCode,true);
        CADExtension.getCADToolAdapter().configureMenu();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    public boolean isEnabled() {
    	return true;
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isVisible()
     */
    public boolean isVisible() {
    	// check if there is a layer (not point or multipoint) active and in edition
    	boolean enabled = false;
    	try {
			if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE){

				FLyrVect lv=(FLyrVect) CADExtension.getEditionManager().getActiveLayerEdited().getLayer();

				if (deleteVertex.isApplicable(lv.getShapeType())){
					enabled = true;
				}
			}
		} catch (ReadDriverException e) {
			  NotificationManager.addError(e.getMessage(),e);
		} catch (Exception e) {
			NotificationManager.addError(e.getMessage(),e);
		}
		return enabled;
    }

	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				iconCode,
				this.getClass().getClassLoader().getResource(iconPath)
			);
	}
	}
