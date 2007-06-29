package org.openfast.template.type.codec;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;

public class NullableByteVector extends NotStopBitEncodedType {

	public ScalarValue decode(InputStream in) {
        ScalarValue decode = TypeCodec.NULLABLE_UNSIGNED_INTEGER.decode(in);
        if (decode == null)
        	return null;
		int length = ((IntegerValue) decode).value;
        byte[] encoding = new byte[length];

        for (int i = 0; i < length; i++)
        	try {
                encoding[i] = (byte) in.read();
            } catch (IOException e) {
            	FastConstants.handleError(FastConstants.IO_ERROR, "An error occurred while decoding a nullable byte vector.", e);
            }
        return new ByteVectorValue(encoding);
	}

	public byte[] encodeValue(ScalarValue value) {
		if (value.isNull())
			return TypeCodec.NULLABLE_UNSIGNED_INTEGER.encodeValue(ScalarValue.NULL);
        ByteVectorValue byteVectorValue = (ByteVectorValue) value;
        int lengthSize = IntegerCodec.getUnsignedIntegerSize(byteVectorValue.value.length);
        byte[] encoding = new byte[byteVectorValue.value.length + lengthSize];
        byte[] length = TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(byteVectorValue.value.length));
        System.arraycopy(length, 0, encoding, 0, lengthSize);
        System.arraycopy(byteVectorValue.value, 0, encoding, lengthSize,
            byteVectorValue.value.length);
        return encoding;
	}

	public ScalarValue getDefaultValue() {
		return new ByteVectorValue(new byte[] {});
	}

	public ScalarValue fromString(String value) {
		return new ByteVectorValue(value.getBytes());
	}

}
