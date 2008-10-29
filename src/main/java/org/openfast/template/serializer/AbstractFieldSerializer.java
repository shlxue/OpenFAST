package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.util.XmlWriter;

public abstract class AbstractFieldSerializer implements FieldSerializer {
    protected static void writeCommonAttributes(XmlWriter writer, Field field, SerializingContext context) {
        writer.addAttribute("name", field.getQName().getName());
        if (!context.getNamespace().equals(field.getQName().getNamespace()))
        writer.addAttribute("ns", field.getQName().getNamespace());
        if (field.getId() != null)
            writer.addAttribute("id", field.getId());
        if (field.isOptional())
            writer.addAttribute("presence", "optional");
    }

    protected static void writeOperator(XmlWriter writer, Scalar scalar, SerializingContext context) {
        writer.start(scalar.getOperator().getName());
        if (!scalar.getDictionary().equals(context.getDictionary())) {
            writer.addAttribute("dictionary", scalar.getDictionary());
        }
        if (!scalar.getKey().equals(scalar.getQName())) {
            writer.addAttribute("key", scalar.getKey().getName());
            if (!context.getNamespace().equals(scalar.getKey().getNamespace()))
                writer.addAttribute("ns", scalar.getKey().getNamespace());
        }
        if (!scalar.getDefaultValue().isUndefined()) {
            writer.addAttribute("value", scalar.getDefaultValue().toString());
        }
        writer.end();
    }

    protected static void writeChildren(XmlWriter writer, SerializingContext context, Group group) {
        for (int i=0; i<group.getFieldCount(); i++) {
            context.serialize(writer, group.getField(i));
        }
    }

    protected static void writeTypeReference(XmlWriter writer, Group group) {
        if (group.getTypeReference() != null) {
            writer.start("typeRef");
            writer.addAttribute("name", group.getTypeReference().getName());
            if (!"".equals(group.getTypeReference().getNamespace()))
                writer.addAttribute("ns", group.getTypeReference().getNamespace());
            writer.end();
        }
    }
    
    protected static void writeLength(XmlWriter writer, Field field, SerializingContext context) {
//        if (field.hasChild("length")) {
//            
//        }
    }
}
