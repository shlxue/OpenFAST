package org.openfast;

import org.openfast.template.LongValue;


public class IntegerValue extends NumericValue {
		
	public final int value;

	public IntegerValue(int value) {
		this.value = value;
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IntegerValue)) return false;
		return equals((IntegerValue) obj);
	}
	
	private boolean equals(IntegerValue otherValue) {
		return value == otherValue.value;
	}
	
	public boolean equalsValue(String defaultValue) {
		return Integer.parseInt(defaultValue) == value;
	}

	public NumericValue increment() {
		return new IntegerValue(value+1);
	}

	public NumericValue decrement() {
		return new IntegerValue(value-1);
	}
	
	public String toString() {
		return "IntegerValue [" + value + "]";
	}

	public NumericValue subtract(NumericValue subend) {
		if (subend instanceof LongValue) return new LongValue(this.value - subend.getLong());
		return new IntegerValue(this.value - subend.getInt());
	}

	public NumericValue add(NumericValue addend) {
		if (addend instanceof LongValue) return addend.add(this);
		return new IntegerValue(this.value + addend.getInt());
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
		return value;
	}
}
