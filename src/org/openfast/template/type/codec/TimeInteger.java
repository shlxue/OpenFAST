package org.openfast.template.type.codec;

import java.io.InputStream;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;

public class TimeInteger extends TypeCodec {
	public ScalarValue decode(InputStream in) {
		int intValue = ((IntegerValue) TypeCodec.UINT.decode(in)).value;
		int hour = intValue / 10000000;
		intValue -= hour * 10000000;
		int minute = intValue / 100000;
		intValue -= minute * 100000;
		int second = intValue / 1000;
		intValue -= second * 1000;
		int millisecond = intValue % 1000;
		return new DateValue(new Date(hour * 3600000 + minute * 60000 + second * 1000 + millisecond));
	}

	public byte[] encodeValue(ScalarValue value) {
		Date date = ((DateValue) value).value;
		int intValue = (int) (date.getHours() * 10000000 + date.getMinutes() * 100000 + date.getSeconds() * 1000 + date.getTime() % 1000);
		return TypeCodec.UINT.encode(new IntegerValue(intValue));
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}
