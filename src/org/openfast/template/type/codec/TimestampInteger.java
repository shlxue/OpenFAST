package org.openfast.template.type.codec;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.Global;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;

public class TimestampInteger extends TypeCodec {
	private final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

	public ScalarValue decode(InputStream in) {
		int intValue = ((IntegerValue) TypeCodec.UINT.decode(in)).value;
		int year = intValue / 10000;
		int month = (intValue - (year * 10000)) / 100;
		int day = intValue % 100;
		return new DateValue(new Date(year, month, day));
	}

	public byte[] encodeValue(ScalarValue value) {
		Date date = ((DateValue) value).value;
		int intValue = date.getDay() + date.getMonth() * 100 + date.getYear() * 10000;
		return TypeCodec.UINT.encode(new IntegerValue(intValue));
	}

}
