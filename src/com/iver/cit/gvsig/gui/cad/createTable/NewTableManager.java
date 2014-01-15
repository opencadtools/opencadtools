package com.iver.cit.gvsig.gui.cad.createTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.gvsig.exceptions.BaseException;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.project.Project;

public class NewTableManager {
    private static final ImageIcon LOGO = new ImageIcon(NewTableManager.class
	    .getClassLoader().getResource("images/logo_wizard.png"));

    private static NewTableManager instance = new NewTableManager();

    public static NewTableManager getInstance() {
	return instance;
    }

    private Map<String, NewTableWizard> wizards = new HashMap<String, NewTableWizard>();

    private NewTableManager() {
	addWizard(PluginServices.getText(this, "dbf_table"),
		new NewDBFTableWizard());
    }

    /**
     * Adds a new create table wizard.
     * 
     * @param type
     *            The type of table
     * @param wizard
     *            The table creation wizard
     */
    public void addWizard(String type, NewTableWizard wizard) {
	wizards.put(type, wizard);
    }

    /**
     * Creates a new table by showing a wizard to the user.
     * 
     * @param project
     *            The project where the table should be added
     * @throws BaseException
     *             if any problem occurs while creating the table
     */
    public void create(Project project) throws BaseException {
	String[] types = wizards.keySet().toArray(new String[0]);
	Arrays.sort(types);

	WizardAndami wizard = new WizardAndami(LOGO);
	JWizardComponents components = wizard.getWizardComponents();
	WindowInfo info = wizard.getWindowInfo();
	NewTableChoosePanel window = new NewTableChoosePanel(types);
	PluginServices.getMDIManager().addWindow(window);
	if (window.getSelectedFormat() == null) {
	    return;
	}

	NewTableWizard newTableWizard = wizards.get(window.getSelectedFormat());
	if (newTableWizard == null) {
	    return;
	}

	JWizardPanel[] panels = newTableWizard.getPanels(wizard);
	for (JWizardPanel panel : panels) {
	    components.addWizardPanel(panel);
	}

	components.setFinishAction(new NewTableFinishAction(components,
		newTableWizard, project));
	components.getBackButton().setEnabled(false);
	components.getNextButton().setEnabled(panels.length > 1);
	components.getFinishButton().setEnabled(false);

	String title = PluginServices.getPluginServices("com.iver.cit.gvsig")
		.getText("new_table");
	info.setTitle(title);
	info.setWidth(640);
	info.setHeight(350);

	PluginServices.getMDIManager().addWindow(wizard);
    }
}
