package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.util.XmlWriter;

public class TemplateSerializer implements FieldSerializer {
    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        MessageTemplate template = (MessageTemplate) field;
        writer.start("template");
        writer.addAttribute("name", template.getName());
        if (!template.getQName().getNamespace().equals(context.getTemplateNamespace())) {
            writer.addAttribute("templateNs", template.getQName().getNamespace());
        }
        if (!template.getChildNamespace().equals(context.getNamespace()) &&
            !template.getChildNamespace().equals("")) {
            writer.addAttribute("ns", template.getChildNamespace());
        }
        if (template.getId() != null) {
            writer.addAttribute("id", template.getId());
        }
//        if (template.getDictionary() != null) {
//            writer.addAttribute("dictionary", template.getDictionary());
//        }
        if (template.getTypeReference() != null) {
            writer.start("typeRef");
            writer.addAttribute("name", template.getTypeReference().getName());
            if (!"".equals(template.getTypeReference().getNamespace()))
                writer.addAttribute("ns", template.getTypeReference().getNamespace());
            writer.end();
        }
        for (int i=1; i<template.getFieldCount(); i++) {
            context.serialize(writer, template.getField(i));
        }
        writer.end();
    }

    public boolean canSerialize(Field field) {
        return field instanceof MessageTemplate;
    }
}
