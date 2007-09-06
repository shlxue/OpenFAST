package org.openfast.template.type.codec;

import java.io.InputStream;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.util.Util;

public class TimestampInteger extends TypeCodec {
	private static final long serialVersionUID = 1L;

	public ScalarValue decode(InputStream in) {
		int intValue = ((IntegerValue) TypeCodec.UINT.decode(in)).value;
		return new DateValue(Util.toTimestamp(intValue));
	}

	public byte[] encodeValue(ScalarValue value) {
		Date date = ((DateValue) value).value;
		int intValue = Util.timestampToInt(date);
		return TypeCodec.UINT.encode(new IntegerValue(intValue));
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}
