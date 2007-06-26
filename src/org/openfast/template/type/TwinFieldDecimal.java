/**
 * 
 */
package org.openfast.template.type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;

final class TwinFieldDecimal extends Type {
	TwinFieldDecimal(Integer type, String name, String[] names) {
		super(type, name, names);
	}

	public byte[] encodeValue(ScalarValue v) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DecimalValue value = (DecimalValue) v;
		try {
			if (v == DecimalValue.NULL) return Type.NULL_TF_DECIMAL_VALUE_ENCODING;
			buffer.write(Type.INTEGER.encode(new IntegerValue(value.exponent)));
			buffer.write(Type.INTEGER.encode(new IntegerValue(value.mantissa)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buffer.toByteArray();
	}

	public ScalarValue decode(InputStream in) {
		int exponent = ((IntegerValue) Type.INTEGER.decode(in)).value;
		if (exponent == Type.NULL_SCALED_NUMBER) return DecimalValue.NULL;
		int mantissa = ((IntegerValue) Type.INTEGER.decode(in)).value;
		return new DecimalValue(mantissa, exponent);
	}

	public ScalarValue parse(String value) {
		return new DecimalValue(Double.parseDouble(value));
	}

	public ScalarValue getDefaultValue() {
		return new DecimalValue(0.0);
	}
}