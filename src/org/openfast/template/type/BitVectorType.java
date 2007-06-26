/**
 * 
 */
package org.openfast.template.type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openfast.BitVector;
import org.openfast.BitVectorValue;
import org.openfast.ScalarValue;

public final class BitVectorType extends Type {
	BitVectorType(Integer type, String name, String[] names) {
		super(type, name, names);
	}

	public byte[] encodeValue(ScalarValue value) {
		return ((BitVectorValue) value).value.getBytes();
	}

	public ScalarValue decode(InputStream in) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int byt;
		do {
			try {
				byt = in.read();
				if (byt < 0) return null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			buffer.write(byt);
		} while ((byt & 0x80) == 0);
		return new BitVectorValue(new BitVector(buffer.toByteArray()));
	}

	public ScalarValue parse(String value) {
		return null;
	}

	public ScalarValue getDefaultValue() {
		return new BitVectorValue(new BitVector(0));
	}
}