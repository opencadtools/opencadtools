package com.iver.cit.gvsig;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.hardcode.driverManager.Driver;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.rules.IRule;
import com.iver.cit.gvsig.fmap.edition.rules.RulePolygon;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.Legend;
import com.iver.cit.gvsig.gui.Table;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.tokenmarker.ConsoleToken;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.ProjectTable;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.utiles.console.jedit.KeywordMap;
import com.iver.utiles.console.jedit.Token;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StartEditing extends Extension {

//	private class MyAction extends AbstractAction
//	{
//
//		public void actionPerformed(ActionEvent e) {
//			System.err.println("F3");
//		}
//
//	}

	View vista;
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		CADExtension.initFocus();
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
				.getActiveWindow();

		if (f instanceof View) {
			vista = (View) f;

			vista.showConsole();
			MapControl mapControl = vista.getMapControl();
			EditionManager editionManager=CADExtension.getEditionManager();
			editionManager.setMapControl(mapControl);

			ProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			FLayers layers = mapa.getLayers();
			boolean bEditingStarted = false;
			for (int i = 0; i < layers.getLayersCount(); i++) {
				if (layers.getLayer(i) instanceof FLyrVect
						&& layers.getLayer(i).isActive()) {
					/*
					 * for (int j = 0; j < i; j++) {
					 * layers.getLayer(j).setVisible(false); }
					 */

					FLyrVect lv = (FLyrVect) layers.getLayer(i);
					// lv.setVisible(true);
					lv.addLayerListener(editionManager);
					try {
						Legend legendOriginal=lv.getLegend().cloneLegend();

	            		if (!lv.isWritable())
	            		{
	        				JOptionPane.showMessageDialog(
	        						(Component) PluginServices.getMDIManager().getActiveWindow(),
	        						PluginServices.getText(this, "this_layer_is_not_self_editable"),
	        						PluginServices.getText(this, "warning_title"), 
	        						JOptionPane.WARNING_MESSAGE);
	            		}
	                	
						lv.setEditing(true);
						VectorialEditableAdapter vea = (VectorialEditableAdapter) lv
						.getSource();
						
						vea.getRules().clear();
						if (vea.getShapeType() == FShape.POLYGON)
						{
							IRule rulePol = new RulePolygon();
							vea.getRules().add(rulePol);
						}

						if (!(lv.getSource().getDriver() instanceof IndexedShpDriver)){
							VectorialLayerEdited vle=(VectorialLayerEdited)editionManager.getLayerEdited(lv);
							vle.setLegend(legendOriginal);
						}
						vea.getCommandRecord().addCommandListener(mapControl);
						// Si existe una tabla asociada a esta capa se cambia su
						// modelo por el VectorialEditableAdapter.
						ProjectExtension pe = (ProjectExtension) PluginServices
								.getExtension(ProjectExtension.class);
						ProjectTable pt = pe.getProject().getTable(lv);
						if (pt != null){
							pt.setModel(vea);
							changeModelTable(pt);
						}
						startCommandsApplicable(vista,lv);
						vista.repaintMap();
					} catch (EditionException e) {
						e.printStackTrace();
						NotificationManager.addError(e);
					} catch (DriverIOException e) {
						e.printStackTrace();
						NotificationManager.addError(e);
					} catch (XMLException e) {
						e.printStackTrace();
					}

//					return;
				}
			}

			/*
			 * PluginServices.getMDIManager().setWaitCursor(); try { if
			 * (((FLyrVect) capa).getSource().getDriver().getClass() ==
			 * DXFCadDriver.class) { if (JOptionPane.showConfirmDialog(
			 * (Component) PluginServices.getMainFrame(), "Todas las geometrías
			 * del formato DXF no se pueden editar, de momento podemos editar:
			 * Line, Point, Polyline, Arc, Circle y Ellipse. \n El resto de
			 * geometrías se perderán con la edición. \n ¿Desea continuar?") ==
			 * JOptionPane.YES_OPTION) { capa.startEdition();
			 * vista.getMapControl().setCadTool("selection"); } else { } } else {
			 * capa.startEdition();
			 * vista.getMapControl().setCadTool("selection"); } } catch
			 * (EditionException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 * PluginServices.getMDIManager().restoreCursor();
			 */
			// vista.getMapControl().drawMap(false);
		}
	}
//	 private void registerKeyStrokes() {
//		 JComponent theComponent = vista.getConsolePanel().getTxt();
//
//		 // The actions
//		 Action F3Action = new AbstractAction("REFENT") {
//			public void actionPerformed(ActionEvent evt) {
//				System.err.println("SOY F3");
//			}
//		};
//
//		 InputMap inputMap = theComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//		 inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), F3Action.getValue(Action.NAME));
//
//		 ActionMap actionMap = theComponent.getActionMap();
//		 // actionMap.put("REFENT", new MyAction());
//		 actionMap.put(F3Action.getValue(Action.NAME), F3Action);
//
//	}

	public static void startCommandsApplicable(View vista,FLyrVect lv) {
		if (vista==null)
			vista=(View)PluginServices.getMDIManager().getActiveWindow();
		CADTool[] cadtools = CADExtension.getCADTools();
		KeywordMap keywordMap = new KeywordMap(true);
		for (int i = 0; i < cadtools.length; i++) {
			try {
				if (cadtools[i].isApplicable(lv.getShapeType())){
					keywordMap.add(cadtools[i].getName(), Token.KEYWORD2);
					keywordMap.add(cadtools[i].toString(), Token.KEYWORD3);
				}
			} catch (com.iver.cit.gvsig.fmap.DriverException e) {
				e.printStackTrace();
			}

		}
		ConsoleToken consoletoken = new ConsoleToken(keywordMap);
		vista.getConsolePanel().setTokenMarker(consoletoken);

	}

	private void changeModelTable(ProjectTable pt){
    	 com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices.getMDIManager().getAllWindows();

 		for (int i=0 ; i<views.length ; i++){
 			if (views[i] instanceof Table){
 				Table table=(Table)views[i];
 				ProjectTable model =table.getModel();
 				if (model.equals(pt)){
 						table.setModel(pt);
 				}
 			}
 		}
    }
	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		FLayer[] selected = f.getModel().getMapContext().getLayers()
				.getActives();
		if (selected.length == 1 && selected[0].isAvailable() && selected[0] instanceof FLyrVect) {
			if (selected[0].isEditing())
				return false;
			return true;
		}
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
				.getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View)
			return true;
		return false;
	}
}
