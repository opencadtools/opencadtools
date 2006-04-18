package com.iver.cit.gvsig;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import jwizardcomponent.example.SimpleLabelWizardPanel;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverManager;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.MyFinishAction;
import com.iver.cit.gvsig.gui.cad.WizardAndami;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;
import com.iver.cit.gvsig.gui.cad.panels.PostGISpanel;
import com.iver.cit.gvsig.gui.cad.panels.ShpPanel;

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

			LOGO = new javax.swing.ImageIcon(this.getClass().getClassLoader()
					.getResource("images/package_graphics.png"));

			/* SimpleLogoJWizardFrame wizardFrame = new SimpleLogoJWizardFrame(
					LOGO);
			wizardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			SwingUtilities.updateComponentTreeUI(wizardFrame);

			wizardFrame.setTitle("Creación de un nuevo Tema"); */
			WizardAndami wizard = new WizardAndami(LOGO);

		    DriverManager writerManager = LayerFactory.getDM(); 
		    ArrayList spatialDrivers = new ArrayList();
		    String[] writerNames = writerManager.getDriverNames();
			for (int i=0; i<writerNames.length; i++)
			{
				Driver drv = writerManager.getDriver(writerNames[i]);
				if (drv instanceof ISpatialWriter)
					spatialDrivers.add(drv.getName());
			}

			ChooseGeometryType panelChoose = new ChooseGeometryType(wizard.getWizardComponents());
			JPanelFieldDefinition panelFields = new JPanelFieldDefinition(wizard.getWizardComponents());			
			wizard.getWizardComponents().addWizardPanel(panelChoose);

			wizard.getWizardComponents().addWizardPanel(panelFields);

			if (actionCommand.equals("SHP"))
			{
				panelChoose.setDriver((ISpatialWriter) writerManager.getDriver("gvSIG shp driver"));
				wizard.getWizardComponents().addWizardPanel(
					new ShpPanel(wizard.getWizardComponents()));
				
				wizard.getWizardComponents().setFinishAction(
						new MyFinishAction(wizard.getWizardComponents(),
								vista, actionCommand));
			}
			if (actionCommand.equals("DXF"))
			{
				wizard.getWizardComponents().addWizardPanel(
					new SimpleLabelWizardPanel(wizard
							.getWizardComponents(), new JLabel("Done!")));
			}
			if (actionCommand.equals("POSTGIS"))
			{
				panelChoose.setDriver((ISpatialWriter) writerManager.getDriver("PostGIS JDBC Driver"));
				wizard.getWizardComponents().addWizardPanel(
					new PostGISpanel(wizard.getWizardComponents()));
				
				wizard.getWizardComponents().setFinishAction(
						new MyFinishAction(wizard.getWizardComponents(),
								vista, actionCommand));
			}			
			
			wizard.getViewInfo().setWidth(540);
			wizard.getViewInfo().setHeight(350);
			wizard.getViewInfo().setTitle(PluginServices.getText(this,"new_theme"));
			// Utilities.centerComponentOnScreen(wizard);
			// wizardFrame.show();
			PluginServices.getMDIManager().addView(wizard);
			// System.out.println("Salgo con " + panelChoose.getLayerName());
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
