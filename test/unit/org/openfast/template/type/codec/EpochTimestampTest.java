package org.openfast.template.type.codec;

import java.util.Calendar;

import org.openfast.DateValue;
import org.openfast.test.OpenFastTestCase;

public class EpochTimestampTest extends OpenFastTestCase {

	public void testEncodeDecode() {
		Calendar cal = Calendar.getInstance();
		cal.set(2007, 7, 7, 12, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEncodeDecode(new DateValue(cal.getTime()), "00100010 01000100 00001000 00110111 00110000 10000000", TypeCodec.EPOCH_TIMESTAMP);
	}
}
