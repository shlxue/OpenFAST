package org.openfast;

import java.math.BigDecimal;

public class DecimalValue extends NumericValue {

	public static final DecimalValue NULL = new DecimalValue(0.0) {
		public boolean isNull() {
			return true;
		}
		public String toString() {
			return "DecimalValue [NULL]";
		}
		public NumericValue add(NumericValue value) {
			return this;
		}
		public NumericValue subtract(NumericValue value) {
			return this;
		}
	};
	
	public final double value;
	public final int exponent;
	public final int mantissa;

	public DecimalValue(double value)
	{
		if (value == 0.0) {
			this.value = 0.0;
			this.exponent = 0;
			this.mantissa = 0;
			return;
		}
		this.value = value;
		BigDecimal decimalValue = BigDecimal.valueOf(value);
		int exponent = decimalValue.scale(); 
		int mantissa = decimalValue.unscaledValue().intValue();
		while (mantissa % 10 == 0 && mantissa != 0)
		{
			mantissa /= 10;
			exponent -= 1;
		}
		this.mantissa = mantissa;
		this.exponent = -exponent;
	}
	
	public DecimalValue(int mantissa, int exponent) {
		this.mantissa = mantissa;
		this.exponent = exponent;
		if (exponent < 0)
			this.value = mantissa / Math.pow(10, -exponent);
		else
			this.value = mantissa * Math.pow(10, exponent);
	}

	public NumericValue increment() {
		return null;
	}

	public NumericValue decrement() {
		return null;
	}
	
	public boolean isNull() {
		return false;
	}
	
	public String toString() {
		return "DecimalValue [" + value + "]";
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DecimalValue)) return false;
		return equals((DecimalValue) obj);
	}
	
	public boolean equals(DecimalValue other) {
		return other.value == this.value;
	}

	public NumericValue subtract(NumericValue priorValue) {
		return new DecimalValue(this.value - ((DecimalValue)priorValue).value);
	}

	public NumericValue add(NumericValue addend) {
		return new DecimalValue(((DecimalValue) addend).value + this.value);
	}
	
	public String serialize() {
		return String.valueOf(value);
	}

	public boolean equals(int value) {
		return (double) value == this.value;
	}

	public long getLong() {
		return (long) value;
	}

	public int getInt() {
		return (int) value;
	}

}
