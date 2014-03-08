package com.iver.cit.gvsig.export;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public abstract class AbstractLayerExporter implements LayerExporter {
    protected String lastPath = null;

    /**
     * Lanza un thread en background que escribe las features. Cuando termina,
     * pregunta al usuario si quiere a�adir la nueva capa a la vista. Para eso
     * necesita un driver de lectura ya configurado.
     * 
     * @param mapContext
     * @param layer
     * @param writer
     * @param reader
     * @throws ReadDriverException
     * @throws DriverException
     * @throws DriverIOException
     */
    protected void writeFeatures(MapContext mapContext, FLyrVect layer,
	    IWriter writer, Driver reader) throws ReadDriverException {
	PluginServices.cancelableBackgroundExecution(new WriterTask(mapContext,
		layer, writer, reader));
    }
}
