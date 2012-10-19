/**
 *
 */
package com.iver.cit.gvsig.writers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Types;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.AttributeType;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * @author fjp
 * 
 *         Example of using a Geotools dataStore to write ONLY the modified
 *         features. So: you put the theme in editing mode, add, modify or
 *         delete features and when you come back to non editing mode, the
 *         changes will be saved into the original shapefile.
 * 
 */
public class WriterGT2Shp extends AbstractWriter {

    FilterFactory filterFactory = FilterFactory.createFilterFactory();
    FLyrVect lyrVect;
    boolean bFromShp;
    File file;
    FeatureStore featStore;
    AttributeType[] types;
    Transaction t;
    int numReg = 0;

    public WriterGT2Shp(FLyrVect lyrVect) throws IOException {
	this.lyrVect = lyrVect;
	VectorialEditableAdapter vea = (VectorialEditableAdapter) lyrVect
		.getSource();
	VectorialDriver vd = vea.getOriginalAdapter().getDriver();
	bFromShp = false;
	if (vd instanceof VectorialFileDriver) {
	    VectorialFileDriver vfd = (VectorialFileDriver) vd;
	    file = vfd.getFile();
	    String filePath = file.getAbsolutePath();
	    if ((filePath.endsWith(".shp")) || (filePath.endsWith(".SHP"))) {
		bFromShp = true;
	    }
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.fmap.edition.IWriter#preProcess()
     */
    public void preProcess() throws StartWriterVisitorException {
	// feature attributes creation
	URL theUrl;
	try {
	    theUrl = file.toURL();
	    ShapefileDataStore dataStore = new ShapefileDataStore(theUrl);
	    String featureName = dataStore.getTypeNames()[0];
	    featStore = (FeatureStore) dataStore.getFeatureSource(featureName);
	    types = featStore.getSchema().getAttributeTypes();
	    t = new DefaultTransaction("handle");
	    featStore.setTransaction(t);

	    t.addAuthorization("handle"); // provide authoriztion

	    // types = new AttributeType[lyrVect.getRecordset().getFieldCount()
	    // +1];
	} catch (IOException e) {
	    throw new StartWriterVisitorException(getName(), e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.fmap.edition.IWriter#process(com.iver.cit.gvsig.fmap
     * .edition.IRowEdited)
     */
    public void process(IRowEdited row) throws ProcessWriterVisitorException {

	IFeature feat = (IFeature) row.getLinkedRow();
	Object[] values = new Object[types.length];
	values[0] = feat.getGeometry().toJTSGeometry();
	for (int i = 1; i < types.length; i++) {
	    values[i] = feat.getAttribute(i);
	}

	Filter theFilter = filterFactory.createFidFilter(feat.getID());
	try {

	    // Aquí habría que mirar si es una modificación, añadido o borrado
	    if ((numReg % 2) == 0) {
		featStore.modifyFeatures(types, values, theFilter);
	    } else {
		featStore.removeFeatures(theFilter);
	    }
	    numReg++;
	} catch (IOException e) {
	    throw new ProcessWriterVisitorException(getName(), e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.fmap.edition.IWriter#postProcess()
     */
    public void postProcess() throws StopWriterVisitorException {
	try {
	    t.commit(); // commit opperations
	} catch (IOException io) {
	    try {
		t.rollback();
	    } catch (IOException e) {
		throw new StopWriterVisitorException(getName(), e);
	    } // cancel opperations
	} finally {
	    try {
		t.close();
	    } catch (IOException e) {
		throw new StopWriterVisitorException(getName(), e);
	    } // free resources
	}

    }

    public String getName() {
	return "Shp Writer from Geotools";
    }

    public boolean canWriteGeometry(int gvSIGgeometryType) {
	switch (gvSIGgeometryType) {
	case FShape.POINT:
	    return true;
	case FShape.LINE:
	    return true;
	case FShape.POLYGON:
	    return true;
	case FShape.ARC:
	    return false;
	case FShape.ELLIPSE:
	    return false;
	case FShape.MULTIPOINT:
	    return true;
	case FShape.TEXT:
	    return false;
	}
	return false;
    }

    @Override
    public boolean canWriteAttribute(int sqlType) {
	switch (sqlType) {
	case Types.DOUBLE:
	case Types.FLOAT:
	case Types.INTEGER:
	case Types.BIGINT:
	    return true;
	case Types.DATE:
	    return true;
	case Types.BIT:
	case Types.BOOLEAN:
	    return true;
	case Types.VARCHAR:
	case Types.CHAR:
	case Types.LONGVARCHAR:
	    return true; // TODO: Revisar esto, porque no creo que admita campos
			 // muy grandes

	}

	return false;
    }

    public void setFlatness(double flatness) {
	// TODO Auto-generated method stub

    }

    @Override
    public void initialize(ITableDefinition tableDefinition)
	    throws InitializeWriterException {
	super.initialize(tableDefinition);

    }

    public boolean canAlterTable() {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean canSaveEdits() {
	// TODO Auto-generated method stub
	return true;
    }

}
