package org.openfast;

import java.util.Date;

public class DateValue extends ScalarValue {
	public final Date value;

	public DateValue(Date date) {
		this.value = date;
	}

	public long toLong() {
		return value.getTime();
	}
	
	public String toString() {
		return Global.getDateFormatter().format(value);
	}
	
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null || !(other instanceof DateValue)) return false;
		return equals((DateValue) other);
	}
	
	private boolean equals(DateValue other) {
		return other.value.equals(value);
	}
}
