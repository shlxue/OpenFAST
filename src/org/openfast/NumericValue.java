package org.openfast;

public abstract class NumericValue extends ScalarValue {

	public abstract NumericValue increment();
	public abstract NumericValue decrement();
	public abstract NumericValue subtract(NumericValue priorValue);
	public abstract NumericValue add(NumericValue addend);
	public abstract boolean equals(int value);
	public abstract long getLong();
	public abstract int getInt();
}
