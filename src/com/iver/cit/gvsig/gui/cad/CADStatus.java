package com.iver.cit.gvsig.gui.cad;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiFrame.JToolBarToggleButton;
import com.iver.andami.ui.mdiFrame.MDIFrame;
import com.iver.utiles.XMLEntity;

public class CADStatus {

	private final static String VERTEX_KEY = "snapperFinalPoint";
	private final static String EDGE_KEY = "snapperNearestPoint";
	private final static String FOLLOW_KEY = "followGeometry";
	private final static String DELETEBUTTON_KEY = "isDeleteButton3";
	private final static String SNAP_GROUP = "snappers";
	private final static String FOLLOW_GROUP = "follow";

	private boolean vertexActivated;
	private boolean nearLineActivated;
	private boolean followGeometryActivated;
	private boolean deleteButtonActivated;

	private static CADStatus cadStatus;

	private CADStatus() {
		cadStatus = this;
		vertexActivated = true;
		nearLineActivated = true;
		followGeometryActivated = false;
		deleteButtonActivated = false;
	}

	public static CADStatus getCADStatus() {
		if (cadStatus == null) {
			createInstance();
		}
		return cadStatus;
	}

	private synchronized static void createInstance() {
		if (cadStatus == null) {
			cadStatus = new CADStatus();
		}
	}

	public boolean isVertexActivated() {
		return vertexActivated;
	}

	public boolean isNearLineActivated() {
		return nearLineActivated;
	}

	public boolean isFollowGeometryActivated() {
		return followGeometryActivated;
	}

	public boolean isDeleteButtonActivated() {
		return deleteButtonActivated;
	}

	public void setVertexActivated(boolean activate) {
		vertexActivated = activate;
		saveProperty(EDGE_KEY, activate);
		toggleButton(SNAP_GROUP, activate);
	}

	public void setNearLineActivated(boolean activate) {
		nearLineActivated = activate;
		saveProperty(VERTEX_KEY, activate);
		toggleButton(SNAP_GROUP, activate);
	}

	public void setFollowGeometryActivated(boolean activate) {
		followGeometryActivated = activate;
		saveProperty(FOLLOW_KEY, activate);
		toggleButton(FOLLOW_GROUP, activate);
	}

	public void setDeleteButtonActivated(boolean activate) {
		deleteButtonActivated = activate;
		saveProperty(DELETEBUTTON_KEY, activate);
	}

	public void load() {
		setVertexActivated(getProperty(VERTEX_KEY, true));
		setNearLineActivated(getProperty(VERTEX_KEY, true));
		setFollowGeometryActivated(getProperty(FOLLOW_KEY, false));
		setDeleteButtonActivated(getProperty(DELETEBUTTON_KEY, false));
	}

	private boolean getProperty(String key, boolean defaultVal) {

		boolean value = defaultVal;
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		if (xml.contains(key)) {
			value = xml.getBooleanProperty(key);
		}

		return value;
	}

	private void saveProperty(String key, boolean value) {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		xml.putProperty(key, value);
	}

	private void toggleButton(String group, boolean pushed) {
		try {
		    MDIFrame f = ((MDIFrame)PluginServices.getMainFrame());
		    if (f.getSelectedTools() == null) {
			f.setSelectedTools(f.getInitialSelectedTools());
		    }
		    
		    if (!pushed) {
			f.setSelectedTool(group, "_empty");
		    } else {
			f.setSelectedTool(group, "_" + group);
		    }
		    
		} catch (ClassCastException e) {
		    e.printStackTrace();
		}
	}
}
