package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.commands.CommandListener;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.command.CommandStackDialog;
import com.iver.cit.gvsig.project.ProjectView;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ViewCommandStackExtension extends Extension implements CommandListener{
	public static CommandStackDialog csd=null;
	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String s) {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
				.getActiveView();

		View vista = (View) f;
		ProjectView model = vista.getModel();
		MapContext mapa = model.getMapContext();
		FLayers layers = mapa.getLayers();
		if (s.equals("COMMANDSTACK")) {
			for (int i =0;i<layers.getLayersCount();i++){
				if (layers.getLayer(i) instanceof FLyrVect){
					FLyrVect lyrVect=(FLyrVect)layers.getLayer(i);
					if (lyrVect.isEditing() && lyrVect.isActive()){
						VectorialEditableAdapter vea = (VectorialEditableAdapter) lyrVect
						.getSource();
						vea.getCommandRecord().addCommandListener(this);
						csd=new CommandStackDialog();
						csd.setModel(((IEditableSource)lyrVect.getSource()).getCommandRecord());
						PluginServices.getMDIManager().addView(csd);
						return;
					}
				}
			}
		}

		//PluginServices.getMainFrame().enableControls();

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
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		return false;

	}

	public void commandRepaint() {
		CADExtension.getCADTool().clearSelection();

	}

	public void commandRefresh() {
		CADExtension.getCADTool().clearSelection();
	}
}
