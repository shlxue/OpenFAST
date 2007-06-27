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

import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.ScalarValue;

import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

import java.io.InputStream;


public class Scalar extends Field {
    private final Type type;
    private final Operator operator;
    private final String typeName;
    private final String operatorName;
    private String dictionary;
    private ScalarValue defaultValue = ScalarValue.UNDEFINED;
    private final ScalarValue initialValue;

    public Scalar(String name, String typeName, Operator operator, ScalarValue defaultValue, boolean optional) {
        super(name, optional);
        this.operator = operator;
        this.operatorName = operator.getName();
        this.dictionary = "global";
        this.defaultValue = (defaultValue == null) ? ScalarValue.UNDEFINED : defaultValue;
        this.typeName = typeName;
        this.type = Type.getType(typeName, optional, operator);
        this.initialValue = ((defaultValue == null) || defaultValue.isUndefined()) 
        								? this.type.getDefaultValue()
                                        : defaultValue;
        validate();
    }

    private void validate() {
    	// TODO - move this validation into the operator class.
    	if (operatorName.equals(Operator.CONSTANT) && defaultValue.isUndefined())
    		FastConstants.handleError(FastConstants.NO_INITIAL_VALUE_FOR_CONST, "The field \"" + name + "\" must have a default value defined.");
    	if (operatorName.equals(Operator.DEFAULT) && !optional && defaultValue.isUndefined())
    		FastConstants.handleError(FastConstants.NO_INITVAL_MNDTRY_DFALT, "The field \"" + name + "\" must have a default value defined.");
	}

	public Scalar(String name, String typeName, String operator, ScalarValue defaultValue, boolean optional) {
        this(name, typeName, Operator.getOperator(operator, typeName), defaultValue, optional);
    }

    public Scalar(String name, String typeName, String operator, String defaultValue, boolean optional) {
        this(name, typeName, operator, getValue(typeName, defaultValue), optional);
    }

	private static ScalarValue getValue(String typeName, String defaultValue) {
		if (defaultValue == null) return ScalarValue.UNDEFINED;
		return ScalarValue.getValue(typeName, defaultValue);
	}

    public String getType() {
        return typeName;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public byte[] encode(FieldValue value, Group template, Context context) {
        try {
            ScalarValue priorValue = (ScalarValue) context.lookup(getDictionary(),
                    template, getKey());
            ScalarValue valueToEncode = operator.getValueToEncode((ScalarValue) value,
                    priorValue, this);

            // TODO - move this operator specific code out
            if (!((operatorName == Operator.DELTA) && (value == null))) {
                context.store(getDictionary(), template, getKey(),
                    (ScalarValue) value);
            }

            if (valueToEncode == null) {
                return new byte[0];
            }

            return type.encode(valueToEncode);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error occurred while encoding scalar \"" + getName() + "\": " +
                e.getMessage(), e);
        }
    }

    public String getDictionary() {
        return dictionary;
    }

    public ScalarValue decodeValue(ScalarValue newValue,
        ScalarValue previousValue) {
        return operator.decodeValue(newValue, previousValue, this);
    }

    public ScalarValue getDefaultValue() {
        return defaultValue;
    }

    public ScalarValue decode(InputStream in, ScalarValue previousValue) {
        // TODO - Refactor out this if condition
        if (operatorName == Operator.CONSTANT) {
            return operator.decodeValue(null, null, this);
        }

        return decodeValue(type.decode(in), previousValue);
    }

    public ScalarValue decode(ScalarValue previousValue) {
        return operator.decodeEmptyValue(previousValue, this);
    }

    public boolean usesPresenceMapBit() {
        return operator.usesPresenceMapBit(optional);
    }

    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return operator.isPresenceMapBitSet(encoding, fieldValue);
    }

    public FieldValue decode(InputStream in, Group template, Context context,
        boolean present) {
        ScalarValue previousValue = context.lookup(getDictionary(), template,
                getKey());
        ScalarValue value;

        if (present) {
            value = decode(in, previousValue);
        } else if ((getOperatorName() == Operator.CONSTANT) && isOptional()) {
            value = null;
        } else {
            value = decode(previousValue);
        }

        if (!((getOperatorName() == Operator.DELTA) && (value == null))) {
            context.store(getDictionary(), template, getKey(), value);
        }

        return value;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String toString() {
        return "Scalar [name=" + name + ", operator=" + operator +
        ", dictionary=" + dictionary + "]";
    }

    public Class getValueType() {
        return ScalarValue.class;
    }

    public FieldValue createValue(String value) {
        return type.parse(value);
    }

    public String getTypeName() {
        return "scalar";
    }

    public ScalarValue getInitialValue() {
        return initialValue;
    }
}
