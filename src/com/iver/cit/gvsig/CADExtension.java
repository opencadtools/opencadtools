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

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.gui.cad.tools.CopyCADTool;
import com.iver.cit.gvsig.gui.cad.tools.RotateCADTool;
import com.iver.cit.gvsig.gui.cad.tools.ScaleCADTool;
import com.iver.utiles.console.JConsole;
import com.iver.utiles.console.ResponseListener;
/**
 * Extensión dedicada a controlar las diferentes operaciones sobre el editado
 * de una capa.
 *
 * @author Vicente Caballero Navarro
 */
public class CADExtension extends Extension {
   private static CADToolAdapter adapter=new CADToolAdapter();
   private static EditionManager editionManager = new EditionManager();
   private static HashMap namesCadTools = new HashMap();
   ///private MapControl mapControl;
   private static View view;
   private MapControl mapControl;
   public static CADToolAdapter getCADToolAdapter(){
	    return adapter;
   }
   /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    public void initialize() {

        CopyCADTool copy=new CopyCADTool();

        RotateCADTool rotate=new RotateCADTool();
        ScaleCADTool scale=new ScaleCADTool();


        addCADTool("copy",copy);

        addCADTool("rotate",rotate);
        addCADTool("scale",scale);

        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventPostProcessor(new myKeyEventPostProcessor());
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    public void execute(String s) {
       initFocus();

        if (s.equals("SPLINE")) {
        	setCADTool("spline",true);
        } else if (s.equals("COPY")) {
        	setCADTool("copy",true);
        } else if (s.equals("EQUIDISTANCE")) {
        	setCADTool("equidistance",true);
        } else if (s.equals("MATRIZ")) {
        	setCADTool("matriz",true);
        } else if (s.equals("SYMMETRY")) {
        	setCADTool("symmetry",true);
        } else if (s.equals("ROTATION")) {
        	setCADTool("rotate",true);
        } else if (s.equals("STRETCHING")) {
        	setCADTool("stretching",true);
        } else if (s.equals("SCALE")) {
        	setCADTool("scale",true);
        } else if (s.equals("EXTEND")) {
        	setCADTool("extend",true);
        } else if (s.equals("TRIM")) {
        	setCADTool("trim",true);
        } else if (s.equals("UNIT")) {
        	setCADTool("unit",true);
        } else if (s.equals("EXPLOIT")) {
        	setCADTool("exploit",true);
        } else if (s.equals("CHAFLAN")) {
        	setCADTool("chaflan",true);
        } else if (s.equals("JOIN")) {
        	setCADTool("join",true);
        }
        adapter.configureMenu();
        //ViewControls.CANCELED=false;
    }
    public static void addCADTool(String name, CADTool c){
		namesCadTools.put(name, c);
	}
    public static void setCADTool(String text,boolean showCommand){
		CADTool ct = (CADTool) namesCadTools.get(text);
		if (ct == null) throw new RuntimeException("No such cad tool");
		adapter.setCadTool(ct);
		ct.init();
		if (showCommand){
			View vista = (View) PluginServices.getMDIManager().getActiveView();
			vista.getConsolePanel().addText("\n" + ct.getName(),JConsole.COMMAND);
			adapter.askQuestion();
		}
		//PluginServices.getMainFrame().setSelectedTool("SELECT");
		//PluginServices.getMainFrame().enableControls();
	}
    public static CADTool getCADTool(){
    	return adapter.getCadTool();
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
    	if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
				view = (View) PluginServices.getMDIManager().getActiveView();
				mapControl = (MapControl) view.getMapControl();
				FLayer[] layers = mapControl.getMapContext().getLayers()
						.getActives();
				if (!(layers[0] instanceof FLyrAnnotation)) {
					return true;
				}
			}
		return false;
    }
	public MapControl getMapControl() {
		return editionManager.getMapControl();
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

	/**
	 * @author fjp
	 *
	 * La idea es usar esto para recibir lo que el usuario escribe y enviarlo
	 * a la consola de la vista para que salga por allí.
	 */
	private class myKeyEventPostProcessor implements KeyEventPostProcessor
	{

		public boolean postProcessKeyEvent(KeyEvent e) {
			// System.out.println("KeyEvent e = " + e);
			if ((adapter==null) ||  (view == null)) return false;
			if (e.getID() != KeyEvent.KEY_RELEASED) return false;
        	if (e.getKeyCode() == KeyEvent.VK_DELETE)
        		adapter.keyPressed("eliminar");
        	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        		adapter.keyPressed("escape");
        	else if (e.getKeyCode() == KeyEvent.VK_ENTER)
        	{
        		// TODO: REVISAR ESTO CUANDO VIENE UN INTRO DESDE UN JTEXTAREA
        		// QUE NO ES EL DE CONSOLA
        		// view.focusConsole("");
        	}
        	else
        	{
        		if ((e.getID() == KeyEvent.KEY_RELEASED) && (!e.isActionKey()))
        		{
	    			if (Character.isLetterOrDigit(e.getKeyChar()))
	    			{
	    				Character keyChar = new Character(e.getKeyChar());
	            		if (e.getComponent().getName() != null)
	            		{
	    	        		System.out.println("Evento de teclado desde el componente " + e.getComponent().getName());
	    	        		if (!e.getComponent().getName().equals("CADConsole"))
	    	        		{
	    	        			view.focusConsole(keyChar+"");
	    	        		}
	            		}
	            		else
	            		{
	    	        		if (!(e.getComponent() instanceof JTextComponent))
	    	        		{
	    	        			view.focusConsole(keyChar+"");
	    	        		}
	            		}
	        		}
        		}
        	}
			return false;
		}

	}

/*	private void registerKeyStrokes(){
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

		// El espacio como si fuera INTRO
		Character keyCharSpace = new Character(' ');
		mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(' '), keyCharSpace);
		mapControl.getActionMap().put(keyCharSpace, new KeyAction(""));


	}
*/
	private static JPopupMenu popup = new JPopupMenu();
	public static  void clearMenu(){
		popup.removeAll();
	}

	public static void addMenuEntry(String text){
		JMenuItem menu = new JMenuItem(text);
		menu.setActionCommand(text);
		menu.setEnabled(true);
		menu.setVisible(true);
		menu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adapter.transition(e.getActionCommand());
			}
		});

		popup.add(menu);
	}
	public static void showPopup(MouseEvent e) {
		    popup.show(e.getComponent(),
                       e.getX(), e.getY());
    }
	public static View getView() {
		return view;
	}

	/**
	 * @return Returns the editionManager.
	 */
	public static EditionManager getEditionManager() {
		return editionManager;
	}
	public static CADTool[] getCADTools(){
		return (CADTool[])namesCadTools.values().toArray(new CADTool[0]);
	}
	public static void initFocus(){
		 view = (View) PluginServices.getMDIManager().getActiveView();
	     MapControl mapControl = (MapControl) view.getMapControl();
	        if (!mapControl.getNamesMapTools().containsKey("cadtooladapter"))
	        	mapControl.addMapTool("cadtooladapter",adapter);
	        	view.getMapControl().setTool("cadtooladapter");
	        	view.addConsoleListener("cad", new ResponseListener() {
	     			public void acceptResponse(String response) {
	     				adapter.textEntered(response);
	     				// TODO:
	     				// FocusManager fm=FocusManager.getCurrentManager();
	     				// fm.focusPreviousComponent(mapControl);
	     				/*if (popup.isShowing()){
	     				    popup.setVisible(false);
	     				}*/

	     			}
	     		});
	       editionManager.setMapControl(mapControl);
	       view.getMapControl().setTool("cadtooladapter");
	}
}
