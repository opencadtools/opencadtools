package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.commands.CommandListener;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.command.CommandStackDialog;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class ViewCommandStackExtension extends Extension implements
	CommandListener {
    public static CommandStackDialog csd = null;

    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    public void initialize() {
	PluginServices.getIconTheme().registerDefault(
		"commands-stack",
		this.getClass().getClassLoader()
			.getResource("images/commandstack.png"));
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    public void execute(String s) {
	com.iver.andami.ui.mdiManager.IWindow f = PluginServices
		.getMDIManager().getActiveWindow();

	View vista = (View) f;
	IProjectView model = vista.getModel();
	MapContext mapa = model.getMapContext();
	FLayers layers = mapa.getLayers();
	if (s.equals("COMMANDSTACK")) {
	    for (int i = 0; i < layers.getLayersCount(); i++) {
		if (layers.getLayer(i) instanceof FLyrVect) {
		    FLyrVect lyrVect = (FLyrVect) layers.getLayer(i);
		    if (lyrVect.isEditing() && lyrVect.isActive()) {
			VectorialEditableAdapter vea = (VectorialEditableAdapter) lyrVect
				.getSource();
			vea.getCommandRecord().addCommandListener(this);
			csd = new CommandStackDialog();
			csd.setModel(((IEditableSource) lyrVect.getSource())
				.getCommandRecord());
			PluginServices.getMDIManager().addWindow(csd);
			return;
		    }
		}
	    }
	}

	// PluginServices.getMainFrame().enableControls();

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
	    return true;
	}
	return false;

    }

    public void commandRepaint() {
	try {
	    CADTool cadTool = CADExtension.getCADTool();
	    if (cadTool != null) {
		cadTool.clearSelection();
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}

    }

    public void commandRefresh() {
	try {
	    CADTool cadTool = CADExtension.getCADTool();
	    if (cadTool != null) {
		cadTool.clearSelection();
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
    }
}
