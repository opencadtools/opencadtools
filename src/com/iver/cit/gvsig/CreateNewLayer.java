package com.iver.cit.gvsig;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.gui.cad.MyFinishAction;
import com.iver.cit.gvsig.gui.cad.createLayer.NewDXFLayerWizard;
import com.iver.cit.gvsig.gui.cad.createLayer.NewLayerWizard;
import com.iver.cit.gvsig.gui.cad.createLayer.NewPostgisLayerWizard;
import com.iver.cit.gvsig.gui.cad.createLayer.NewSHPLayerWizard;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * @author Vicente Caballero Navarro
 */
public class CreateNewLayer extends Extension {
    private static final ImageIcon LOGO = new ImageIcon(CreateNewLayer.class
	    .getClassLoader().getResource("images/package_graphics.png"));;

    private static final Map<String, NewLayerWizard> wizards = new HashMap<String, NewLayerWizard>();

    @Override
    public void initialize() {
	wizards.put("SHP", new NewSHPLayerWizard());
	wizards.put("POSTGIS", new NewPostgisLayerWizard());
	wizards.put("DXF", new NewDXFLayerWizard());
    }

    /**
     * Adds a new wizard to the create layer extension.
     * 
     * Note that this does not add any menu/toolbar. It only attaches the given
     * wizard so it can be used by a later call to {@link #create(String, View)}
     * method.
     * 
     * @param type
     *            The type of layer to create. This string must be used later to
     *            call the {@link #create(String, View)} method.
     * @param wizard
     *            The specific wizard to use with this type of layer.
     */
    public static void addWizard(String type, NewLayerWizard wizard) {
	wizards.put(type, wizard);
    }

    @Override
    public void execute(String actionCommand) {
	IWindow window = PluginServices.getMDIManager().getActiveWindow();

	if (window instanceof View) {
	    create(actionCommand, (View) window);
	}
    }

    /**
     * Opens a new wizard in order to create a new layer.
     * 
     * @param type
     *            The type of layer to create. A wizard must be previously added
     *            with this string using the
     *            {@link #addWizard(String, NewLayerWizard)} method.
     * @param view
     *            The view where the new layer may be added.
     */
    public static void create(String type, View view) {
	try {
	    CADExtension.getCADToolAdapter()
		    .setMapControl(view.getMapControl());

	    WizardAndami wizard = new WizardAndami(LOGO);
	    JWizardComponents components = wizard.getWizardComponents();
	    WindowInfo info = wizard.getWindowInfo();

	    NewLayerWizard newLayerWizard = wizards.get(type);

	    if (newLayerWizard == null) {
		return;
	    }

	    JWizardPanel[] panels = newLayerWizard.getPanels(wizard);
	    for (JWizardPanel panel : panels) {
		components.addWizardPanel(panel);
	    }

	    components.setFinishAction(new MyFinishAction(components, view,
		    newLayerWizard));
	    components.getBackButton().setEnabled(false);
	    components.getNextButton().setEnabled(panels.length > 1);
	    components.getFinishButton().setEnabled(false);

	    String title = PluginServices.getPluginServices(
		    "com.iver.cit.gvsig").getText("new_layer");
	    info.setTitle(title);
	    info.setWidth(640);
	    info.setHeight(350);

	    PluginServices.getMDIManager().addWindow(wizard);
	} catch (DriverLoadException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
    }

    @Override
    public boolean isEnabled() {
	return true;
    }

    @Override
    public boolean isVisible() {
	IWindow f = PluginServices.getMDIManager().getActiveWindow();
	return (f instanceof BaseView);
    }
}