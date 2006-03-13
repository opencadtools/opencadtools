package com.iver.cit.gvsig.gui.cad;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.ChooseWriteDriver;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;

public class MyFinishAction extends FinishAction
{
	JWizardComponents myWizardComponents;
	FinishAction oldAction;
	LayerDefinition lyrDef = null;
	public MyFinishAction(JWizardComponents wizardComponents) {		
		super(wizardComponents);
		oldAction = wizardComponents.getFinishAction();
		myWizardComponents = wizardComponents;
		// TODO Auto-generated constructor stub
	}

	public void performAction() {
		// TODO Auto-generated method stub
		try {
			ChooseWriteDriver driverPanel = (ChooseWriteDriver) myWizardComponents.getWizardPanel(0);
			ChooseGeometryType geometryTypePanel = (ChooseGeometryType) myWizardComponents.getWizardPanel(1);
			JPanelFieldDefinition fieldDefinitionPanel = (JPanelFieldDefinition) myWizardComponents.getWizardPanel(1);
			 
			String layerName = driverPanel.getLayerName();
			String selectedDriver = driverPanel.getSelectedDriver();
			int geometryType = geometryTypePanel.getSelectedGeometryType();
			FieldDescription[] fieldsDesc = fieldDefinitionPanel.getFieldsDescription();
			
			ISpatialWriter drv = (ISpatialWriter) LayerFactory.getWM().getWriter(selectedDriver);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Para cerrar el cuadro de diálogo.
		oldAction.performAction();
		
	}
	  
}

