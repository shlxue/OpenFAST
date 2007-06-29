package org.openfast.template.type;

import java.util.HashMap;
import java.util.Map;

import org.openfast.ByteVectorValue;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.LongValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.operator.TwinOperator;

public abstract class Type {
	private static abstract class SimpleType extends Type {
		private final TypeCodec codec;
		private final TypeCodec nullableCodec;
		
		public SimpleType (String typeName, TypeCodec codec, TypeCodec nullableCodec) {
			super(typeName);
			this.codec = codec;
			this.nullableCodec = nullableCodec;
		}
		
		public TypeCodec getCodec(Operator operator, boolean optional) {
			if (optional)
				return nullableCodec;
			return codec;
		}
		
		public ScalarValue getValue(String value) {
			if (value == null) return null;
			return getVal(value);
		}

		protected abstract ScalarValue getVal(String value);
	}
	
	private static class IntegerType extends SimpleType {
		public IntegerType(String typeName, TypeCodec codec, TypeCodec nullableCodec) {
			super(typeName, codec, nullableCodec);
		}
		
		public TypeCodec getCodec(Operator operator, boolean optional) {
			if (operator.equals(Operator.DELTA_INTEGER))
				if (optional)
					return TypeCodec.NULLABLE_INTEGER;
				else
					return TypeCodec.INTEGER;
			return super.getCodec(operator, optional);
		}
		
		protected ScalarValue getVal(String value) {
			try {
				return new IntegerValue(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}
	
	private static class StringType extends SimpleType {
		public StringType(String typeName, TypeCodec codec, TypeCodec nullableCodec) {
			super(typeName, codec, nullableCodec);
		}
		
		public ScalarValue getVal(String value) {
			return new StringValue(value);
		}
		
		public TypeCodec getCodec(Operator operator, boolean optional) {
			if (operator.equals(Operator.DELTA_STRING))
				return (optional) ? TypeCodec.NULLABLE_STRING_DELTA : TypeCodec.STRING_DELTA;
			return super.getCodec(operator, optional);
		}
	}
	
	private final static Map TYPE_NAME_MAP = new HashMap();
	private final String typeName;
	
	public Type(String typeName) {
		this.typeName = typeName;
		TYPE_NAME_MAP.put(typeName, this);
	}
	
	public static Type getType(String typeName) {
		return (Type) TYPE_NAME_MAP.get(typeName);
	}
	
	public String toString() {
		return typeName;
	}
	
	public abstract TypeCodec getCodec(Operator operator, boolean optional);
	
	public final static Type U8 = new IntegerType("u8", TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER); 
	public final static Type U16 = new IntegerType("u16", TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER); 
	public final static Type U32 = new IntegerType("u32", TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER); 
	public final static Type U64 = new IntegerType("u64", TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER) {
		protected ScalarValue getVal(String value) {
			try {
				return new LongValue(Long.parseLong(value));
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}; 
	public final static Type I8 = new IntegerType("i8", TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER); 
	public final static Type I16 = new IntegerType("i16", TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER); 
	public final static Type I32 = new IntegerType("i32", TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER); 
	public final static Type I64 = new IntegerType("i64", TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER) {
		protected ScalarValue getVal(String value) {
			try {
				return new LongValue(Long.parseLong(value));
			} catch (NumberFormatException e) {
				return null;
			}
		}
	};  
	public final static Type STRING = new StringType("string", TypeCodec.ASCII, TypeCodec.NULLABLE_ASCII);
	public final static Type ASCII = new StringType("ascii", TypeCodec.ASCII, TypeCodec.NULLABLE_ASCII);
	public final static Type UNICODE = new StringType("unicode", TypeCodec.UNICODE, TypeCodec.NULLABLE_UNICODE);
	public final static Type BYTE_VECTOR = new SimpleType("byte", TypeCodec.BYTE_VECTOR_TYPE, TypeCodec.NULLABLE_BYTE_VECTOR_TYPE){
		protected ScalarValue getVal(String value) {
			return new ByteVectorValue(value.getBytes());
		}};
	
	public final static Type DECIMAL = new SimpleType("decimal", TypeCodec.SF_SCALED_NUMBER, TypeCodec.NULLABLE_SF_SCALED_NUMBER) {
		public TypeCodec getCodec(Operator operator, boolean optional) {
			if (operator instanceof TwinOperator)
				return TypeCodec.TF_SCALED_NUMBER;
			return super.getCodec(operator, optional);
		}
		
		protected ScalarValue getVal(String value) {
			return new DecimalValue(Double.parseDouble(value));
		}	
	};
		
	public static final Type[] ALL_TYPES = new Type[] { U8, U16, U32, U64, I8, I16, I32, I64, STRING, ASCII, UNICODE, BYTE_VECTOR, DECIMAL };
	public static final Type[] INTEGER_TYPES = new Type[] { U8, U16, U32, U64, I8, I16, I32, I64 };

	public abstract ScalarValue getValue(String value);
}
