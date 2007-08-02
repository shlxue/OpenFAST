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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;


public class UtilTest extends TestCase {
	public void testIt() {
		DateFormat format = new SimpleDateFormat("mm:ss.S");
//		assertEquals("10:01.2", format.format(new Date(10 * 60000 + 1000 + 202)));
		System.out.println((int) 'T');
	}
	public void testCollectionToString() {
		Map map = new LinkedHashMap();
		map.put("abc", "123");
		map.put("def", "456");
		assertEquals("{abc,def}", Util.collectionToString(map.keySet()));
	}
	
    public void testGetDifference() {
        assertEquals(tv(0, "d"), diff("abc", "abcd"));
        assertEquals(tv(1, "ed"), diff("abc", "abed"));

        assertEquals(tv(0, "GEH6"), diff("", "GEH6"));
        assertEquals(tv(2, "M6"), diff("GEH6", "GEM6"));
        assertEquals(tv(-3, "ES"), diff("GEM6", "ESM6"));
        assertEquals(tv(-1, "RS"), diff("ESM6", "RSESM6"));
        assertEquals(tv(0, ""), diff("RSESM6", "RSESM6"));
    }

    public void testApplyDifference() {
        assertEquals(s("abcd"), apply("abc", tv(0, "d")));
        assertEquals(s("abed"), apply("abc", tv(1, "ed")));

        assertEquals(s("GEH6"), apply("", tv(0, "GEH6")));
        assertEquals(s("GEM6"), apply("GEH6", tv(2, "M6")));
        assertEquals(s("ESM6"), apply("GEM6", tv(-3, "ES")));
        assertEquals(s("RSESM6"), apply("ESM6", tv(-1, "RS")));
        assertEquals(s("RSESM6"), apply("RSESM6", tv(0, "")));
    }

    private StringValue s(String value) {
        return new StringValue(value);
    }

    private StringValue apply(String base, TwinValue diff) {
        return Util.applyDifference(new StringValue(base), diff);
    }

    private TwinValue tv(int sub, String diff) {
        return new TwinValue(new IntegerValue(sub), new StringValue(diff));
    }

    private ScalarValue diff(String base, String value) {
        return Util.getDifference(new StringValue(value), new StringValue(base));
    }
}
