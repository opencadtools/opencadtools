package com.iver.cit.gvsig.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.iver.cit.gvsig.fmap.layers.FLayer;

public class CADListenerManager {

    private static HashMap<String, EndGeometryListener> endGeometryListeners = new HashMap<String, EndGeometryListener>();

    public static void addEndGeometryListener(String key,
	    EndGeometryListener lis) {
	endGeometryListeners.put(key, lis);
    }

    public static void removeEndGeometryListener(String key) {
	endGeometryListeners.remove(key);
    }

    public static void endGeometry(FLayer layer, String cadToolKey) {

	Set<String> keys = endGeometryListeners.keySet();
	Iterator<String> iterator = keys.iterator();

	while (iterator.hasNext()) {
	    String key = iterator.next();
	    endGeometryListeners.get(key).endGeometry(layer, cadToolKey);
	}
    }

}
