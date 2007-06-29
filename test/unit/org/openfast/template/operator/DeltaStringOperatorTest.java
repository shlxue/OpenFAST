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


package org.openfast.template.operator;

import junit.framework.TestCase;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;


public class DeltaStringOperatorTest extends TestCase {
    private Scalar field;

    protected void setUp() throws Exception {
    }

    public void testGetValueToEncodeMandatory() {
        field = new Scalar(null, Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false);

        assertEquals(tv(0, "ABCD"), encode("ABCD", ScalarValue.UNDEFINED));
        assertEquals(tv(1, "E"), encode("ABCE", s("ABCD")));
        assertEquals(tv(-2, "Z"), encode("ZBCE", s("ABCE")));
        assertEquals(tv(-1, "Y"), encode("YZBCE", s("ZBCE")));
        assertEquals(tv(0, "F"), encode("YZBCEF", s("YZBCE")));
    }

    public void testDecodeValueMandatory() {
        field = new Scalar(null, Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false);

        assertEquals(new StringValue("ABCD"),
            decode(tv(0, "ABCD"), ScalarValue.UNDEFINED));
        assertEquals(new StringValue("ABCE"), decode(tv(1, "E"), s("ABCD")));
        assertEquals(new StringValue("ZBCE"), decode(tv(-2, "Z"), s("ABCE")));
        assertEquals(new StringValue("YZBCE"), decode(tv(-1, "Y"), s("ZBCE")));
        assertEquals(new StringValue("YZBCEF"), decode(tv(0, "F"), s("YZBCE")));
    }

    public void testGetValueToEncodeOptional() {
        field = new Scalar(null, Type.ASCII, Operator.DELTA,
                ScalarValue.UNDEFINED, true);

        assertEquals(tv(0, "ABCD"), encode("ABCD", ScalarValue.UNDEFINED));
        assertEquals(tv(1, "E"), encode("ABCE", s("ABCD")));
        assertEquals(tv(-2, "Z"), encode("ZBCE", s("ABCE")));
        assertEquals(tv(-1, "Y"), encode("YZBCE", s("ZBCE")));
        assertEquals(tv(0, "F"), encode("YZBCEF", s("YZBCE")));
        assertEquals(ScalarValue.NULL, encode(null, s("YZBCEF")));
    }

    public void testDecodeValueOptional() {
        field = new Scalar(null, Type.ASCII, Operator.DELTA,
                ScalarValue.UNDEFINED, true);

        assertEquals(new StringValue("ABCD"),
            decode(tv(0, "ABCD"), ScalarValue.UNDEFINED));
        assertEquals(new StringValue("ABCE"), decode(tv(1, "E"), s("ABCD")));
        assertEquals(new StringValue("ZBCE"), decode(tv(-2, "Z"), s("ABCE")));
        assertEquals(new StringValue("YZBCE"), decode(tv(-1, "Y"), s("ZBCE")));
        assertEquals(new StringValue("YZBCEF"), decode(tv(0, "F"), s("YZBCE")));
        assertEquals(null, decode(ScalarValue.NULL, s("YZBCEF")));
    }

    private ScalarValue s(String value) {
        return new StringValue(value);
    }

    private ScalarValue encode(String value, ScalarValue priorValue) {
        if (value == null) {
            return Operator.DELTA_STRING.getValueToEncode(null, priorValue,
                field);
        }

        return Operator.DELTA_STRING.getValueToEncode(new StringValue(value),
            priorValue, field);
    }

    private ScalarValue decode(ScalarValue diff, ScalarValue priorValue) {
        return Operator.DELTA_STRING.decodeValue(diff, priorValue, field);
    }

    private TwinValue tv(int subtraction, String diff) {
        return new TwinValue(new IntegerValue(subtraction),
            new StringValue(diff));
    }
}
