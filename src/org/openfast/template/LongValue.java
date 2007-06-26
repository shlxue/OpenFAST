package org.openfast.template;

import org.openfast.NumericValue;

public class LongValue extends NumericValue {
		
	public final long value;

	public LongValue(long value) {
		this.value = value;
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof LongValue)) return false;
		return equals((LongValue) obj);
	}
	
	private boolean equals(LongValue otherValue) {
		return value == otherValue.value;
	}
	
	public boolean equalsValue(String defaultValue) {
		return Integer.parseInt(defaultValue) == value;
	}

	public NumericValue increment() {
		return new LongValue(value+1);
	}

	public NumericValue decrement() {
		return new LongValue(value-1);
	}
	
	public String toString() {
		return String.valueOf(value);
	}

	public NumericValue subtract(NumericValue subend) {
		return new LongValue(this.value - subend.getLong());
	}

	public NumericValue add(NumericValue addend) {
		return new LongValue(this.value + addend.getLong());
	}
	
	public String serialize() {
		return String.valueOf(value);
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public long getLong() {
		return value;
	}
	
	public int getInt() {
		return (int) value;
	}
}
