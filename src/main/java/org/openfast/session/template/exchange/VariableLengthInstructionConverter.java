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
package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.error.FastConstants;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.type.Type;

public class VariableLengthInstructionConverter extends ScalarConverter {
    public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
        Scalar scalar = (Scalar) super.convert(fieldDef, templateRegistry, context);
        if (fieldDef.isDefined("Length")) {
            scalar.addAttribute(FastConstants.LENGTH_FIELD, fieldDef.getGroup("Length").getString("Name"));
        }
        return scalar;
    }

    public GroupValue convert(Field field, ConversionContext context) {
        Scalar scalar = (Scalar) field;
        GroupValue fieldDef = super.convert(field, context);
        if (scalar.hasAttribute(FastConstants.LENGTH_FIELD)) {
            GroupValue lengthDef = new GroupValue(fieldDef.getGroup().getGroup("Length"));
            lengthDef.setString("Name", scalar.getAttribute(FastConstants.LENGTH_FIELD));
            fieldDef.setFieldValue("Length", lengthDef);
        }
        return fieldDef;
    }

    public boolean shouldConvert(Field field) {
        if (!field.getClass().equals(Scalar.class))
            return false;
        Type type = ((Scalar) field).getType();
        return type.equals(Type.BYTE_VECTOR) || type.equals(Type.UNICODE);
    }

    public Group[] getTemplateExchangeTemplates() {
        return new Group[] { SessionControlProtocol_1_1.BYTE_VECTOR_INSTR, SessionControlProtocol_1_1.UNICODE_INSTR };
    }
}
