package com.iver.cit.gvsig.gui.cad.createTable;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

public class NewTableFinishAction extends FinishAction {
    private static final Logger logger = Logger
	    .getLogger(NewTableFinishAction.class);

    private FinishAction oldAction;
    private NewTableWizard wizard;
    private Project project;

    public NewTableFinishAction(JWizardComponents wizardComponents,
	    NewTableWizard wizard, Project project) {
	super(wizardComponents);
	this.oldAction = wizardComponents.getFinishAction();
	this.wizard = wizard;
	this.project = project;
    }

    @Override
    public void performAction() {
	DataSource dataSource;
	EditableAdapter adapter = new EditableAdapter();
	try {
	    dataSource = wizard.createTable();
	    adapter.setOriginalDataSource(new SelectableDataSource(dataSource));
	} catch (Exception e) {
	    logger.error("Cannot create table", e);
	    NotificationManager.showMessageError(e.getLocalizedMessage(), e);
	    return;
	}

	ProjectTable projectTable = ProjectTableFactory.createTable(
		dataSource.getName(), adapter);
	projectTable.setProject(project, 0);
	projectTable.setProjectDocumentFactory(Project
		.getProjectDocumentFactory(ProjectTableFactory.registerName));

	Table table = new Table();
	table.setModel(projectTable);
	PluginServices.getMDIManager().addWindow(table);

	project.addDocument(projectTable);
	project.setModified(true);

	oldAction.performAction();
    }
}