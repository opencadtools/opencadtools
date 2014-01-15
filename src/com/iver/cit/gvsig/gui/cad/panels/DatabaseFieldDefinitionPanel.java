package com.iver.cit.gvsig.gui.cad.panels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import jwizardcomponent.JWizardComponents;

import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.thoughtworks.xstream.XStreamException;

import es.icarto.gvsig.schema.DatabaseFieldDefinition;
import es.icarto.gvsig.schema.DatabaseSchemaSerializator;

public class DatabaseFieldDefinitionPanel extends JPanelFieldDefinition {
    protected static final int FIELD_PK = FIELD_LENGTH + 1;

    private DefaultTableModel delegate;
    private DatabaseFieldTableModel dbModel;

    public DatabaseFieldDefinitionPanel(JWizardComponents wizardComponents,
	    IWriter writer) {
	super(wizardComponents, writer);
    }

    @Override
    protected void addField() {
	model.addRow(new Object[] { getNewFieldName(), "String", "20", false });
    }

    @Override
    protected DefaultTableModel createModel() {
	this.delegate = super.createModel();
	this.dbModel = new DatabaseFieldTableModel();
	this.dbModel.addTableModelListener(new TableModelListener() {
	    @Override
	    public void tableChanged(TableModelEvent e) {
		if (e.getColumn() != dbModel.getColumnCount() - 1
			|| e.getFirstRow() == e.getLastRow()) {
		    return;
		}

		int col = e.getColumn();
		int row = e.getFirstRow();
		Object value = dbModel.getValueAt(row, col);
		boolean isPrimaryKey = Boolean.parseBoolean(value.toString());
		if (isPrimaryKey) {
		    for (int i = 0; i < dbModel.getRowCount(); i++) {
			if (i == row) {
			    continue;
			}

			dbModel.setValueAt(false, i, col);
		    }
		}
	    }
	});
	return dbModel;
    }

    @Override
    protected Object[][] readSchema(File file) throws XStreamException {
	List<DatabaseFieldDefinition> fields = new DatabaseSchemaSerializator()
		.fromXML(file);
	Object[][] ret = new Object[fields.size()][];
	for (int i = 0; i < ret.length; i++) {
	    DatabaseFieldDefinition field = fields.get(i);
	    ret[i] = new Object[] { field.getName(), field.getType(),
		    field.getLength(), field.isPrimaryKey() };
	}

	return ret;
    }

    @Override
    protected String getSchemaXML() {
	List<DatabaseFieldDefinition> fields = new ArrayList<DatabaseFieldDefinition>();

	for (int i = 0; i < model.getRowCount(); i++) {
	    DatabaseFieldDefinition field = new DatabaseFieldDefinition();
	    field.setName(model.getValueAt(i, FIELD_NAME).toString());
	    field.setType(model.getValueAt(i, FIELD_TYPE).toString());
	    field.setLength(model.getValueAt(i, FIELD_LENGTH).toString());
	    field.setPrimaryKey(Boolean.parseBoolean(model.getValueAt(i,
		    FIELD_PK).toString()));
	    fields.add(field);
	}

	return new DatabaseSchemaSerializator().toXML(fields);
    }

    public String getPrimaryKey() {
	for (int i = 0; i < dbModel.getRowCount(); i++) {
	    if (i == dbModel.primaryKeyRowIndex) {
		return dbModel.getValueAt(i, FIELD_NAME).toString();
	    }
	}

	return null;
    }

    private class DatabaseFieldTableModel extends DefaultTableModel {
	private int primaryKeyRowIndex;

	public DatabaseFieldTableModel() {
	    this.primaryKeyRowIndex = -1;
	}

	@Override
	public int getRowCount() {
	    return delegate.getRowCount();
	}

	@Override
	public int getColumnCount() {
	    return delegate.getColumnCount() + 1;
	}

	@Override
	public String getColumnName(int column) {
	    if (column < delegate.getColumnCount()) {
		return delegate.getColumnName(column);
	    } else {
		return "Primary Key";
	    }
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
	    if (columnIndex < delegate.getColumnCount()) {
		return delegate.getValueAt(rowIndex, columnIndex);
	    } else {
		return rowIndex == primaryKeyRowIndex;
	    }
	}

	@Override
	public void addRow(Object[] rowData) {
	    delegate.addRow(rowData);
	    fireTableDataChanged();
	}

	@Override
	public void removeRow(int row) {
	    delegate.removeRow(row);

	    if (row == primaryKeyRowIndex) {
		primaryKeyRowIndex = -1;
	    } else if (row < primaryKeyRowIndex) {
		primaryKeyRowIndex--;
	    }

	    fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
	    if (columnIndex < delegate.getColumnCount()) {
		return delegate.getColumnClass(columnIndex);
	    } else {
		return Boolean.class;
	    }
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
	    if (column < delegate.getColumnCount()) {
		delegate.setValueAt(aValue, row, column);
	    } else {
		if (Boolean.parseBoolean(aValue.toString())) {
		    primaryKeyRowIndex = row;
		} else {
		    primaryKeyRowIndex = -1;
		}
	    }
	    fireTableDataChanged();
	}
    }
}
