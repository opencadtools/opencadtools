package es.icarto.gvsig.schema;

import java.io.File;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class SchemaSerializator {

    public String toXML(List<FieldDefinition> fields) {
	XStream xstream = new XStream();
	xstream.alias("gvsig-layer-schema", List.class);
	xstream.alias("field", FieldDefinition.class);
	return xstream.toXML(fields);
    }

    public List<FieldDefinition> fromXML(File xml) {
	XStream xstream = new XStream();
	xstream.alias("gvsig-layer-schema", List.class);
	xstream.alias("field", FieldDefinition.class);
	return (List<FieldDefinition>) xstream.fromXML(xml);
    }

}
