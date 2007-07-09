package org.openfast.template.type;

import java.text.ParseException;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class DateType extends Type {
	public DateType() {
		super("date");
	}

	public ScalarValue getDefaultValue() {
		return new DateValue(new Date(0));
	}

	public boolean isValueOf(ScalarValue previousValue) {
		return previousValue instanceof DateValue;
	}

	public TypeCodec getCodec(Operator operator, boolean optional) {
		return Global.getCodec(this, operator, optional);
	}

	public ScalarValue getValue(String value) {
		try {
			return new DateValue(Global.getDateFormatter().parse(value));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
