package org.openfast.template.type;

import org.openfast.template.type.codec.TypeCodec;

public class SignedIntegerType extends IntegerType {
	private static final long serialVersionUID = 1L;

	public SignedIntegerType(int numberBits) {
		super("int" + numberBits, getMin(numberBits), getMax(numberBits), TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER);
	}
}
