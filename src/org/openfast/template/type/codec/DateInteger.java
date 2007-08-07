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

public class DateInteger extends TypeCodec {

	public ScalarValue decode(InputStream in) {
		long longValue = ((ScalarValue) TypeCodec.UINT.decode(in)).toLong();
		int year = (int) (longValue / 10000);
		int month = (int) ((longValue - (year * 10000)) / 100);
		int day = (int) (longValue % 100);
		return new DateValue(new Date(year - 1900, month - 1, day));
	}

	public byte[] encodeValue(ScalarValue value) {
		Date date = ((DateValue) value).value;
		int intValue = date.getDate() + (date.getMonth()+1) * 100 + (1900+date.getYear()) * 10000;
		return TypeCodec.UINT.encode(new IntegerValue(intValue));
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}
