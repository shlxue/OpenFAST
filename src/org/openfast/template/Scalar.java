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
import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.ScalarValue;

import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.template.type.codec.TypeCodec;

import java.io.InputStream;


public class Scalar extends Field {
    private final TypeCodec typeCodec;
    private final Operator operator;
    private final Type type;
    private final String operatorName;
    private String dictionary;
    private ScalarValue defaultValue = ScalarValue.UNDEFINED;
    private final ScalarValue initialValue;

    /**
     * Scalar constructor - sets the dictionary as global and validates the entries 
     * @param name The name of Scalar as a string
     * @param typeName The name of the type as a string
     * @param operator The Operator object
     * @param defaultValue The default value of the ScalarValue
     * @param optional The optional boolean
     */
    public Scalar(String name, Type type, Operator operator, ScalarValue defaultValue, boolean optional) {
        super(name, optional);
        this.operator = operator;
        this.operatorName = operator.getName();
        this.dictionary = "global";
        this.defaultValue = (defaultValue == null) ? ScalarValue.UNDEFINED : defaultValue;
        this.type = type;
        this.typeCodec = type.getCodec(operator, optional);
        this.initialValue = ((defaultValue == null) || defaultValue.isUndefined()) 
        								? this.typeCodec.getDefaultValue()
                                        : defaultValue;
        validate();
    }

	public Scalar(String name, Type type, String operator, ScalarValue defaultValue, boolean optional) {
        this(name, type, Operator.getOperator(operator, type), defaultValue, optional);
    }

    /**
     * 
     * Checks to make sure there is a default value for the operators.
     *
     */
    private void validate() {
    	// TODO - move this validation into the operator class.
    	if (operatorName.equals(Operator.CONSTANT) && defaultValue.isUndefined())
    		FastConstants.handleError(FastConstants.S4_NO_INITIAL_VALUE_FOR_CONST, "The field \"" + name + "\" must have a default value defined.");
    	if (operatorName.equals(Operator.DEFAULT) && !optional && defaultValue.isUndefined())
    		FastConstants.handleError(FastConstants.S5_NO_INITVAL_MNDTRY_DFALT, "The field \"" + name + "\" must have a default value defined.");
	}

	/**
	 * 
	 * @return Returns the type as a string
	 */
    public Type getType() {
        return type;
    }

    /**
     * 
     * @return Returns the Operator object
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * 
     * @return Returns the operator name as a string
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param value The Field value
     * @param template The Group object
     * @param context The Context object
     * @param presenceMapBuilder The BitVector builder
     * @return 
     * @throw Throws RuntimeException if the encoding fails - will print to console the name of the scalar to fail
     */
    public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
        try {
            ScalarValue priorValue = (ScalarValue) context.lookup(getDictionary(),
                    template, getKey());
            ScalarValue valueToEncode = operator.getValueToEncode((ScalarValue) value, priorValue, this, presenceMapBuilder);

            // TODO - move this operator specific code out
            if (!((operatorName == Operator.DELTA) && (value == null))) {
                context.store(getDictionary(), template, getKey(),
                    (ScalarValue) value);
            }

            if (valueToEncode == null) {
                return new byte[0];
            }

            return typeCodec.encode(valueToEncode);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error occurred while encoding scalar \"" + getName() + "\": " +
                e.getMessage(), e);
        }
    }

    /**
     * 
     * @return Returns the dictionary as a string
     */
    public String getDictionary() {
        return dictionary;
    }

    /**
     * 
     * @param newValue
     * @param previousValue
     * @return
     */
    public ScalarValue decodeValue(ScalarValue newValue,
        ScalarValue previousValue) {
        return operator.decodeValue(newValue, previousValue, this);
    }

    /**
     * 
     * @return Returns the defaultValue of the current ScalarValue
     */
    public ScalarValue getDefaultValue() {
        return defaultValue;
    }

    /**
     * 
     * @param in
     * @param previousValue
     * @return 
     */
    public ScalarValue decode(InputStream in, ScalarValue previousValue) {
        // TODO - Refactor out this if condition
        if (operatorName == Operator.CONSTANT) {
            return operator.decodeValue(null, null, this);
        }

        return decodeValue(typeCodec.decode(in), previousValue);
    }

    /**
     * 
     * @param previousValue The previousValue of the ScalarValue
     * @return Depending on the operator, various ScalarValues could be returned
     */
    public ScalarValue decode(ScalarValue previousValue) {
        return operator.decodeEmptyValue(previousValue, this);
    }
    
    /**
     * Only returns the presenceMap index if there is a map present and the optional boolean is set to true
     * @return Returns the presenceMap index
     */
    public int encodePresenceMap(BitVector presenceMap, int presenceMapIndex, byte[] encoding, FieldValue fieldValue) {
    	return operator.encodePresenceMap(presenceMap, presenceMapIndex, encoding, fieldValue, optional);
    }
    
    /**
     * @return Returns true
     */
    public boolean usesPresenceMapBit() {
        return operator.usesPresenceMapBit(optional);
    }

    /**
     * @return Returns true if the byte array has a length 
     */
    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return operator.isPresenceMapBitSet(encoding, fieldValue);
    }

    /**
     * 
     * @param in The InputStream to be decoded
     * @param template The Group object
     * @param context The Context Object
     * @param present 
     * @return Returns the null if the Operator is constant and the optional boolean is true and the present boolean is true,
     * otherwise decodes the previousValue and returns the FieldValue object after decoding
     */
    public FieldValue decode(InputStream in, Group template, Context context,
        boolean present) {
        ScalarValue previousValue = context.lookup(getDictionary(), template, getKey());
        validateDictionaryTypeAgainstFieldType(previousValue, this.type);
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

    private void validateDictionaryTypeAgainstFieldType(ScalarValue previousValue, Type type) {
    	
	}

	/**
     * Sets the dictionary to the passed string
     * @param dictionary The string to be stored as the dictionary
     */
    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * @return Returns the string 'Scalar [name=XXX, operator=XXX, dictionary=XXX]'
     */
    public String toString() {
        return "Scalar [name=" + name + ", operator=" + operator +
        ", dictionary=" + dictionary + "]";
    }

    /**
     * @return Returns the class of the current ScalarValue
     */
    public Class getValueType() {
        return ScalarValue.class;
    }

    /**
     * @param value Creates a FieldValue of the passed value
     * @return Returns the FieldValue object with the passed value
     */
    public FieldValue createValue(String value) {
        return type.getValue(value);
    }

    /**
     * @return Returns the string 'scalar'
     */
    public String getTypeName() {
        return "scalar";
    }

    /**
     * 
     * @return Returns the initialValue of the current ScalarValue object
     */
    public ScalarValue getInitialValue() {
        return initialValue;
    }
    
    /**
     * 
     * @return Returns the type of the Codec
     */
    public TypeCodec getTypeCodec() {
    	return typeCodec;
    }
}
