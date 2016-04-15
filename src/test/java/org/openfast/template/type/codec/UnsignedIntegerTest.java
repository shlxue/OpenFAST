package org.openfast.template.type.codec;

import org.openfast.template.LongValue;
import org.openfast.test.OpenFastTestCase;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnsignedIntegerTest extends OpenFastTestCase {

	public void testEncodeDecode() {
        assertEncodeDecode(i(127),   "11111111", TypeCodec.UINT);
        assertEncodeDecode(i(16383), "01111111 11111111", TypeCodec.UINT);
        assertEncodeDecode(i(5),     "10000101", TypeCodec.UINT);
        assertEncodeDecode(i(0),     "10000000", TypeCodec.UINT);
        assertEncodeDecode(i(942755), "00111001 01000101 10100011", TypeCodec.UINT);
        assertEncodeDecode(i(268435452), "01111111 01111111 01111111 11111100", TypeCodec.UINT);
        assertEncodeDecode(i(269435452), "00000001 00000000 00111101 00000100 10111100", TypeCodec.UINT);
        assertEncodeDecode(l(274877906943L), "00000111 01111111 01111111 01111111 01111111 11111111", TypeCodec.UINT);
        assertEncodeDecode(l(1181048340000L), "00100010 00101111 01011111 01011101 01111100 10100000", TypeCodec.UINT);
        
        assertEncodeDecode(l(4294967295L), "00001111 01111111 01111111 01111111 11111111", TypeCodec.UINT);
    }

    public void testSomeBoundValues() throws Exception {
        List<Integer> intBounds = Arrays.asList(/* 128, */ 16384, 2097152, 268435456);
        List<Long> longBounds = new ArrayList<Long>(Arrays.asList(34359738368L, 4398046511104L, 562949953421312L, 72057594037927936L));
        for (int i : intBounds) {
            longBounds.add((long) i);
        }
        List<Long> values = new ArrayList<Long>(longBounds.size() * 3);
        for (long i : longBounds) {
            values.add(i - 1);
            values.add(i);
            values.add(i + 1);
        }
        for (long i : values) {
            byte[] bytes = TypeCodec.UINT.encode(new LongValue(i));
            if (longBounds.indexOf(i) == -1) {
                assertEquals(i, TypeCodec.UINT.decode(new ByteArrayInputStream(bytes)).toLong());
            } else {
                assertTrue(i != TypeCodec.UINT.decode(new ByteArrayInputStream(bytes)).toLong());
            }
        }
    }
}
