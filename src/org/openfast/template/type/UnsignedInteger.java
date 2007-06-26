/**
 * 
 */
package org.openfast.template.type;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.LongValue;

public final class UnsignedInteger extends IntegerType {
	UnsignedInteger(Integer type, String name, String[] names) {
		super(type, name, names);
	}

	public byte[] encodeValue(ScalarValue scalarValue) {
		long value;
		if (scalarValue instanceof IntegerValue)
			value = ((IntegerValue) scalarValue).value;
		else
			value = ((LongValue) scalarValue).value;
		int size = getUnsignedIntegerSize(value);
		byte[] encoded = new byte[size];
		for (int factor = 0; factor < size; factor++)
		{
			encoded[size - factor - 1] = (byte) ( (value >> (factor * 7)) & 0x7f);  
		}
		return encoded;
	}

	public ScalarValue decode(InputStream in) {
		long value = 0;
		int byt;
		try {
			do {
				byt = in.read();
				value = (value << 7) | (byt & 0x7f);
			} while ((byt & 0x80) == 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return createValue(value);
	}
}