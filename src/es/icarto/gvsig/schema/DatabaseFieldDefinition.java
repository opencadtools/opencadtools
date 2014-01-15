package es.icarto.gvsig.schema;


public class DatabaseFieldDefinition extends FieldDefinition {
    private boolean isPrimaryKey;

    public boolean isPrimaryKey() {
	return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
	this.isPrimaryKey = isPrimaryKey;
    }
}
