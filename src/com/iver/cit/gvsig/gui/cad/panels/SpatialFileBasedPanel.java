package com.iver.cit.gvsig.gui.cad.panels;

import jwizardcomponent.JWizardComponents;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;

public class SpatialFileBasedPanel extends FileBasedPanel {
    private CRSSelectPanel crsSelectPanel = null;

    public SpatialFileBasedPanel(JWizardComponents wizardComponents) {
	super(wizardComponents);
    }

    public SpatialFileBasedPanel(JWizardComponents wizardComponents, String extension) {
	super(wizardComponents, extension);
    }

    @Override
    protected void initialize() {
	super.initialize();
	this.add(getCrsPanel(), null);
    }

    private CRSSelectPanel getCrsPanel() {
	if (crsSelectPanel == null) {
	    crsSelectPanel = CRSSelectPanel.getPanel(AddLayerDialog
		    .getLastProjection());
	    crsSelectPanel.setBounds(new java.awt.Rectangle(16, 98, 348, 44));
	    IWindow view = PluginServices.getMDIManager().getActiveWindow();
	    if (view instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
		if (((com.iver.cit.gvsig.project.documents.view.gui.View) view)
			.getMapControl().getMapContext().getLayers()
			.getLayersCount() != 0) {
		    crsSelectPanel.getJBtnChangeProj().setEnabled(false);
		}
	    }
	    crsSelectPanel
		    .addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    if (crsSelectPanel.isOkPressed()) {
				AddLayerDialog.setLastProjection(crsSelectPanel
					.getCurProj());
			    }
			}
		    });
	}
	return crsSelectPanel;
    }
}