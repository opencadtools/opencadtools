/**
 * 
 */
package com.iver.cit.gvsig.writers;

import java.io.IOException;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;

import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;

/**
 * @author fjp
 * 
 * Example of using a Geotools featureStore to write features
 * Example of use: Inside the extension, open a dataStore and
 * get the featureStore. Then create this class with it.
 *
 */
public class WriterGT2 implements IWriter {

	FilterFactory filterFactory = FilterFactory.createFilterFactory();
	FeatureStore featStore;
	AttributeType[] types;
	Transaction t;
	int numReg = 0;
	
	public WriterGT2(FeatureStore featureStore) throws IOException
	{
		this.featStore = featureStore;
	}
	
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#preProcess()
	 */
	public void preProcess() throws EditionException {
		try {
			types = featStore.getSchema().getAttributeTypes();
			t = new DefaultTransaction("handle");
			featStore.setTransaction(t);
    	  
			t.addAuthorization("handle");  // provide authoriztion
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EditionException(e);
		}


	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#process(com.iver.cit.gvsig.fmap.edition.IRowEdited)
	 */
	public void process(IRowEdited row) throws EditionException {
		
		IFeature feat = (IFeature) row.getLinkedRow();
		Object[] values = new Object[types.length];
		values[0] = feat.getGeometry().toJTSGeometry();
		for (int i=1; i < types.length; i++)
			values[i] = feat.getAttribute(i-1);

		Filter theFilter = filterFactory.createFidFilter(feat.getID()); 
        try {
        	System.out.println("Escribiendo numReg=" + numReg + " con STATUS=" + row.getStatus());
        	switch (row.getStatus())
        	{
        		case IRowEdited.STATUS_ADDED:        			
        			Feature featGT2 = featStore.getSchema().create(values);
        			FeatureReader reader = DataUtilities.reader(
        					new Feature[] {featGT2});
        			featStore.addFeatures(reader);
        			break;
        		case IRowEdited.STATUS_MODIFIED:
        			featStore.modifyFeatures(types, values, theFilter);	
        			break;
        		case IRowEdited.STATUS_ORIGINAL:
        			
        			break;
        		case IRowEdited.STATUS_DELETED:
            		featStore.removeFeatures(theFilter);        			
        			break;
        	}
        		

			numReg++;
		} catch (IOException e) {
			e.printStackTrace();
			throw new EditionException(e);
		} catch (IllegalAttributeException e) {
			e.printStackTrace();
			throw new EditionException(e);
		}
				
		
			

	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#postProcess()
	 */
	public void postProcess() throws EditionException {
		try
		{
			t.commit(); // commit opperations
		}
		catch (IOException io){
			try {
				t.rollback();
			} catch (IOException e) {
				e.printStackTrace();
				throw new EditionException(e);
			} // cancel opperations
		}
		finally {
			try {
				t.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new EditionException(e);
			} // free resources
		}

	}

}
