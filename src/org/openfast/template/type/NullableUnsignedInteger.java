/**
 * 
 */
package org.openfast.template.type;

import java.io.InputStream;

import org.openfast.IntegerValue;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;

public final class NullableUnsignedInteger extends IntegerType {
	NullableUnsignedInteger(Integer type, String name, String[] names, boolean nullable) {
		super(type, name, names, nullable);
	}

	public byte[] encodeValue(ScalarValue v) {
		if (v.isNull()) return Type.NULL_VALUE_ENCODING;
		return Type.UINT.encodeValue(((IntegerValue) v).increment());
	}

	public ScalarValue decode(InputStream in) {
		NumericValue value = (NumericValue) Type.UINT.decode(in);
		if (value.equals(0)) return null;
		return value.decrement();
	}
}