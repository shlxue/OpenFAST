/**
 * 
 */
package org.openfast.template.type;

import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.template.type.codec.TypeCodec;

final class ByteVectorType extends SimpleType {
	ByteVectorType() {
		super("byte", TypeCodec.BYTE_VECTOR_TYPE, TypeCodec.NULLABLE_BYTE_VECTOR_TYPE);
	}

	protected ScalarValue getVal(String value) {
		return new ByteVectorValue(value.getBytes());
	}

	public ScalarValue getDefaultValue() {
	    return new ByteVectorValue(new byte[] {  });
	}

	public boolean isValueOf(ScalarValue previousValue) {
		return previousValue instanceof ByteVectorValue;
	}
}