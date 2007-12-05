package org.openfast.template.type.codec;

import java.io.InputStream;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.ScalarValue;
import org.openfast.template.LongValue;

public class EpochTimestamp extends TypeCodec {

	private static final long serialVersionUID = 1L;

	public ScalarValue decode(InputStream in) {
		return new DateValue(new Date(TypeCodec.INTEGER.decode(in).toLong()));
	}

	public byte[] encodeValue(ScalarValue value) {
		return TypeCodec.INTEGER.encodeValue(new LongValue(value.toLong()));
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}
