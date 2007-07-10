/**
 * 
 */
package org.openfast.template.type;

import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.template.type.codec.TypeCodec;

final class ByteVectorType extends SimpleType {
	ByteVectorType() {
		super("byte", TypeCodec.BYTE_VECTOR, TypeCodec.NULLABLE_BYTE_VECTOR_TYPE);
	}

	/**
	 * @param value
	 * @return  
	 */
	protected ScalarValue getVal(String value) {
		return new ByteVectorValue(value.getBytes());
	}

	/**
	 * @return Returns the default value
	 */
	public ScalarValue getDefaultValue() {
	    return new ByteVectorValue(new byte[] {  });
	}

	/**
	 * Determines if previousValue is of type ByteVectorValue
	 * @param previousValue The previous value of the Field, used in 
	 * determining the corresponding field value for the current
	 * message being decoded.
	 * @return Returns true if the previousValue is an instance of ByteVectorValue,
	 * false otherwise 
	 */
	public boolean isValueOf(ScalarValue previousValue) {
		return previousValue instanceof ByteVectorValue;
	}
}