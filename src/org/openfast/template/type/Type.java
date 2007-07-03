package org.openfast.template.type;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.LongValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.util.Util;

public abstract class Type {
	private final static Map TYPE_NAME_MAP = new LinkedHashMap();
	private final String name;
	
	public Type(String typeName) {
		this.name = typeName;
		TYPE_NAME_MAP.put(typeName, this);
	}
	
	/**
	 * Return the type that is being searched for
	 * @param typeName The type name that being searched for
	 * @return Return a Type object of the type that is being searched for
	 */
	public static Type getType(String typeName) {
		if (!TYPE_NAME_MAP.containsKey(typeName))
			throw new IllegalArgumentException("The type named " + typeName + " does not exist.  Existing types are " + Util.collectionToString(TYPE_NAME_MAP.keySet()));
		return (Type) TYPE_NAME_MAP.get(typeName);
	}
	
	/**
	 * 
	 * @return Returns name as a string
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Returns the name as a string
	 */
	public String toString() {
		return name;
	}
	
	public abstract TypeCodec getCodec(Operator operator, boolean optional);
	public abstract ScalarValue getValue(String value);
	public abstract ScalarValue getDefaultValue();
	public abstract boolean isValueOf(ScalarValue previousValue);
	public void validateValue(ScalarValue value) {}
	
	public final static Type U8 = new UnsignedIntegerType(8); 
	public final static Type U16 = new UnsignedIntegerType(16); 
	public final static Type U32 = new UnsignedIntegerType(32) {
		public void validateValue(ScalarValue value) {
			if (value instanceof LongValue) {
				if (value.toLong() > Integer.MAX_VALUE || value.toLong() < Integer.MIN_VALUE)
					Global.handleError(FastConstants.D2_INT_OUT_OF_RANGE, "The value " + value + " is out of range for the type " + this);
			}
		}
	}; 
	public final static Type U64 = new UnsignedIntegerType(64);
	public final static Type I8 = new SignedIntegerType(8); 
	public final static Type I16 = new SignedIntegerType(16); 
	public final static Type I32 = new SignedIntegerType(32) {
		public void validateValue(ScalarValue value) {
			if (value instanceof LongValue) {
				if (value.toLong() > Integer.MAX_VALUE || value.toLong() < Integer.MIN_VALUE)
					Global.handleError(FastConstants.D2_INT_OUT_OF_RANGE, "The value " + value + " is out of range for the type " + this);
			}
		}
	};  
	public final static Type I64 = new SignedIntegerType(64);  
	public final static Type STRING = new StringType("string", TypeCodec.ASCII, TypeCodec.NULLABLE_ASCII);
	public final static Type ASCII = new StringType("ascii", TypeCodec.ASCII, TypeCodec.NULLABLE_ASCII);
	public final static Type UNICODE = new StringType("unicode", TypeCodec.UNICODE, TypeCodec.NULLABLE_UNICODE);
	public final static Type BYTE_VECTOR = new ByteVectorType();
	public final static Type DECIMAL = new DecimalType();
		
	public static final Type[] ALL_TYPES = new Type[] { U8, U16, U32, U64, I8, I16, I32, I64, STRING, ASCII, UNICODE, BYTE_VECTOR, DECIMAL };
	public static final Type[] INTEGER_TYPES = new Type[] { U8, U16, U32, U64, I8, I16, I32, I64 };

}
