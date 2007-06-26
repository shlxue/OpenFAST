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


package org.openfast;

import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;


public class Message extends GroupValue {
    private final int templateId;
    final MessageTemplate template;

    public Message(MessageTemplate template, int templateId,
        FieldValue[] fieldValues) {
        super(template, fieldValues);
        this.template = template;
        this.templateId = templateId;
    }

    public Message(MessageTemplate template, int templateId) {
        this(template, templateId,
            initializeFieldValues(template.getFieldCount(), templateId));

        for (int i = 1; i < template.getFieldCount(); i++) {
            if (template.getField(i) instanceof Scalar) {
                Scalar scalar = ((Scalar) template.getField(i));

                if (scalar.getOperatorName().equals(Operator.CONSTANT)) {
                    setFieldValue(i, scalar.getDefaultValue());
                }
            }
        }
    }

    private static FieldValue[] initializeFieldValues(int fieldCount,
        int templateId) {
        FieldValue[] fields = new FieldValue[fieldCount];
        fields[0] = new IntegerValue(templateId);

        return fields;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String toString() {
        return "Message [TID: " + String.valueOf(templateId) + "]";
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof Message)) {
            return false;
        }

        return equals((Message) obj);
    }

    public boolean equals(Message message) {
        if ((this.getTemplateId() != message.getTemplateId()) ||
                (this.getFieldCount() != message.getFieldCount())) {
            return false;
        }

        for (int i = 1; i < message.getFieldCount(); i++)
            if (message.getValue(i) == null) {
                if (this.getValue(i) == null) {
                    continue;
                } else {
                    return false;
                }
            } else if (!message.getValue(i).equals(this.getValue(i))) {
                return false;
            }

        return true;
    }

    public int getFieldCount() {
        return values.length;
    }

    public MessageTemplate getTemplate() {
        return template;
    }
}
