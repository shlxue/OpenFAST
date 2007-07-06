/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.util;

import java.util.Collection;
import java.util.Iterator;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;


public class Util {
    private static final TwinValue NO_DIFF = new TwinValue(new IntegerValue(0),
            new StringValue(""));

    public static boolean isBiggerThanInt(long value) {
        return (value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE);
    }

    public static ScalarValue getDifference(StringValue newValue,
        StringValue priorValue) {
        String value = newValue.value;

        if ((priorValue == null) || (priorValue.value.length() == 0)) {
            return new TwinValue(new IntegerValue(0), newValue);
        }

        if (priorValue.equals(newValue)) {
            return NO_DIFF;
        }

        String base = priorValue.value;
        int appendIndex = 0;

        while ((appendIndex < base.length()) &&
               (appendIndex < value.length()) &&
               (value.charAt(appendIndex) == base.charAt(appendIndex)))
            appendIndex++;

        String append = value.substring(appendIndex);

        int prependIndex = 1;

        while((prependIndex <= value.length()) &&
              (prependIndex <= base.length()) &&
              (value.charAt(value.length() - prependIndex) == base.charAt(base.length() - prependIndex)))
        	prependIndex++;

        String prepend = value.substring(0, value.length() - prependIndex + 1);

        if (prepend.length() < append.length()) {
            return new TwinValue(new IntegerValue(prependIndex - base.length() -
                    2), new StringValue(prepend));
        }

        return new TwinValue(new IntegerValue(base.length() - appendIndex),
            new StringValue(append));
    }

    public static StringValue applyDifference(StringValue baseValue,
        TwinValue diffValue) {
        int subtraction = ((IntegerValue) diffValue.first).value;
        String base = baseValue.value;
        String diff = ((StringValue) diffValue.second).value;

        if (subtraction < 0) {
            subtraction = (-1 * subtraction) - 1;

            return new StringValue(diff +
                base.substring(subtraction, base.length()));
        }

        return new StringValue(base.substring(0, base.length() - subtraction) +
            diff);
    }

	public static String collectionToString(Collection set) {
		StringBuffer buffer = new StringBuffer();
		Iterator iter = set.iterator();
		buffer.append("{");
		while (iter.hasNext()) {
			buffer.append(iter.next()).append(",");
		}
		buffer.deleteCharAt(buffer.length()-1);
		buffer.append("}");
		return buffer.toString();
	}
}
