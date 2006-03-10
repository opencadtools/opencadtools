package com.iver.cit.gvsig;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import jwizardcomponent.Utilities;
import jwizardcomponent.example.SimpleDynamicWizardPanel;
import jwizardcomponent.example.SimpleLabelWizardPanel;
import jwizardcomponent.frame.SimpleLogoJWizardFrame;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.gui.View;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class NewTheme implements Extension {
	static ImageIcon LOGO;

	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void inicializar() {
	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		if (f instanceof View) {
			View vista = (View) f;

			LOGO = new javax.swing.ImageIcon(this.getClass().
					getClassLoader().getResource("images/package_graphics.png"));
			// new
			// ImageIcon(DefaultJWizardComponents.class.getResource("images/logo.jpeg"));

			SimpleLogoJWizardFrame wizardFrame = new SimpleLogoJWizardFrame(
					LOGO);
			wizardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			SwingUtilities.updateComponentTreeUI(wizardFrame);

			wizardFrame.setTitle("Creación de un nuevo Tema");

			wizardFrame.getWizardComponents()
					.addWizardPanel(
							new SimpleLabelWizardPanel(wizardFrame
									.getWizardComponents(), new JLabel(
									"Dynamic Test")));

			wizardFrame.getWizardComponents().addWizardPanel(
					new SimpleDynamicWizardPanel(wizardFrame
							.getWizardComponents()));

			wizardFrame.getWizardComponents().addWizardPanel(
					new SimpleLabelWizardPanel(wizardFrame
							.getWizardComponents(), new JLabel("Done!")));
			wizardFrame.setSize(500, 300);
			Utilities.centerComponentOnScreen(wizardFrame);
			wizardFrame.show();
		}
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveView();

		if (f == null) {
			return false;
		} else
			return true;
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		if (f == null) {
			return false;
		}

		if (f.getClass() == View.class) {
			return true;
		} else {
			return false;
		}

	}
}
