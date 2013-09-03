package com.iver.cit.gvsig.gui.cad.createLayer;

import jwizardcomponent.JWizardPanel;

import org.cresques.cts.IProjection;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public interface NewLayerWizard {
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
     * Creates a new layer using the information from the panels returned in
     * {@link #getPanels(WizardAndami)} and the given projection to create a new
     * layer.
     * 
     * @param projection
     *            the projection of the new layer
     * @return the new layer or <code>null</code> if the layer cannot be
     *         created.
     * @throws Exception
     *             if any error occurs while creating the layer.
     */
    FLyrVect createLayer(IProjection projection) throws Exception;
}
