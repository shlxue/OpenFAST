package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.StringType;
import org.openfast.template.type.Type;
import org.openfast.util.XmlWriter;

public class ScalarSerializer implements FieldSerializer {

    public boolean canSerialize(Field field) {
        return field instanceof Scalar;
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        Scalar scalar = (Scalar) field;
        String nodeName = getNodeName(scalar);
        writer.start(nodeName);
        AbstractFieldSerializer.writeCommonAttributes(writer, field, context);
        if (scalar.getType().equals(Type.UNICODE))
            writer.addAttribute("charset", "unicode");
        if (!Operator.NONE.equals(scalar.getOperator())) {
            AbstractFieldSerializer.writeOperator(writer, scalar, context);
        }
        writer.end();
    }

    private String getNodeName(Scalar scalar) {
        String nodeName = scalar.getType().getName();
        if (scalar.getType() instanceof StringType)
            nodeName = "string";
        return nodeName;
    }
}
