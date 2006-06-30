package com.iver.cit.gvsig.gui.cad;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import jwizardcomponent.CancelAction;
import jwizardcomponent.DefaultJWizardComponents;
import jwizardcomponent.FinishAction;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.View;
import com.iver.andami.ui.mdiManager.ViewInfo;
import com.iver.cit.gvsig.gui.dialogs.CSSelectionDialog;

public class WizardAndami extends JPanel implements View {
	ViewInfo viewInfo = null;
	WizardPanelWithLogo wizardPanel;
	
	// No deberían necesitarse un FinishAction y un CancelAction, pero bueno, 
	// lo mantengo por ahora.
	private class CloseAction extends FinishAction
	{
		View v;
		public CloseAction(View view)
		{
			super(wizardPanel.getWizardComponents());
			v = view;
		}
		public void performAction() {
			PluginServices.getMDIManager().closeView(v);
		}
		
	}
	private class CloseAction2 extends CancelAction
	{

		View v;
		public CloseAction2(View view)
		{
			super(wizardPanel.getWizardComponents());
			v = view;
		}
		public void performAction() {
			PluginServices.getMDIManager().closeView(v);
		}
		
	}
	
	
	public WizardAndami(ImageIcon logo)
	{
		wizardPanel = new WizardPanelWithLogo(logo);
		CloseAction closeAction = new CloseAction(this);
		CloseAction2 closeAction2 = new CloseAction2(this);
		wizardPanel.getWizardComponents().setFinishAction(closeAction);
		wizardPanel.getWizardComponents().setCancelAction(closeAction2);
		
		this.setLayout(new BorderLayout());
		this.add(wizardPanel, BorderLayout.CENTER);
	}
	
	public DefaultJWizardComponents getWizardComponents()
	{
		return wizardPanel.getWizardComponents();
	}
	
	
	public ViewInfo getViewInfo() {
		if (viewInfo == null)
		{
			viewInfo = new ViewInfo(ViewInfo.MODALDIALOG|ViewInfo.RESIZABLE);			
		}
		return viewInfo;
	}

}
