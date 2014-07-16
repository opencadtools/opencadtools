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

import com.iver.cit.gvsig.gui.cad.tools.DeleteVertexCADTool;

/**
 * Extension to delete a vertex on a geometry of a layer in edition. Layer's
 * geometry can not be a point or multipoint
 * 
 * @author Nacho Uve
 * @author fpuga <fpuga (at) cartolab.es>
 */
public class DeleteVertexExtension extends BaseCADExtension {

    private final static String CAD_TOOL_KEY = "_deleteVertex";
    private final static String ICON_KEY = "edition-geometry-delete-vertex";
    private final static String ICON_PATH = "images/icons/eliminar_vertice.png";

    @Override
    public void initialize() {
	tool = new DeleteVertexCADTool();
	CADExtension.addCADTool(CAD_TOOL_KEY, tool);
	registerIcon(ICON_KEY, ICON_PATH);
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	CADExtension.setCADTool(CAD_TOOL_KEY, true);
	CADExtension.getCADToolAdapter().configureMenu();
    }

}
