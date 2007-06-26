/**
 * 
 */
package org.openfast.template.type;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

final class ByteVectorType extends Type {
	ByteVectorType(Integer type, String name, String[] names) {
		super(type, name, names);
	}

	public byte[] encode(ScalarValue value) {
		ByteVectorValue byteVectorValue = (ByteVectorValue) value;
		int lengthSize = IntegerType.getUnsignedIntegerSize(byteVectorValue.value.length);
		byte[] encoding = new byte[byteVectorValue.value.length + lengthSize];
		byte[] length = Type.UINT.encode(new IntegerValue(byteVectorValue.value.length));
		System.arraycopy(length, 0, encoding, 0, lengthSize);
		System.arraycopy(byteVectorValue.value, 0, encoding, lengthSize, byteVectorValue.value.length);
		return encoding;
	}

	public ScalarValue decode(InputStream in) {
		int length = ((IntegerValue)Type.UINT.decode(in)).value;
		byte[] encoding = new byte[length];
		for (int i=0; i<length; i++)
			try {
				encoding[i] = (byte) in.read();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		return new ByteVectorValue(encoding);
	}

	public byte[] encodeValue(ScalarValue value) {
		throw new NotImplementedException();
	}

	public ScalarValue parse(String value) {
		return null;
	}

	public ScalarValue getDefaultValue() {
		return new ByteVectorValue(new byte[] {});
	}
}