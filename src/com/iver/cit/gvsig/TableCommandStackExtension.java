package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.command.CommandStackDialog;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class TableCommandStackExtension extends Extension {
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
				.getActiveWindow();

		Table table = (Table) f;
		ProjectTable model = table.getModel();
		if (s.equals("COMMANDSTACK")) {
			CommandRecord cr=null;

			if (model.getAssociatedTable()!=null){
				cr=((IEditableSource)((FLyrVect)model.getAssociatedTable()).getSource()).getCommandRecord();
				cr.addCommandListener(table);
			}else{
				cr=model.getModelo().getCommandRecord();
				cr.addCommandListener(table);
			}
			CommandStackDialog csd = new CommandStackDialog();
			csd.setModel(cr);
			PluginServices.getMDIManager().addWindow(csd);
		}
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
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
		.getActiveWindow();
		if (f instanceof Table){
		Table table = (Table) f;
		ProjectTable model = table.getModel();
		if (model.getModelo().isEditing())
			return true;
		}
			return false;

	}
}
