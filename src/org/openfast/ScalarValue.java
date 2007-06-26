package org.openfast;

import org.openfast.template.type.Type;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ScalarValue implements FieldValue {

	public static final ScalarValue UNDEFINED = new ScalarValue() {
		public boolean isUndefined() {
			return true;
		}
		public String toString() {
			return "UNDEFINED";
		}
	};
	static public final ScalarValue NULL = new ScalarValue() {
		public boolean isNull() {
			return true;
		}
		public String toString() {
			return "NULL";
		}
	};

	public boolean equalsValue(String defaultValue) {
		return false;
	}

	public boolean isUndefined() {
		return false;
	}
	
	public boolean isNull() {
		return false;
	}

	public static ScalarValue getValue(Integer type, String value) {
		if (type == Type.SIGNED_INTEGER || type == Type.UNSIGNED_INTEGER)
			return new IntegerValue(Integer.parseInt(value));
		else if (type == Type.DECIMAL)
			return new DecimalValue(Double.parseDouble(value));
		else if (type == Type.STRING)
			return new StringValue(value);
		else if (type == Type.BYTE_VECTOR)
			throw new NotImplementedException();
		throw new IllegalArgumentException("The type specified does not exist.");
	}

	public String serialize() {
		return "";
	}

}
