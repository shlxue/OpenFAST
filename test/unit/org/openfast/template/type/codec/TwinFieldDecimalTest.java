package org.openfast.template.type.codec;

import org.openfast.test.OpenFastTestCase;

public class TwinFieldDecimalTest extends OpenFastTestCase {

	public void testEncodeDecode() {
		assertEncodeDecode(twin(i(0), i(4)), "10000000 10000100", TypeCodec.TF_SCALED_NUMBER);
		assertEncodeDecode(twin(i(2), i(4)), "10000010 10000100", TypeCodec.TF_SCALED_NUMBER);
		assertEncodeDecode(twin(i(-1), i(4)), "11111111 10000100", TypeCodec.TF_SCALED_NUMBER);
		assertEncodeDecode(twin(i(3), i(-1)), "10000011 11111111", TypeCodec.TF_SCALED_NUMBER);
		assertEncodeDecode(twin(i(0), i(702)), "10000000 00000101 10111110", TypeCodec.TF_SCALED_NUMBER);
		assertEncodeDecode(twin(i(0), i(-53)), "10000000 11001011", TypeCodec.TF_SCALED_NUMBER);
	}

}
