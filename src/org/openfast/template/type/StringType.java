/**
 * 
 */
package org.openfast.template.type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openfast.ScalarValue;
import org.openfast.StringValue;

final class StringType extends Type {
	StringType(Integer type, String name, String[] names) {
		super(type, name, names);
	}

	public byte[] encodeValue(ScalarValue value) {
		if (value == null || value.isNull()) throw new IllegalStateException("Only nullable strings can represent null values."); 
		String string = ((StringValue) value).value;
		if (string != null && string.length() == 0) return Type.NULL_VALUE_ENCODING;
		return string.getBytes();
	}

	public ScalarValue decode(InputStream in) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int byt;
		try {
			do {
				byt = in.read();
				buffer.write(byt);
			} while ((byt & 0x80) == 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] bytes = buffer.toByteArray();
		bytes[bytes.length - 1] &= 0x7f;
		if (bytes.length == 1 && bytes[0] == 0)
			return new StringValue("");
		return new StringValue(new String(bytes));
	}

	public ScalarValue parse(String value) {
		return new StringValue(value);
	}

	public ScalarValue getDefaultValue() {
		return new StringValue("");
	}
}