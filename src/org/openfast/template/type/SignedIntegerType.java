package org.openfast.template.type;

import org.openfast.template.type.codec.TypeCodec;

public class SignedIntegerType extends IntegerType {
	public SignedIntegerType(int numberBits) {
		super("int" + numberBits, getMin(numberBits), getMax(numberBits), TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER);
	}
}
