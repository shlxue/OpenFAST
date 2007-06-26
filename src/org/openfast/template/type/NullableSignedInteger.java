/**
 * 
 */
package org.openfast.template.type;

import java.io.InputStream;

import org.openfast.IntegerValue;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;

public final class NullableSignedInteger extends IntegerType {
	NullableSignedInteger(Integer type, String name, String[] names, boolean nullable) {
		super(type, name, names, nullable);
	}

	public byte[] encodeValue(ScalarValue value) {
		if (value.isNull()) return Type.NULL_VALUE_ENCODING;
		IntegerValue intValue = (IntegerValue) value;
		if (intValue.value >= 0) return Type.INTEGER.encodeValue(intValue.increment());
		else return Type.INTEGER.encodeValue(intValue);
	}

	public ScalarValue decode(InputStream in) {
		NumericValue numericValue = ((NumericValue) Type.INTEGER.decode(in));
		long value = numericValue.getLong();
		if (value == 0) return ScalarValue.NULL;
		if (value > 0) return numericValue.decrement();
		return numericValue;
	}
}