package org.openfast.template.type.codec;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.util.Util;

public class MillisecondsSinceMidnight extends TypeCodec {
	public ScalarValue decode(InputStream in) {
		int millisecondsSinceMidnight = TypeCodec.INTEGER.decode(in).toInt();
		Calendar cal = Calendar.getInstance();
		int hour = millisecondsSinceMidnight / 3600000;
		millisecondsSinceMidnight -= hour * 3600000;
		cal.set(Calendar.HOUR_OF_DAY, hour);
		int minute = millisecondsSinceMidnight / 60000;
		millisecondsSinceMidnight -= minute * 60000;
		cal.set(Calendar.MINUTE, minute);
		int second = millisecondsSinceMidnight / 1000;
		millisecondsSinceMidnight -= second * 1000;
		cal.set(Calendar.SECOND, second);
		int millisecond = millisecondsSinceMidnight;
		cal.set(Calendar.MILLISECOND, millisecond);
		return new DateValue(cal.getTime());
	}

	public byte[] encodeValue(ScalarValue value) {
		Date date = ((DateValue) value).value;
		int millisecondsSinceMidnight = Util.millisecondsSinceMidnight(date);
		return TypeCodec.INTEGER.encodeValue(new IntegerValue(millisecondsSinceMidnight));
	}

}
