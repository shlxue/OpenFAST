package org.openfast.template.type;

import java.io.InputStream;

import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;

public class NullableStringDelta extends Type {

	public NullableStringDelta(Integer type, String typeName, String[] typeNames) {
		super(type, typeName, typeNames);
	}

	public ScalarValue decode(InputStream in) {
		ScalarValue subtractionLength = Type.NULLABLE_INTEGER.decode(in);
		if (subtractionLength == ScalarValue.NULL) return null;
		ScalarValue difference = Type.STRING_TYPE.decode(in);
		return new TwinValue(subtractionLength, difference);
	}

	public byte[] encodeValue(ScalarValue value) {
		if (value == null || value == ScalarValue.NULL) return Type.NULL_VALUE_ENCODING;
		TwinValue diff = (TwinValue) value;
		byte[] subtractionLength = Type.NULLABLE_INTEGER.encode(diff.first);
		byte[] difference = Type.STRING_TYPE.encode(diff.second);
		byte[] encoded = new byte[subtractionLength.length+difference.length];
		System.arraycopy(subtractionLength, 0, encoded, 0, subtractionLength.length);
		System.arraycopy(difference, 0, encoded, subtractionLength.length, difference.length);
		return encoded;
	}

	public ScalarValue getDefaultValue() {
		return new StringValue("");
	}

	public ScalarValue parse(String value) {
		return new StringValue(value);
	}

}
