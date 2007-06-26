package org.openfast;


public class StringValue extends ScalarValue {

	public final String value;

	public StringValue(String value) {
		this.value = value;
	}
	
	public String serialize() {
		return value;
	}

	public String toString() {
		return "StringValue [" + value + "]";
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof StringValue)) return false;
		return equals((StringValue) obj);
	}
	
	private boolean equals(StringValue otherValue) {
		return value.equals(otherValue.value);
	}
	
	public boolean equalsValue(String defaultValue) {
		return value.equals(defaultValue);
	}}
