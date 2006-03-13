package com.iver.cit.gvsig;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import jwizardcomponent.Utilities;
import jwizardcomponent.example.SimpleDynamicWizardPanel;
import jwizardcomponent.example.SimpleLabelWizardPanel;
import jwizardcomponent.frame.SimpleLogoJWizardFrame;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.WriterManager;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.ChooseWriteDriver;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class NewTheme implements Extension {
	static ImageIcon LOGO;

	private LayerDefinition lyrDef;
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

			LOGO = new javax.swing.ImageIcon(this.getClass().getClassLoader()
					.getResource("images/package_graphics.png"));
			// new
			// ImageIcon(DefaultJWizardComponents.class.getResource("images/logo.jpeg"));

			SimpleLogoJWizardFrame wizardFrame = new SimpleLogoJWizardFrame(
					LOGO);
			wizardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			SwingUtilities.updateComponentTreeUI(wizardFrame);

			wizardFrame.setTitle("Creación de un nuevo Tema");

			WriterManager writerManager = LayerFactory.getWM();
			ArrayList spatialDrivers = new ArrayList();
			String[] writerNames = writerManager.getWriterNames();
			for (int i = 0; i < writerNames.length; i++) {
				Driver drv = writerManager.getWriter(writerNames[i]);
				if (drv instanceof ISpatialWriter)
					spatialDrivers.add(drv.getName());
			}

			wizardFrame.getWizardComponents().addWizardPanel(
					new ChooseWriteDriver(wizardFrame.getWizardComponents(),
							"Dynamic Test", (String[]) spatialDrivers
									.toArray(new String[0])));

			wizardFrame.getWizardComponents().addWizardPanel(
					new ChooseGeometryType(wizardFrame.getWizardComponents()));

			wizardFrame.getWizardComponents()
					.addWizardPanel(
							new JPanelFieldDefinition(wizardFrame
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
