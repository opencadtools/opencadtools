package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.Table;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.command.CommandStackDialog;
import com.iver.cit.gvsig.project.ProjectTable;
import com.iver.cit.gvsig.project.ProjectView;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class TableCommandStackExtension implements Extension {
	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void inicializar() {
	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String s) {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		Table table = (Table) f;
		ProjectTable model = table.getModel();
		if (s.equals("COMMANDSTACK")) {
			CommandRecord cr=null;

			if (model.getAssociatedTable()!=null){
				cr=((IEditableSource)((FLyrVect)model.getAssociatedTable()).getSource()).getCommandRecord();
				cr.addExecuteCommand(table);
			}else{
				cr=model.getModelo().getCommandRecord();
				cr.addExecuteCommand(table);
			}
			CommandStackDialog csd = new CommandStackDialog(cr);
			PluginServices.getMDIManager().addView(csd);
		}
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
		.getActiveView();
		if (f instanceof Table){
		Table table = (Table) f;
		ProjectTable model = table.getModel();
		if (model.getModelo().isEditing())
			return true;
		}
			return false;

	}
}
