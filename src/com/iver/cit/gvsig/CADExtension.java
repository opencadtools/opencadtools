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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.FocusManager;
import javax.swing.KeyStroke;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.gui.cad.tools.LineCADTool;
import com.iver.cit.gvsig.gui.cad.tools.PointCADTool;
import com.iver.utiles.console.ResponseListener;
/**
 * Extensión dedicada a controlar las diferentes operaciones sobre el editado
 * de una capa.
 *
 * @author Vicente Caballero Navarro
 */
public class CADExtension implements Extension {
   private static CADToolAdapter adapter=new CADToolAdapter();
   private HashMap namesCadTools = new HashMap();
   private boolean isLoad =false;
   private MapControl mapControl;
   private View view;
   public static CADToolAdapter getCADToolAdapter(){
	   return adapter;
   }
   /**
     * @see com.iver.andami.plugins.Extension#inicializar()
     */
    public void inicializar() {
        LineCADTool line = new LineCADTool();
        PointCADTool point = new PointCADTool();
        addCADTool("line", line);
        addCADTool("point", point);
    }

    /**
     * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
     */
    public void execute(String s) {
        view = (View) PluginServices.getMDIManager().getActiveView();

        //PluginServices.getMainFrame().showConsole();
        mapControl = (MapControl) view.getMapControl();
        if (!isLoad){
        	mapControl.addMapTool("cadtooladapter", new Behavior[]{adapter});
        	 view.addConsoleListener("cad", new ResponseListener() {
     			public void acceptResponse(String response) {
     				adapter.textEntered(response);
     				FocusManager fm=FocusManager.getCurrentManager();
     				fm.focusPreviousComponent(mapControl);
     				/*if (popup.isShowing()){
     				    popup.setVisible(false);
     				}*/

     			}
     		});
        }
        FLayers layers=mapControl.getMapContext().getLayers();
		for (int i=0;i<layers.getLayersCount();i++){
			if (layers.getLayer(i).isEditing() && layers.getLayer(i) instanceof FLyrVect){
				adapter.setVectorialAdapter((VectorialEditableAdapter)((FLyrVect)layers.getLayer(i)).getSource(),null);
				adapter.setMapControl(mapControl);
			}
		}

        view.getMapControl().setTool("cadtooladapter");
        //((CADToolAdapter)vista.getMapControl().getCurrentMapTool());
        if (s.compareTo("SPLINE") == 0) {
            //Spline.png
            ///vista.getMapControl().setCadTool("spline");
        } else if (s.compareTo("COPY") == 0) {
            //Copy.png
            ///vista.getMapControl().setCadTool("copy");
        } else if (s.compareTo("EQUIDISTANCE") == 0) {
            //Equidistance.png
        } else if (s.compareTo("MATRIZ") == 0) {
            //Matriz.png
        } else if (s.compareTo("SYMMETRY") == 0) {
            //Symmetry.png
            ///vista.getMapControl().setCadTool("symmetry");
        } else if (s.compareTo("ROTATION") == 0) {
            //Rotation.png
            ///vista.getMapControl().setCadTool("rotate");
        } else if (s.compareTo("STRETCHING") == 0) {
            //Stretching.png
            ///vista.getMapControl().setCadTool("stretching");
        } else if (s.compareTo("SCALE") == 0) {
            //Scale.png
            ///vista.getMapControl().setCadTool("scale");
        } else if (s.compareTo("EXTEND") == 0) {
            //Extend.png
        } else if (s.compareTo("TRIM") == 0) {
            //Trim.png
        } else if (s.compareTo("UNIT") == 0) {
            //Unit.png
        } else if (s.compareTo("EXPLOIT") == 0) {
            //Exploit.png
            ///vista.getMapControl().setCadTool("exploit");
        } else if (s.compareTo("CHAFLAN") == 0) {
            //Chaflan.png
        } else if (s.compareTo("JOIN") == 0) {
            //Join.png
        } else if (s.compareTo("SELECT") == 0) {
            ///vista.getMapControl().setCadTool("selection");
        } else if (s.compareTo("POINT") == 0) {
            ///vista.getMapControl().setCadTool("point");
        	setCADTool("point");
        } else if (s.compareTo("LINE") == 0) {
        	setCADTool("line");
        	///vista.getMapControl().setCadTool("line");
        } else if (s.compareTo("POLYLINE") == 0) {
            ///vista.getMapControl().setCadTool("polyline");
        } else if (s.compareTo("CIRCLE") == 0) {
            ///vista.getMapControl().setCadTool("circle");
        } else if (s.compareTo("ARC") == 0) {
            ///vista.getMapControl().setCadTool("arc");
        } else if (s.compareTo("ELLIPSE") == 0) {
            ///vista.getMapControl().setCadTool("ellipse");
        } else if (s.compareTo("RECTANGLE") == 0) {
            ///vista.getMapControl().setCadTool("rectangle");
        } else if (s.compareTo("POLYGON") == 0) {
            ///vista.getMapControl().setCadTool("polygon");
        }

        //ViewControls.CANCELED=false;
    }
    public void addCADTool(String name, CADTool c){
		namesCadTools.put(name, c);
	}
    public void setCADTool(String text){
		CADTool ct = (CADTool) namesCadTools.get(text);
		if (ct == null) throw new RuntimeException("No such cad tool");
		ct.init();
		adapter.setCadTool(ct);
	}
    /**
     * @see com.iver.andami.plugins.Extension#isEnabled()
     */
    public boolean isEnabled() {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        if (f == null) {
            return false;
        }

        if (f.getClass() == View.class) {
            FLayer[] l = ((View) f).getModel().getMapContext().getLayers()
                          .getActives();

            for (int i = 0; i < l.length; i++) {
                if (l[i] instanceof FLyrVect && ((FLyrVect) l[i]).isEditing()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @see com.iver.andami.plugins.Extension#isVisible()
     */
    public boolean isVisible() {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        if (f == null) {
            return false;
        }

        if (f.getClass() == View.class) {
            return true;
        } else {
            return false;
        }
    }
	public MapControl getMapControl() {
		return this.mapControl;
	}
	class KeyAction extends AbstractAction{

		private String key;

		public KeyAction(String key){
			this.key = key;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			view.focusConsole(key);
		}

	}

	class MyAction extends AbstractAction{
		private String actionCommand;

		public MyAction(String command){
			actionCommand = command;
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			adapter.keyPressed(actionCommand);
		}

	}

	private void registerKeyStrokes(){
		for (char key = '0'; key <= '9'; key++){
			Character keyChar = new Character(key);
			mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), keyChar);
			mapControl.getActionMap().put(keyChar, new KeyAction(keyChar+""));
		}
		for (char key = 'a'; key <= 'z'; key++){
			Character keyChar = new Character(key);
			mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), keyChar);
			mapControl.getActionMap().put(keyChar, new KeyAction(keyChar+""));
		}
		for (char key = 'A'; key <= 'Z'; key++){
			Character keyChar = new Character(key);
			mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), keyChar);
			mapControl.getActionMap().put(keyChar, new KeyAction(keyChar+""));
		}
		//this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        //this.getActionMap().put("enter", new MyAction("enter"));
		Character keyChar = new Character(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0).getKeyChar());
		mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),keyChar);
		mapControl.getActionMap().put(keyChar, new KeyAction(""));
	}
}
