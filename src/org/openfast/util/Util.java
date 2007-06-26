package org.openfast.util;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;

public class Util {

	private static final TwinValue NO_DIFF = new TwinValue(new IntegerValue(0), new StringValue(""));

	public static boolean isBiggerThanInt(long value) {
		return value > Integer.MAX_VALUE || value < Integer.MIN_VALUE;
	}
	
	public static ScalarValue getDifference(StringValue newValue, StringValue priorValue) {
		String value = newValue.value;
		if (priorValue == null || priorValue.value.length() == 0) return new TwinValue(new IntegerValue(0), newValue);
		if (priorValue.equals(newValue)) return NO_DIFF;
		String base = priorValue.value;
		int appendIndex = 0;
		for (; appendIndex < base.length() && appendIndex < value.length() &&
			value.charAt(appendIndex) == base.charAt(appendIndex); appendIndex++);
		String append = value.substring(appendIndex);
		
		int prependIndex = 1;
		for (; prependIndex <= value.length() && prependIndex <= base.length() &&
			value.charAt(value.length() - prependIndex) == base.charAt(base.length() - prependIndex); prependIndex++);
		String prepend = value.substring(0, value.length() - prependIndex + 1);
		
		if (prepend.length() == 0 || append.length() == 0) return ScalarValue.NULL;
		if (prepend.length() < append.length()) {
			return new TwinValue(new IntegerValue(prependIndex - base.length() - 2), new StringValue(prepend));
		} 
		return new TwinValue(new IntegerValue(base.length() - appendIndex), new StringValue(append));
	}
	
	public static StringValue applyDifference(StringValue baseValue, TwinValue diffValue) {
		int subtraction = ((IntegerValue)diffValue.first).value;
		String base = baseValue.value;
		String diff = ((StringValue)diffValue.second).value;
		if (subtraction < 0) {
			subtraction = -1 * subtraction - 1;
			return new StringValue(diff + base.substring(subtraction, base.length()));
		}
		return new StringValue(base.substring(0, base.length() - subtraction) + diff);
	}
}
