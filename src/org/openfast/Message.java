package org.openfast;

import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;

public class Message extends GroupValue {
	private final int templateId;
	final MessageTemplate template;
	
	public Message(MessageTemplate template, int templateId, FieldValue[] fieldValues) {
		super(template, fieldValues);
		this.template = template;
		this.templateId = templateId;
	}
	
	public Message(MessageTemplate template, int templateId) {
		this(template, templateId, initializeFieldValues(template.getFieldCount(), templateId));
		for (int i=1; i<template.getFieldCount(); i++) {
			if (template.getField(i) instanceof Scalar) {
				Scalar scalar = ((Scalar) template.getField(i));
				if (scalar.getOperatorName().equals(Operator.CONSTANT))
					setFieldValue(i, scalar.getDefaultValue());
			}
		}
	}

	private static FieldValue[] initializeFieldValues(int fieldCount, int templateId) {
		FieldValue[] fields = new FieldValue[fieldCount];
		fields[0] = new IntegerValue(templateId);
		return fields;
	}

	public int getTemplateId()
	{
		return templateId;
	}
	
	public String toString() {
		return "Message [TID: " + String.valueOf(templateId) + "]";
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Message)) return false;
		return equals((Message) obj);
	}
	
	public boolean equals(Message message)
	{
		if (this.getTemplateId() != message.getTemplateId() ||
				this.getFieldCount() != message.getFieldCount()) return false;
		for (int i=1; i<message.getFieldCount(); i++)
			if (message.getValue(i) == null)
				if (this.getValue(i) == null)
					continue;
				else
					return false;
			else if (!message.getValue(i).equals(this.getValue(i)))
				return false;
		return true;
	}

	public int getFieldCount() {
		return values.length;
	}

	public MessageTemplate getTemplate() {
		return template;
	}
}
