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


package org.openfast;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.test.OpenFastTestCase;


public class DecimalValueTest extends OpenFastTestCase {
    public void testMantissaAndExponent() {
        DecimalValue value = new DecimalValue(9427.55);
        assertEquals(942755, value.mantissa);
        assertEquals(-2, value.exponent);

        value = new DecimalValue(942755, -2);
        assertEquals(9427.55, value.value, .01);
    }
    
    public void testToByte() {
    	assertEquals(100, d(100.0).toByte());
    }
    
    public void testToByteWithDecimalPart() {
    	try {
    		d(100.1).toByte();
    		fail();
    	} catch (FastException e) {
    		assertEquals(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, e.getCode());
    	}
    }
    
    public void testToShort() {
    	assertEquals(128, d(128.0).toShort());
    }
    
    public void testToShortWithDecimalPart() {
    	try {
    		d(100.1).toShort();
    		fail();
    	} catch (FastException e) {
    		assertEquals(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, e.getCode());
    	}
    }
    
    public void testToInt() {
    	assertEquals(100, d(100.0).toInt());
    }
    
    public void testToIntWithDecimalPart() {
    	try {
    		d(100.1).toInt();
    		fail();
    	} catch (FastException e) {
    		assertEquals(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, e.getCode());
    	}
    }
    
    public void testToLong() {
    	assertEquals(10000000000000L, d(10000000000000.0).toLong());
    }
    
    public void testToLongWithDecimalPart() {
    	try {
    		d(100.1).toLong();
    		fail();
    	} catch (FastException e) {
    		assertEquals(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, e.getCode());
    	}
    }
    
    
}
