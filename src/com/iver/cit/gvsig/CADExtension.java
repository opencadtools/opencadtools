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
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.gui.accelerators.ForceCursorAccelerator;
import com.iver.cit.gvsig.gui.accelerators.GridAccelerator;
import com.iver.cit.gvsig.gui.accelerators.OrtoAccelerator;
import com.iver.cit.gvsig.gui.accelerators.RefentAccelerator;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.gui.cad.tools.CopyCADTool;
import com.iver.cit.gvsig.gui.cad.tools.RotateCADTool;
import com.iver.cit.gvsig.gui.cad.tools.ScaleCADTool;
import com.iver.cit.gvsig.gui.cad.tools.SymmetryCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toc.MenuEntry;
import com.iver.utiles.console.JConsole;
import com.iver.utiles.console.ResponseListener;
import com.iver.utiles.console.jedit.JEditTextArea;

/**
 * Extensión dedicada a controlar las diferentes operaciones sobre el editado de
 * una capa.
 *
 * @author Vicente Caballero Navarro
 */
public class CADExtension extends Extension {
	private static HashMap adapters=new HashMap();

	private static View view;

	private MapControl mapControl;
	private static CADToolAdapter adapter=null;

	public static CADToolAdapter getCADToolAdapter() {
		com.iver.andami.ui.mdiManager.IWindow view=PluginServices.getMDIManager().getActiveWindow();
		if (view instanceof View) {
			View v=(View)view;
			if (!adapters.containsKey(v.getModel())) {
				adapters.put(v.getModel(),new CADToolAdapter());
			}
			adapter=(CADToolAdapter)adapters.get(v.getModel());
			return adapter;
		}
		return adapter;

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {

		// Registramos los Popup menus:
        MenuEntry.register();

		// Fijamos que los símbolos de dibujo tengan outline
		// TODO: Esto se debe configurar en el cuadro de diálogo de preferencias
		CADTool.drawingSymbol.setOutlined(true);
		CADTool.drawingSymbol.setOutlineColor(CADTool.drawingSymbol.getColor().darker());
		CADTool.modifySymbol.setOutlined(true);
		CADTool.modifySymbol.setOutlineColor(CADTool.modifySymbol.getColor().darker());
		CADTool.selectSymbol.setOutlined(true);
		CADTool.selectSymbol.setOutlineColor(CADTool.selectSymbol.getColor().darker());


		CopyCADTool copy = new CopyCADTool();
		RotateCADTool rotate = new RotateCADTool();
		ScaleCADTool scale = new ScaleCADTool();
		SymmetryCADTool symmetry=new SymmetryCADTool();
		addCADTool("_copy", copy);
		addCADTool("_rotate", rotate);
		addCADTool("_scale", scale);
		addCADTool("_symmetry", symmetry);
	// Registramos las teclas de acceso rápido que vamos a usar.

		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
		RefentAccelerator accRef = new RefentAccelerator();
		PluginServices.registerKeyStroke(key, accRef);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0);
		OrtoAccelerator accOrto = new OrtoAccelerator();
		PluginServices.registerKeyStroke(key, accOrto);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
		GridAccelerator accGrid = new GridAccelerator();
		PluginServices.registerKeyStroke(key, accGrid);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
		ForceCursorAccelerator accForce = new ForceCursorAccelerator();
		PluginServices.registerKeyStroke(key, accForce);


		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.addKeyEventPostProcessor(new myKeyEventPostProcessor());
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		initFocus();

		if (s.equals("_spline") || s.equals("_copy")
				|| s.equals("_equidistance") || s.equals("_matriz")
				|| s.equals("_symmetry") || s.equals("_rotate")
				|| s.equals("_stretch") || s.equals("_scale")
				|| s.equals("_extend") || s.equals("_trim")
				|| s.equals("_unit")
				|| s.equals("_chaflan") || s.equals("_join")) {
			setCADTool(s, true);
		}
		CADToolAdapter cta=getCADToolAdapter();
		cta.configureMenu();
	}

	public static void addCADTool(String name, CADTool c) {
		CADToolAdapter.addCADTool(name, c);
	}

	public static void setCADTool(String text, boolean showCommand) {
		CADToolAdapter cta=getCADToolAdapter();
		CADTool ct=cta.getCADTool(text);

		if (ct == null)
			throw new RuntimeException("No such cad tool");
		cta.initializeFlatness();
		cta.initializeGrid();
		cta.setCadTool(ct);
		ct.init();
		if (showCommand) {
			if (PluginServices.getMDIManager().getActiveWindow() instanceof View) {
				View vista = (View) PluginServices.getMDIManager().getActiveWindow();
				vista.getConsolePanel().addText("\n" + ct.getName(),
					JConsole.COMMAND);
				cta.askQuestion();
			}
		}
		// PluginServices.getMainFrame().setSelectedTool("SELECT");
		// PluginServices.getMainFrame().enableControls();
	}

