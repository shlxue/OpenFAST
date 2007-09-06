package org.openfast.template.type.codec;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.openfast.DateValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;

public class DateString extends TypeCodec {
	private static final long serialVersionUID = 1L;
	private final DateFormat formatter;

	public DateString(String format) {
		formatter = new SimpleDateFormat(format);
	}
	
	public ScalarValue decode(InputStream in) {
		try {
			return new DateValue(formatter.parse(TypeCodec.ASCII.decode(in).toString()));
		} catch (ParseException e) {
			Global.handleError(FastConstants.PARSE_ERROR, "", e);
			return null;
		}
	}

	public byte[] encodeValue(ScalarValue value) {
		return TypeCodec.ASCII.encode(new StringValue(formatter.format(((DateValue) value).value)));
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}
