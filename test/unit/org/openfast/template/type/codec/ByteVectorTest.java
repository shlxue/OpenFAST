package org.openfast.template.type.codec;

import org.openfast.template.type.codec.TypeCodec;
import org.openfast.test.OpenFastTestCase;

public class ByteVectorTest extends OpenFastTestCase {

	public void testEncode() {
		assertEncodeDecode(byt(new byte[] { 0x00 }), "10000001 00000000", TypeCodec.BYTE_VECTOR);
		assertEncodeDecode(byt(new byte[] { 0x00, 0x7f }), "10000010 00000000 01111111", TypeCodec.BYTE_VECTOR);
		assertEncodeDecode(byt(new byte[] { 0x01, 0x02, 0x04, 0x08, 0x10 }), "10000101 00000001 00000010 00000100 00001000 00010000", TypeCodec.BYTE_VECTOR);
		assertEncodeDecode(byt(new byte[] { 0x16, 0x32, 0x64, 0x0f }), "10000100 00010110 00110010 01100100 00001111", TypeCodec.BYTE_VECTOR);
		assertEncodeDecode(byt(new byte[] { 0x57, 0x4e }), "10000010 01010111 01001110", TypeCodec.BYTE_VECTOR);
	}

}
