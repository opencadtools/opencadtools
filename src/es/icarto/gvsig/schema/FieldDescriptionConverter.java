package es.icarto.gvsig.schema;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FieldDescriptionConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
	return clazz.equals(FieldDescription[].class);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
	    MarshallingContext context) {
	FieldDescription[] fields = (FieldDescription[]) value;
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
	    UnmarshallingContext context) {
	return null;
    }

}
