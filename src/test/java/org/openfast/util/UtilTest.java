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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;
import org.openfast.test.OpenFastTestCase;

public class UtilTest extends OpenFastTestCase {
    public void testCollectionToString() {
        Map map = new LinkedHashMap();
        map.put("abc", "123");
        map.put("def", "456");
        assertEquals("{abc,def}", Util.collectionToString(map.keySet()));
    }

    public void testIt() throws Exception {
        //String key = "564edf1f22ac28b586cfefb37e0bee1d11e5f0627e22342739a52391201770a3c0d9244a5690a7bbab3d09a9234dd3e041daf8c6995bf0b4ea571e048ca3dd0084b65a9e5d8edaf19cb971e7455df6cf05f03ddf92d359202ce14c8c8b5b1389f110a7e84fd7e0d229f07b791b51ca6361ca522ac2058cdf9d8767fb8776ed6b01e40bd8b6e05f9cfb8226e98c2bc067ace012f43f71c5aef4c0523112e2424b181d8054eaa67f417a5569b51b7f6c6640a64b4030eb6c6433a62ce797a78cc706ea788677324c80f75b9bb2b72430d180ae6ba3426e897c0c625aa7be8f6467145779abba96461dcfb83d11005afecb34258ef3d107cb480f7ae69f724d4f45";
          String key = "4d6146ff88305cbe1157a531814e09a8e2f04914798890d091b3f777db30bf771f6681add7025cfb399730af0a39c624d4b794aaf34bdfad910714c8d83bffae2ad1c07e86138cedef8cdf37825b28926f6570b103d74737c1ccf46c9cd04b2631c5ce10a74cec4e01eefb25d8808806cd80103985dce7cfbf4dfff4e440f379787480f936048d4a04bf4e9b89e2a7be30937277f97b59f0526bcbb859c407f9710ad21c3597bd8638d75ffc9e5073aabae79d80f464c6196378671ec334315ba32cc75fe49bd5d221544026ee69ddca78440b3d8b3826f4175f4d22d2e9029d03b7b6f45d02cc6455b9003c8e74dce65dd6bf47bd4e9454416a8ce9475ede00";
        File f = new File("C:\\key.ltl");
        FileOutputStream out = new FileOutputStream(f);
        try {
            for (int i=0; i<key.length(); i+=2) {
                String val = key.substring(i, i+2);
                int byt = Integer.parseInt(val, 16);
                out.write(byt);
            }
        } finally {
            out.close();
        }
    }

    public void testByteVector() {
        byte[] bytes = ByteUtil.convertBitStringToFastByteArray("11000000 10000001 10000000");
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        buffer.flip();
        putBytes(bytes, buffer);
        assertEquals(192, (buffer.get() & 0xFF));
        assertEquals(129, (buffer.get() & 0xFF));
        assertEquals(128, (buffer.get() & 0xFF));
        assertFalse(buffer.hasRemaining());
        bytes = ByteUtil.convertBitStringToFastByteArray("10000001 10000001 10000001");
        putBytes(bytes, buffer);
        assertEquals(bytes[0], buffer.get());
        assertEquals(bytes[1], buffer.get());
        assertEquals(bytes[2], buffer.get());
        assertFalse(buffer.hasRemaining());

    }

    private void putBytes(byte[] bytes, ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            buffer.clear();
            buffer.put(bytes);
            buffer.flip();
        }
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
        assertEquals(b("abcd"), apply("abc", tv(0, "d")));
        assertEquals(b("abed"), apply("abc", tv(1, "ed")));
        assertEquals(b("GEH6"), apply("", tv(0, "GEH6")));
        assertEquals(b("GEM6"), apply("GEH6", tv(2, "M6")));
        assertEquals(b("ESM6"), apply("GEM6", tv(-3, "ES")));
        assertEquals(b("RSESM6"), apply("ESM6", tv(-1, "RS")));
        assertEquals(b("RSESM6"), apply("RSESM6", tv(0, "")));
    }
    private String b(String string) {
        return ByteUtil.convertByteArrayToBitString(string.getBytes());
    }

    public void testIntToTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(2007, 0, 10, 14, 25, 12);
        cal.set(Calendar.MILLISECOND, 253);
        assertEquals(cal.getTime(), Util.toTimestamp(20070110142512253L));
    }
    private byte[] apply(String base, TwinValue diff) {
        return Util.applyDifference(new StringValue(base), diff);
    }
    private TwinValue tv(int sub, String diff) {
        return new TwinValue(new IntegerValue(sub), new ByteVectorValue(diff.getBytes()));
    }
    private ScalarValue diff(String base, String value) {
        return Util.getDifference(value.getBytes(), base.getBytes());
    }
}
