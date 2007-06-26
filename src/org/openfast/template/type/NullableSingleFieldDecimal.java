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
import org.openfast.error.FastConstants;

final class NullableSingleFieldDecimal extends Type {
	NullableSingleFieldDecimal() {
		super(DECIMAL, "twin field scaled number", new String[] { "decimal" }, true);
	}

	public byte[] encodeValue(ScalarValue v) {
		if (v == ScalarValue.NULL) return Type.NULL_VALUE_ENCODING;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DecimalValue value = (DecimalValue) v;
		try {
			if (Math.abs(value.exponent) > 63)
				FastConstants.handleError(FastConstants.LARGE_DECIMAL, "");
			buffer.write(Type.NULLABLE_INTEGER.encode(new IntegerValue(value.exponent)));
			buffer.write(Type.INTEGER.encode(new IntegerValue(value.mantissa)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buffer.toByteArray();
	}

	public ScalarValue decode(InputStream in) {
		ScalarValue exp = Type.NULLABLE_INTEGER.decode(in);
		if (exp == null || exp.isNull())
			return null;
		int exponent = ((IntegerValue) exp).value;
		int mantissa = ((IntegerValue) Type.INTEGER.decode(in)).value;
		DecimalValue decimalValue = new DecimalValue(mantissa, exponent);
		return decimalValue;
	}
	
	public ScalarValue parse(String value) {
		return new DecimalValue(Double.parseDouble(value));
	}

	public ScalarValue getDefaultValue() {
		return new DecimalValue(0.0);
	}
}