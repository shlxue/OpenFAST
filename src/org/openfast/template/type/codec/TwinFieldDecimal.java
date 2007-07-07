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


/**
 *
 */
package org.openfast.template.type.codec;

import java.io.InputStream;

import org.openfast.ByteUtil;
import org.openfast.DecimalValue;
import org.openfast.ScalarValue;
import org.openfast.template.TwinValue;


final class TwinFieldDecimal extends TypeCodec {
    TwinFieldDecimal() { }

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * @param v The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue v) {
    	if (v.isNull())
    		return NULL_VALUE_ENCODING;
        TwinValue value = (TwinValue) v;
        if (value.first != null && value.second != null)
        	return ByteUtil.combine(TypeCodec.INTEGER.encode(value.first), TypeCodec.INTEGER.encode(value.second));
        if (value.second != null)
        	return TypeCodec.INTEGER.encode(value.second);
        if (value.first != null)
        	return TypeCodec.INTEGER.encode(value.first);
        return new byte[] {};
    }

    /**
     * Reads in a stream of data and stores it to a TwinValue object
     * @param in The InputStream to be decoded
     * @return Returns a TwinValue object with the data stream
     */
    public ScalarValue decode(InputStream in) {
        return new TwinValue(TypeCodec.INTEGER.decode(in), TypeCodec.INTEGER.decode(in));
    }

    /**
     * 
     * @param value The string value to be converted
     * @return Returns a DecimalValue object with the passed string as its parameter
     */
    public ScalarValue fromString(String value) {
        return new DecimalValue(Double.parseDouble(value));
    }

    /**
     * 
     * @return Returns a new DecimalValue object with a default value of 0.0
     */
    public ScalarValue getDefaultValue() {
        return new DecimalValue(0.0);
    }
}
