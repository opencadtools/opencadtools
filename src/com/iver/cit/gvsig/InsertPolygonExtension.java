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
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.CircleCADTool;
import com.iver.cit.gvsig.gui.cad.tools.EIELPolygonCADTool;
import com.iver.cit.gvsig.gui.cad.tools.EditVertexCADTool;
import com.iver.cit.gvsig.gui.cad.tools.EllipseCADTool;
import com.iver.cit.gvsig.gui.cad.tools.PolygonCADTool;
import com.iver.cit.gvsig.gui.cad.tools.RectangleCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extensión que gestiona la inserción de poligonos en edición.
 *
 * @author Vicente Caballero Navarro
 */
public class InsertPolygonExtension extends Extension {
	protected View view;

	protected MapControl mapControl;
	protected PolygonCADTool polygon;

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		polygon = new PolygonCADTool();
		CircleCADTool circle=new CircleCADTool();
        RectangleCADTool rectangle=new RectangleCADTool();
        EllipseCADTool ellipse=new EllipseCADTool();
        EditVertexCADTool editvertex=new EditVertexCADTool();
		CADExtension.addCADTool("_polygon", polygon);
		CADExtension.addCADTool("_circle",circle);
	    CADExtension.addCADTool("_rectangle", rectangle);
	    CADExtension.addCADTool("_ellipse", ellipse);
	    CADExtension.addCADTool("_editvertex",editvertex);

	    registerIcons();
	}

	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"edition-insert-geometry-polygon",
				this.getClass().getClassLoader().getResource("images/Poligon.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"edition-insert-geometry-rectangle",
				this.getClass().getClassLoader().getResource("images/Rectangle.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"edition-insert-geometry-circle",
				this.getClass().getClassLoader().getResource("images/Circle.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"edition-insert-geometry-ellipse",
				this.getClass().getClassLoader().getResource("images/Ellipse.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"edition-geometry-edit-vertex",
				this.getClass().getClassLoader().getResource("images/EditVertex.png")
			);
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		CADExtension.initFocus();
		if (s.equals("_polygon")||
				s.equals("_circle")||
				s.equals("_ellipse")||
				s.equals("_rectangle")||
				s.equals("_editvertex")) {
        	CADExtension.setCADTool(s,true);
        }
		CADExtension.getEditionManager().setMapControl(mapControl);
		CADExtension.getCADToolAdapter().configureMenu();
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {

		try {
			if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
				view = (View) PluginServices.getMDIManager().getActiveWindow();
				mapControl = view.getMapControl();
				if (CADExtension.getEditionManager().getActiveLayerEdited()==null)
					return false;
				FLyrVect lv=(FLyrVect)CADExtension.getEditionManager().getActiveLayerEdited().getLayer();
				if (polygon.isApplicable(lv.getShapeType())){
					return true;
				}
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
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
}
