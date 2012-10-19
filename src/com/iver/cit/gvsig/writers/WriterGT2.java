/**
 *
 */
package com.iver.cit.gvsig.writers;

import java.io.IOException;
import java.sql.Types;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypes;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.NullValue;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * @author fjp
 * 
 *         Example of using a Geotools featureStore to write features Example of
 *         use: Inside the extension, open a dataStore and get the featureStore.
 *         Then create this class with it.
 * 
 */
public class WriterGT2 extends AbstractWriter {

    FilterFactory filterFactory = FilterFactory.createFilterFactory();
    FeatureStore featStore;
    AttributeType[] types;
    Transaction t;
    int numReg = 0;

    public static Class getClassBySqlTYPE(int type) {
	switch (type) {
	case Types.SMALLINT:
	    return Integer.class;
	case Types.INTEGER:
	    return Integer.class;
	case Types.BIGINT:
	    return Integer.class;
	case Types.BOOLEAN:
	    return Boolean.class;
	case Types.DECIMAL:
	    return Double.class;
	case Types.DOUBLE:
	    return Double.class;
	case Types.FLOAT:
	    return Double.class;
	case Types.CHAR:
	    return Character.class;
	case Types.VARCHAR:
	    return String.class;
	case Types.LONGVARCHAR:
	    return String.class;
	}
	return NullValue.class;
    }

    public static FeatureType getFeatureType(FLyrVect layer, String geomField,
	    String featName) throws SchemaException, ReadDriverException {

	Class geomType = findBestGeometryClass(layer.getShapeType());
	// geomType = Geometry.class;
	AttributeType geom = AttributeTypeFactory.newAttributeType(geomField,
		geomType);
	int numFields = layer.getRecordset().getFieldCount() + 1;
	AttributeType[] att = new AttributeType[numFields];
	att[0] = geom;
	for (int i = 1; i < numFields; i++) {
	    att[i] = AttributeTypeFactory.newAttributeType(layer.getRecordset()
		    .getFieldName(i - 1), getClassBySqlTYPE(layer
		    .getRecordset().getFieldType(i - 1)));
	}
	FeatureType featType = FeatureTypes.newFeatureType(att, featName);
	return featType;
    }

    public static final Class findBestGeometryClass(int layerType) {
	Class best = Geometry.class;
	switch (layerType) {
	case FShape.LINE:
	    best = MultiLineString.class;
	    break;
	case FShape.MULTIPOINT:
	    best = MultiPoint.class;
	    break;
	case FShape.POINT:
	    best = Point.class;
	    break;
	case FShape.POLYGON:
	    best = MultiPolygon.class;
	    break;
	case FShape.MULTI:
	    best = Geometry.class;
	    break;
	default:
	    throw new RuntimeException(
		    "Unknown gvSigShapeType->GeometryClass : " + layerType);
	}
	return best;
    }

    public WriterGT2(FeatureStore featureStore, boolean writeAllFeatures) {
	this.featStore = featureStore;
	this.bWriteAll = writeAllFeatures;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.fmap.edition.IWriter#preProcess()
     */
    public void preProcess() throws StartWriterVisitorException {
	try {
	    types = featStore.getSchema().getAttributeTypes();
	    t = new DefaultTransaction("handle");
	    featStore.setTransaction(t);

	    t.addAuthorization("handle"); // provide authoriztion

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
	// FeatureType featType = featStore.getSchema();
	// TODO: OJO CON EL ORDEN DE LOS CAMPOS, QUE NO ES EL MISMO
	Object[] values = new Object[types.length];
	values[0] = feat.getGeometry().toJTSGeometry();
	for (int i = 1; i < types.length; i++) {
	    values[i] = feat.getAttribute(i - 1);
	}

	Filter theFilter = filterFactory.createFidFilter(feat.getID());
	try {
	    // System.out.println("Escribiendo numReg=" + numReg +
	    // " con STATUS=" + row.getStatus());
	    switch (row.getStatus()) {
	    case IRowEdited.STATUS_ADDED:
		Feature featGT2 = featStore.getSchema().create(values);
		FeatureReader reader = DataUtilities
			.reader(new Feature[] { featGT2 });
		featStore.addFeatures(reader);
		break;
	    case IRowEdited.STATUS_MODIFIED:
		featStore.modifyFeatures(types, values, theFilter);
		break;
	    case IRowEdited.STATUS_ORIGINAL:
		if (bWriteAll) {
		    featGT2 = featStore.getSchema().create(values);
		    reader = DataUtilities.reader(new Feature[] { featGT2 });
		    featStore.addFeatures(reader);
		}
		break;
	    case IRowEdited.STATUS_DELETED:
		featStore.removeFeatures(theFilter);
		break;
	    }

	    numReg++;
	} catch (IOException e) {
	    throw new ProcessWriterVisitorException(getName(), e);
	} catch (IllegalAttributeException e) {
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
	return "JDBC Writer from Geotools";
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
