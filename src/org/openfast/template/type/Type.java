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


package org.openfast.template.type;

import org.openfast.ScalarValue;

import org.openfast.template.operator.Operator;
import org.openfast.template.operator.TwinOperator;

import org.openfast.util.Key;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;


public abstract class Type {
    static final int NULL_SCALED_NUMBER = -64;
    protected static final byte[] NULL_SF_DECIMAL_VALUE_ENCODING = new byte[] {
            (byte) 0xc0
        };
    protected static final byte[] NULL_TF_DECIMAL_VALUE_ENCODING = new byte[] {
            (byte) 0xc0, (byte) 0x80
        };
    protected static final byte STOP_BIT = (byte) 0x80;
    static final byte[] NULL_VALUE_ENCODING = new byte[] { STOP_BIT };
    private static final Map TYPE_NAME_MAP = new HashMap();

    // Type Enumerations
    public static final Integer UNSIGNED_INTEGER = new Integer(0);
    public static final Integer SIGNED_INTEGER = new Integer(1);
    public static final Integer DECIMAL = new Integer(2);
    public static final Integer STRING = new Integer(3);
    public static final Integer BYTE_VECTOR = new Integer(4);

    // Type Collections
    public static final Integer[] ALL_TYPES = new Integer[] {
            UNSIGNED_INTEGER, SIGNED_INTEGER, DECIMAL, STRING, BYTE_VECTOR
        };
    public static final Integer[] INTEGER_TYPES = new Integer[] {
            Type.UNSIGNED_INTEGER, Type.SIGNED_INTEGER
        };

    // Type Definitions
    public static final Type UINT = new UnsignedInteger(UNSIGNED_INTEGER,
            "unsigned integer", new String[] { "u8", "u16", "u32", "u64" });
    public static final Type NULLABLE_UNSIGNED_INTEGER = new NullableUnsignedInteger(UNSIGNED_INTEGER,
            "nullable unsigned integer", new String[] {  }, true);
    public static final Type INTEGER = new SignedInteger(SIGNED_INTEGER,
            "signed integer", new String[] { "i8", "i16", "i32", "i64" });
    public static final Type NULLABLE_INTEGER = new NullableSignedInteger(SIGNED_INTEGER,
            "signed null integer", new String[] {  }, true);
    public static final Type STRING_TYPE = new StringType(STRING, "string",
            new String[] { "string" });
    public static final Type NULLABLE_STRING_TYPE = new NullableStringType(STRING,
            "nullable string", new String[] { "" }, true);
    public static final Type BIT_VECTOR = new BitVectorType(null, "bit vector",
            new String[] { "bitvector", "bit" });
    public static final Type BYTE_VECTOR_TYPE = new ByteVectorType(BYTE_VECTOR,
            "byte vector", new String[] { "bytevector", "byte" });
    public static final Type SF_SCALED_NUMBER = new SingleFieldDecimal();
    public static final Type NULLABLE_SF_SCALED_NUMBER = new NullableSingleFieldDecimal();
    public static final Type TF_SCALED_NUMBER = new TwinFieldDecimal(DECIMAL,
            "twin field scaled number", new String[] {  });
    public static final Type STRING_DELTA = new StringDelta(STRING,
            "Delta String", new String[] {  });
    public static final Type NULLABLE_STRING_DELTA = new NullableStringDelta(STRING,
            "Nullable Delta String", new String[] {  });
    private static final Map TYPE_MAP = new HashMap();

    static {
        TYPE_MAP.put(new Key(SIGNED_INTEGER, Boolean.TRUE), NULLABLE_INTEGER);
        TYPE_MAP.put(new Key(SIGNED_INTEGER, Boolean.FALSE), INTEGER);
        TYPE_MAP.put(new Key(UNSIGNED_INTEGER, Boolean.TRUE),
            NULLABLE_UNSIGNED_INTEGER);
        TYPE_MAP.put(new Key(UNSIGNED_INTEGER, Boolean.FALSE), UINT);
        TYPE_MAP.put(new Key(DECIMAL, Boolean.TRUE), NULLABLE_SF_SCALED_NUMBER);
        TYPE_MAP.put(new Key(DECIMAL, Boolean.FALSE), SF_SCALED_NUMBER);
        TYPE_MAP.put(new Key(STRING, Boolean.TRUE), NULLABLE_STRING_TYPE);
        TYPE_MAP.put(new Key(STRING, Boolean.FALSE), STRING_TYPE);
        TYPE_MAP.put(new Key(BYTE_VECTOR, Boolean.TRUE), BYTE_VECTOR_TYPE);
        TYPE_MAP.put(new Key(BYTE_VECTOR, Boolean.FALSE), BYTE_VECTOR_TYPE);
    }

    private final String typeName;
    private final boolean nullable;

    public Type(Integer type, String typeName, String[] typeNames) {
        this(type, typeName, typeNames, false);
    }

    public Type(Integer type, String typeName, String[] typeNames,
        boolean nullable) {
        this.typeName = typeName;
        this.nullable = nullable;

        for (int i = 0; i < typeNames.length; i++)
            TYPE_NAME_MAP.put(typeNames[i], type);
    }

    public abstract byte[] encodeValue(ScalarValue value);

    public byte[] encode(ScalarValue value) {
        byte[] encoding = encodeValue(value);
        encoding[encoding.length - 1] |= 0x80; // add stop bit;

        return encoding;
    }

    public abstract ScalarValue parse(String value);

    public abstract ScalarValue decode(InputStream in);

    public String toString() {
        return "Type [" + typeName + "]";
    }

    public boolean isNullable() {
        return nullable;
    }

    public static Type getType(Integer type, boolean optional, Operator operator) {
        Key key = new Key(type, Boolean.valueOf(optional));

        if (operator instanceof TwinOperator) {
            if (type != DECIMAL) {
                throw new IllegalArgumentException(
                    "Twin field operators can only be used for decimal values.");
            }

            return TF_SCALED_NUMBER;
        } else if (operator == Operator.DELTA_INTEGER) {
            return (optional) ? NULLABLE_INTEGER : INTEGER;
        } else if (operator == Operator.DELTA_STRING) {
            return (optional) ? NULLABLE_STRING_DELTA : STRING_DELTA;
        }

        if (!TYPE_MAP.containsKey(key)) {
            throw new IllegalArgumentException(
                "The type specified does not exist.");
        }

        return (Type) TYPE_MAP.get(key);
    }

    public static Integer getTypeEnum(String typeName) {
        if (!TYPE_NAME_MAP.containsKey(typeName)) {
            throw new IllegalArgumentException("The type \"" + typeName +
                "\" is not registered.");
        }

        return (Integer) TYPE_NAME_MAP.get(typeName);
    }

    public abstract ScalarValue getDefaultValue();
}
