/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.template;

import org.openfast.BitVector;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;

import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

import java.io.InputStream;


public class MessageTemplate extends Group implements FieldSet {
    private String messageReference;

    public MessageTemplate(String name, Field[] fields) {
        super(name, addTemplateIdField(fields), false);
    }

    private static Field[] addTemplateIdField(Field[] fields) {
        Field[] newFields = new Field[fields.length + 1];
        newFields[0] = new Scalar("templateId", Type.UNSIGNED_INTEGER,
                Operator.COPY, false);
        System.arraycopy(fields, 0, newFields, 1, fields.length);

        return newFields;
    }

    public void setMessageReference(String messageReference) {
        this.messageReference = messageReference;
    }

    public Field getField(int index) {
        return fields[index];
    }

    public int getFieldCount() {
        return fields.length;
    }

    public String getMessageReference() {
        return messageReference;
    }

    public byte[] encode(GroupValue message, Context context) {
        return super.encode(message, this, context);
    }

    public Message decode(InputStream in, int templateId, BitVector pmap,
        Context context) {
        FieldValue[] fieldValues = super.decodeFieldValues(in, this, pmap,
                context, 1);
        System.out.println();
        fieldValues[0] = new IntegerValue(templateId);

        return new Message(this, templateId, fieldValues);
    }

    public Class getValueType() {
        return Message.class;
    }

    public String toString() {
        return "MessageTemplate [" + name + "]";
    }

    public FieldValue createValue(String value) {
        return new Message(this, Integer.parseInt(value));
    }

    public Field[] getFields() {
        return fields;
    }
}
