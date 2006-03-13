package com.iver.cit.gvsig.gui.cad;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

public class MyFinishAction extends FinishAction
{
	JWizardComponents myWizardComponents;
	FinishAction oldAction;
	public MyFinishAction(JWizardComponents wizardComponents) {		
		super(wizardComponents);
		oldAction = wizardComponents.getFinishAction();
		myWizardComponents = wizardComponents;
		// TODO Auto-generated constructor stub
	}

	public void performAction() {
		// TODO Auto-generated method stub
		try {
			JWizardPanel lastPanel = myWizardComponents.getCurrentPanel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Para cerrar el cuadro de diálogo.
		oldAction.performAction();
		
	}
	  
}

