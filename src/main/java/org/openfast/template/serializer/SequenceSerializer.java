package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.Sequence;
import org.openfast.util.XmlWriter;

public class SequenceSerializer extends AbstractFieldSerializer implements FieldSerializer {
    public boolean canSerialize(Field field) {
        return field instanceof Sequence;
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        Sequence sequence = (Sequence) field;
        writer.start("sequence");
        writeCommonAttributes(writer, field, context);
        writeTypeReference(writer, sequence.getGroup());
        writeLength(writer, sequence, context);
        writeChildren(writer, context, sequence.getGroup());
        writer.end();
    }
}