	public static CADTool getCADTool() {
		CADToolAdapter cta=getCADToolAdapter();
		return cta.getCadTool();
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		// initFocus();
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
			view = (View) PluginServices.getMDIManager().getActiveWindow();
			mapControl = view.getMapControl();
			FLayer[] layers = mapControl.getMapContext().getLayers()
					.getActives();
			if (!(layers[0] instanceof FLyrAnnotation)) {
				return true;
			}
		}
		return false;
	}

	public MapControl getMapControl() {
		return getEditionManager().getMapControl();
	}

	class KeyAction extends AbstractAction {

		private String key;

		public KeyAction(String key) {
			this.key = key;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			view.focusConsole(key);
		}

	}

	class MyAction extends AbstractAction {
		private String actionCommand;

		public MyAction(String command) {
			actionCommand = command;
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			CADToolAdapter cta=getCADToolAdapter();
			cta.keyPressed(actionCommand);
		}

	}

	/**
	 * @author fjp
	 *
	 * La idea es usar esto para recibir lo que el usuario escribe y enviarlo a
	 * la consola de la vista para que salga por allí.
	 */
	private class myKeyEventPostProcessor implements KeyEventPostProcessor {

		public boolean postProcessKeyEvent(KeyEvent e) {
			// System.out.println("KeyEvent e = " + e);
			CADToolAdapter cta=getCADToolAdapter();
			if ((cta == null) || (view == null))
				return false;

			if (cta.getMapControl() == null){
				return false;
			}

			if (e.getID() != KeyEvent.KEY_RELEASED)
				return false;
			if (!(e.getComponent() instanceof JTextComponent)) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					cta.keyPressed("eliminar");
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					cta.keyPressed("escape");
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// TODO: REVISAR ESTO CUANDO VIENE UN INTRO DESDE UN
					// JTEXTAREA
					// QUE NO ES EL DE CONSOLA
					if (!(e.getSource() instanceof JTable))
						view.focusConsole("");
				}
				else if ((!e.isActionKey())) {
						//if (Character.isLetterOrDigit(e.getKeyChar())) {
							Character keyChar = new Character(e.getKeyChar());
							if (e.getComponent().getName() != null) {
								System.out
										.println("Evento de teclado desde el componente "
												+ e.getComponent().getName());
								if (!e.getComponent().getName().equals(
										"CADConsole")) {
									view.focusConsole(keyChar + "");
								}
							} else {
								if (!(e.getComponent() instanceof JTextComponent)) {
									view.focusConsole(keyChar + "");
								}
							}
						//}
					}
				}
			return false;
		}

	}

	/*
	 * private void registerKeyStrokes(){ for (char key = '0'; key <= '9';
	 * key++){ Character keyChar = new Character(key);
	 * mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),
	 * keyChar); mapControl.getActionMap().put(keyChar, new
	 * KeyAction(keyChar+"")); } for (char key = 'a'; key <= 'z'; key++){
	 * Character keyChar = new Character(key);
	 * mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),
	 * keyChar); mapControl.getActionMap().put(keyChar, new
	 * KeyAction(keyChar+"")); } for (char key = 'A'; key <= 'Z'; key++){
	 * Character keyChar = new Character(key);
	 * mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),
	 * keyChar); mapControl.getActionMap().put(keyChar, new
	 * KeyAction(keyChar+"")); }
	 * //this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
	 * 0), "enter"); //this.getActionMap().put("enter", new MyAction("enter"));
	 * Character keyChar = new
	 * Character(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0).getKeyChar());
	 * mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
	 * 0),keyChar); mapControl.getActionMap().put(keyChar, new KeyAction(""));
	 *  // El espacio como si fuera INTRO Character keyCharSpace = new
	 * Character(' ');
	 * mapControl.getInputMap(MapControl.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('
	 * '), keyCharSpace); mapControl.getActionMap().put(keyCharSpace, new
	 * KeyAction(""));
	 *
	 *  }
	 */
	private static JPopupMenu popup = new JPopupMenu();

	public static void clearMenu() {
		popup.removeAll();
	}

	public static void addMenuEntry(String text) {
		JMenuItem menu = new JMenuItem(text);
		menu.setActionCommand(text);
		menu.setEnabled(true);
		menu.setVisible(true);
		menu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CADToolAdapter cta=getCADToolAdapter();
				cta.transition(e.getActionCommand());
			}
		});

		popup.add(menu);
	}

	public static void showPopup(MouseEvent e) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	public static View getView() {
		return view;
	}

	public static void clearView() {
		view = null;
	}

	/**
	 * @return Returns the editionManager.
	 */
	public static EditionManager getEditionManager() {
		CADToolAdapter cta=getCADToolAdapter();
		return cta.getEditionManager();
	}

	public static CADTool[] getCADTools() {
		return CADToolAdapter.getCADTools();
	}

	public static void initFocus() {
		view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapControl = view.getMapControl();
		CADToolAdapter cta=getCADToolAdapter();
		if (!mapControl.getNamesMapTools().containsKey("cadtooladapter")){
			// StatusBarListener sbl=new StatusBarListener(view.getMapControl());
			// mapControl.addMapTool("cadtooladapter",  new Behavior[]{adapter,new MouseMovementBehavior(sbl)});
			mapControl.addMapTool("cadtooladapter", cta);
		}
		// view.getMapControl().setTool("cadtooladapter");
		JEditTextArea jeta=view.getConsolePanel().getTxt();
		jeta.requestFocusInWindow();
		jeta.setCaretPosition(jeta.getText().length());

		view.addConsoleListener("cad", new ResponseListener() {
			public void acceptResponse(String response) {
				CADToolAdapter cta=getCADToolAdapter();
				cta.textEntered(response);
				// TODO:
				// FocusManager fm=FocusManager.getCurrentManager();
				// fm.focusPreviousComponent(mapControl);
				/*
				 * if (popup.isShowing()){ popup.setVisible(false); }
				 */

			}
		});
		cta.getEditionManager().setMapControl(mapControl);
		view.getMapControl().setTool("cadtooladapter");
	}
}
