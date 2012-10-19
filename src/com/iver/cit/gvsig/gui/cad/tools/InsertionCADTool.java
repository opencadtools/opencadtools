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

package com.iver.cit.gvsig.gui.cad.tools;

import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.listeners.CADListenerManager;

/**
 * Insertion tools are grouped with this class
 * 
 * @author Isabel Pérez-Urria Lage [LBD]
 * @author Javier Estévez [Cartolab]
 */
public abstract class InsertionCADTool extends DefaultCADTool {

    /**
     * Throws end geometry event to every class listening to an
     * EndGeometryListener.
     */
    public void fireEndGeometry() {
	CADListenerManager.endGeometry(getActiveLayer(),
		"cad-tool-non-specified");
    }
}
