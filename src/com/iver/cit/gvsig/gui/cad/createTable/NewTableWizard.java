package com.iver.cit.gvsig.gui.cad.createTable;

import jwizardcomponent.JWizardPanel;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.ui.wizard.WizardAndami;

public interface NewTableWizard {
    /**
     * Returns the panels of this wizard.
     * 
     * @param wizard
     *            The parent andami wizard panel.
     * @return the panels of this wizard.
     * @throws DriverLoadException
     *             if any driver error occurs while initializing the panels.
     */
    JWizardPanel[] getPanels(WizardAndami wizard) throws DriverLoadException;

    /**
     * Creates a new table using the information from the panels returned in
     * {@link #getPanels(WizardAndami)}.
     * 
     * @return the new data source or <code>null</code> if the table cannot be
     *         created.
     * @throws Exception
     *             if any error occurs while creating the table.
     */
    DataSource createTable() throws Exception;
}