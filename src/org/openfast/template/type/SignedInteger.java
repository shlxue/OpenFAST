/**
 * 
 */
package org.openfast.template.type;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.NumericValue;
import org.openfast.ScalarValue;

public final class SignedInteger extends IntegerType {
	SignedInteger(Integer type, String name, String[] names) {
		super(type, name, names);
	}

	public byte[] encodeValue(ScalarValue value) {
		long longValue = ((NumericValue) value).getLong();
		int size = getSignedIntegerSize(longValue);
		byte[] encoding = new byte[size];
		for (int factor = 0; factor < size; factor++)
		{
			int bitMask = (factor == size - 1) ? 0x3f : 0x7f;
			encoding[size - factor - 1] = (byte) ( (longValue >> (factor * 7)) & bitMask);  
		}
		// Get the sign bit from the long value and set it on the first byte
		// 01000000 00000000 ... 00000000
		//  ^----SIGN BIT
		encoding[0] |= 0x40 & (longValue >> 57);
		return encoding;
	}

	public ScalarValue decode(InputStream in) {
		long value = 0;
		try {
			int byt = in.read();
			if ((byt & 0x40) > 0) value = -1;
			value = (value << 7) | (byt & 0x7f);
			while ((byt & 0x80) == 0) {
				byt = in.read();
				value = (value << 7) | (byt & 0x7f);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return createValue(value);
	}
}