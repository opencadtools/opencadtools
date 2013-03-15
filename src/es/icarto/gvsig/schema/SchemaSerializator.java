package es.icarto.gvsig.schema;

import java.io.File;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.thoughtworks.xstream.XStream;

public class SchemaSerializator {

    public String toXML(FieldDescription[] fields) {
	XStream xstream = new XStream();
	xstream.alias("gvsig-layer-schema", FieldDescription[].class);
	xstream.alias("field", FieldDescription.class);
	xstream.aliasField("name", FieldDescription.class, "fieldName");
	xstream.aliasField("type", FieldDescription.class, "fieldType");
	xstream.aliasField("length", FieldDescription.class, "fieldLength");

	xstream.omitField(FieldDescription.class, "fieldAlias");
	xstream.omitField(FieldDescription.class, "fieldDecimalCount");
	xstream.omitField(FieldDescription.class, "defaultValue");
	return xstream.toXML(fields);
    }

    public FieldDescription[] fromXML(File xml) {
	XStream xstream = new XStream();
	xstream.alias("gvsig-layer-schema", FieldDescription[].class);
	xstream.alias("field", FieldDescription.class);
	xstream.aliasField("name", FieldDescription.class, "fieldName");
	xstream.aliasField("type", FieldDescription.class, "fieldType");
	xstream.aliasField("length", FieldDescription.class, "fieldLength");

	xstream.omitField(FieldDescription.class, "fieldAlias");
	xstream.omitField(FieldDescription.class, "fieldDecimalCount");
	xstream.omitField(FieldDescription.class, "defaultValue");
	return (FieldDescription[]) xstream.fromXML(xml);
    }

}
