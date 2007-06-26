package org.openfast.template;

import org.openfast.ScalarValue;

public class TwinValue extends ScalarValue {
	public final ScalarValue first;
	public final ScalarValue second;
	
	public TwinValue(ScalarValue first, ScalarValue second) {
		this.first = first;
		this.second = second;
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TwinValue)) return false;
		return equals((TwinValue) obj);
	}
	
	private boolean equals(TwinValue other) {
		return (first.equals(other.first) && second.equals(other.second));
	}
	
	public String toString() {
		return first.toString() + ", " + second.toString();
	}
}
