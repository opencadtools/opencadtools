package com.iver.cit.gvsig.gui.preferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapper;
import com.iver.cit.gvsig.project.documents.view.snapping.gui.SnapConfig;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/**
 * Preferencias de snapping.
 *
 * @author Vicente Caballero Navarro
 */
public class SnapConfigPage extends AbstractPreferencePage {
    private static Preferences prefs = Preferences.userRoot().node("snappers");
    private ImageIcon icon;
    private SnapConfig snapConfig;
    private ArrayList snappers = new ArrayList();
	private static boolean applySnappers=true;
    private static TreeMap selected = new TreeMap(new Comparator() {
        public int compare(Object o1, Object o2) {
            if ((o1 != null) && (o2 != null)) {
                ISnapper v2 = (ISnapper) o2;
                ISnapper v1 = (ISnapper) o1;
                if (v1.getPriority()>v2.getPriority())
                	return -1;
                else if (v1.getPriority()<v2.getPriority())
            		return 1;
                else if (v1.getClass() == v2.getClass()) {
                	return 0;
                }else
                	return -1;
            }
            return 0;
        }
    }); // Para poder ordenar

    static {
    	new SnapConfigPage().initializeValues();
    }


    public SnapConfigPage() {
        super();
        icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/Snapper.png"));
        snapConfig = new SnapConfig();
        snappers = getSnappers();
        snapConfig.setSnappers(snappers);
        addComponent(snapConfig);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static ArrayList getSnappers() {
        ArrayList snappers = new ArrayList();
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();

        ExtensionPoint extensionPoint = (ExtensionPoint) extensionPoints.get(
                "Snapper");
        Iterator iterator = extensionPoint.keySet().iterator();

        while (iterator.hasNext()) {
            try {
                ISnapper snapper = (ISnapper) extensionPoint.create((String) iterator.next());
                snappers.add(snapper);
            } catch (InstantiationException e) {
            	NotificationManager.addError(e.getMessage(),e);
            } catch (IllegalAccessException e) {
            	NotificationManager.addError(e.getMessage(),e);
            }
        }

        return snappers;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ArrayList getActivesSnappers() {
       if (!applySnappers)
    	   return new ArrayList();
       return new ArrayList(selected.keySet());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws StoreException DOCUMENT ME!
     */
    public void storeValues() throws StoreException {
        selected.clear();
    	for (int n = 0; n < snappers.size(); n++) {
            Boolean b = (Boolean) snapConfig.getTableModel().getValueAt(n, 0);
            ISnapper snp = (ISnapper) snappers.get(n);
            String nameClass=snp.getClass().getName();
            nameClass=nameClass.substring(nameClass.lastIndexOf('.'));
            prefs.putBoolean("snapper_activated" + nameClass, b.booleanValue());
            if (b.booleanValue())
            	selected.put(snp, b);
            prefs.putInt("snapper_priority"+ nameClass,snp.getPriority());
        }
        boolean b=snapConfig.applySnappers();
        prefs.putBoolean("apply-snappers",b);
        applySnappers=b;
    }

    /**
     * DOCUMENT ME!
     */
    public void setChangesApplied() {
    	setChanged(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getID() {
        return this.getClass().getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return PluginServices.getText(this, "Snapping");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JPanel getPanel() {
        return this;
    }

    /**
     * DOCUMENT ME!
     */
    public void initializeValues() {
        for (int n = 0; n < snappers.size(); n++) {
            ISnapper snp = (ISnapper) snappers.get(n);
            String nameClass=snp.getClass().getName();
            nameClass=nameClass.substring(nameClass.lastIndexOf('.'));
            boolean select = prefs.getBoolean("snapper_activated" + nameClass, false);
            if (select)
            	selected.put(snp, new Boolean(select));
            int priority = prefs.getInt("snapper_priority" + nameClass,3);
            snp.setPriority(priority);
        }
        applySnappers = prefs.getBoolean("apply-snappers",true);
        snapConfig.setApplySnappers(applySnappers);
        snapConfig.selectSnappers(selected);

    }

    /**
     * DOCUMENT ME!
     */
    public void initializeDefaults() {
        initializeValues();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isValueChanged() {
    	return super.hasChanged();
    }
}
