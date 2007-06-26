package org.openfast.util;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

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
