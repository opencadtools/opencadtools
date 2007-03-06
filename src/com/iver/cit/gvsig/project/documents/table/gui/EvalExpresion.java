package com.iver.cit.gvsig.project.documents.table.gui;

import java.sql.Types;
import java.util.BitSet;
import java.util.Date;
import java.util.prefs.Preferences;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.StopEditionLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
/**
 * @author Vicente Caballero Navarro
 */
public class EvalExpresion {
	private FieldDescription[] fieldDescriptors;
	private int selectedIndex;
	private FieldDescription fieldDescriptor;
	private FLyrVect lv;
	private  IEditableSource ies =null;
	private Table table=null;
	private static Preferences prefs = Preferences.userRoot().node( "fieldExpresionOptions" );
	private int limit;
	public EvalExpresion() {
		limit=prefs.getInt("limit_rows_in_memory",-1);
	}
	public void setTable(Table table) {
		BitSet columnSelected = table.getSelectedFieldIndices();
        fieldDescriptors = table.getModel().getModelo().getFieldsDescription();
        selectedIndex = columnSelected.nextSetBit(0);
        fieldDescriptor = fieldDescriptors[selectedIndex];
        this.table=table;
        lv=(FLyrVect)table.getModel().getAssociatedTable();
        if (lv ==null)
            ies=table.getModel().getModelo();
        else
            ies = (VectorialEditableAdapter) lv.getSource();
	}
	 public void setValue(Object obj,int i) {
	    	//VectorialEditableAdapter vea = (VectorialEditableAdapter) lv.getSource();
	    	 Value value = getValue(obj);
	    	 try {
	    		 IRowEdited rowEdited=ies.getRow(i);
	    		 Value[] values = rowEdited.getAttributes();
	    		 values[selectedIndex] = value;
	    		 IRow newRow = null;
	    		 IGeometry geometry = ((DefaultFeature) rowEdited.getLinkedRow()).getGeometry();
	    		 newRow = new DefaultFeature(geometry, values,rowEdited.getID());
	    		 ies.modifyRow(rowEdited.getIndex(), newRow,"", EditionEvent.ALPHANUMERIC);

//	    	 IRow feat=null;
//			try {
//				feat = ies.getRow(i).getLinkedRow().cloneRow();
//			} catch (DriverIOException e1) {
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//	    	 Value[] values = feat.getAttributes();
//	    	 values[selectedIndex] = value;
//	    	 feat.setAttributes(values);
//
//	    	 IRowEdited edRow = new DefaultRowEdited(feat,
//	    			 IRowEdited.STATUS_MODIFIED, i);
//
//	    	 try {
//
//				ies.modifyRow(edRow.getIndex(), edRow.getLinkedRow(), "",
//						 EditionEvent.ALPHANUMERIC);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (DriverIOException e) {
//				e.printStackTrace();
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpansionFileReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidateRowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpansionFileWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	 /**
	     * Returns the value created from object.
	     *
	     * @param obj value.
	     *
	     * @return Value.
	     */
	    private Value getValue(Object obj) {
	        int typeField = fieldDescriptor.getFieldType();
	        Value value = ValueFactory.createNullValue();

	        if (obj instanceof Double || obj instanceof Float || obj instanceof Integer) {
	            if (typeField == Types.DOUBLE) {
	                double dv = ((Number) obj).doubleValue();
	                value = ValueFactory.createValue(dv);
	            } else if (typeField == Types.FLOAT) {
	                float df = ((Number) obj).floatValue();
	                value = ValueFactory.createValue(df);
	            } else if (typeField == Types.INTEGER) {
	                int di = ((Number) obj).intValue();
	                value = ValueFactory.createValue(di);
	            } else if (typeField == Types.VARCHAR) {
	                String s = ((Number) obj).toString();
	                value = ValueFactory.createValue(s);
	            }
	        } else if (obj instanceof Date) {
	            if (typeField == Types.DATE) {
	                Date date = (Date) obj;
	                value = ValueFactory.createValue(date);
	            } else if (typeField == Types.VARCHAR) {
	                String s = ((Date) obj).toString();
	                value = ValueFactory.createValue(s);
	            }
	        } else if (obj instanceof Boolean) {
	            if (typeField == Types.BOOLEAN) {
	                boolean b = ((Boolean) obj).booleanValue();
	                value = ValueFactory.createValue(b);
	            } else if (typeField == Types.VARCHAR) {
	                String s = ((Boolean) obj).toString();
	                value = ValueFactory.createValue(s);
	            }
	        } else if (obj instanceof String) {
	            if (typeField == Types.VARCHAR) {
	                String s = obj.toString();
	                value = ValueFactory.createValue(s);
	            }
	        }

	        return value;
	    }
	public FieldDescription getFieldDescriptorSelected() {
		return fieldDescriptor;
	}
	public FieldDescription[] getFieldDescriptors() {
		return fieldDescriptors;
	}
//	public void setFieldValue(Object obj,int i) {
//    	try {
//			((DBFDriver)table.getModel().getModelo().getOriginalDriver()).setFieldValue(i,selectedIndex,obj);
//		} catch (DriverLoadException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }
	public void saveEdits(int numRows) throws DriverLoadException, ReadDriverException, InitializeWriterException, StopWriterVisitorException {
		if (limit==-1 || numRows == 0 || (numRows % limit)!=0) {
			return;
		}
		ies.endComplexRow(PluginServices.getText(this, "expresion"));
		if ((lv != null) &&
                lv.getSource() instanceof VectorialEditableAdapter) {
                VectorialEditableAdapter vea = (VectorialEditableAdapter) lv.getSource();
                ISpatialWriter spatialWriter = (ISpatialWriter) vea.getDriver();
                vea.cleanSelectableDatasource();
         		lv.setRecordset(vea.getRecordset()); // Queremos que el recordset del layer
         		// refleje los cambios en los campos.
         		ILayerDefinition lyrDef = EditionUtilities.createLayerDefinition(lv);
         		spatialWriter.initialize(lyrDef);
         		vea.saveEdits(spatialWriter,EditionEvent.ALPHANUMERIC);
         		vea.getCommandRecord().clearAll();
         } else {
              if (ies instanceof IWriteable){
             	 IWriteable w = (IWriteable) ies;
	                 IWriter writer = w.getWriter();
	                 if (writer == null){
	                 }else{
	     				ITableDefinition tableDef = ies.getTableDefinition();
	    				writer.initialize(tableDef);

	    				ies.saveEdits(writer,EditionEvent.ALPHANUMERIC);
	                	ies.getSelection().clear();
	                 }
              }
              ies.getCommandRecord().clearAll();
         }
		ies.startComplexRow();
    	 table.refresh();
    }
}
