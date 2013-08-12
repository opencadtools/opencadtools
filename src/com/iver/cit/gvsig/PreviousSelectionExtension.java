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
package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.layers.ILayerEdited;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * Extensión que gestiona el poder cambiar la selección a una anterior.
 * 
 * @author Vicente Caballero Navarro
 */
public class PreviousSelectionExtension extends Extension {

    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    @Override
    public void initialize() {
	PluginServices.getIconTheme().registerDefault(
		"previous-selection",
		this.getClass().getClassLoader()
			.getResource("images/previousSel.png"));
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	ILayerEdited layerEdited = CADExtension.getEditionManager()
		.getActiveLayerEdited();
	if (layerEdited instanceof VectorialLayerEdited) {
	    try {
		((VectorialLayerEdited) layerEdited).restorePreviousSelection();
	    } catch (ReadDriverException e) {
		NotificationManager.addError(e.getMessage(), e);
	    }
	    VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) ((VectorialLayerEdited) layerEdited)
		    .getLayer()).getSource();
	    vea.getCommandRecord().fireCommandsRepaint(null);
	}
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	ILayerEdited layerEdited = CADExtension.getEditionManager()
		.getActiveLayerEdited();
	if (layerEdited == null) {
	    return false;
	}
	if (layerEdited instanceof VectorialLayerEdited) {
	    return ((VectorialLayerEdited) layerEdited).getPreviousSelection();
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
}
