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

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * @author Isabel Pérez-Urria Lage
 * @author Andrés Maneiro [Cartolab]
 */

public class ShortcutsExtension extends Extension implements KeyEventPostProcessor {

	public void execute(String actionCommand) {
		
	}

	public void initialize() {
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventPostProcessor(this);
		
	}

	public boolean isEnabled() {
		return PluginServices.getMDIManager().getActiveWindow() instanceof View;
	}

	public boolean isVisible() {
		return false;
	}
	
	/**
	 * Changes selected tool.
	 * @param tool toolname 
	 * @param toolBarTool toolname in its config.xml
	 */
	private void setTool(String tool, String toolBarTool){
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		if (v.getMapControl()!=null){
			v.getMapControl().setTool(tool);
			PluginServices.getMainFrame().setSelectedTool(toolBarTool);
		}
	}

	public boolean postProcessKeyEvent(KeyEvent event) {
		
		boolean processed = false;
		
		if (event.getID() == KeyEvent.KEY_RELEASED) {
			if (event.getKeyCode() == KeyEvent.VK_F2) {
				setTool("zoomIn", "ZOOM_IN");
				processed = true;
			}
			if (event.getKeyCode() == KeyEvent.VK_F3) {
				setTool("zoomOut", "ZOOM_OUT");
				processed = true;	
			}
			if (event.getKeyCode() == KeyEvent.VK_F4) {
				setTool("pan", "PAN");
				processed = true;	
			}
		}
		return processed;
	}

}
