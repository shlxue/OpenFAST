package org.openfast.template.type.codec;

import org.openfast.ScalarValue;

public abstract class NotStopBitEncodedType extends TypeCodec {

	public byte[] encode(ScalarValue value) {
		return encodeValue(value);
	}
}
