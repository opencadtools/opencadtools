package es.icarto.gvsig.schema;

import java.io.File;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class GenericSchemaSerializator<T extends FieldDefinition> {
    private Class<T> clazz;

    public GenericSchemaSerializator(Class<T> clazz) {
	this.clazz = clazz;
    }

    public String toXML(List<T> fields) {
	XStream xstream = new XStream();
	xstream.alias("gvsig-layer-schema", List.class);
	xstream.alias("field", clazz);
	return xstream.toXML(fields);
    }

    public List<T> fromXML(File xml) throws XStreamException {
	XStream xstream = new XStream();
	xstream.alias("gvsig-layer-schema", List.class);
	xstream.alias("field", clazz);
	return (List<T>) xstream.fromXML(xml);
    }
}
