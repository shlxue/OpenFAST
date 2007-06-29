package org.openfast.template.type;

import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class UnsignedIntegerType extends IntegerType {

	public UnsignedIntegerType(int numberBits) {
		super("uInt" + numberBits, 0, getMax(numberBits), TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER);
	}

	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (operator.equals(Operator.DELTA_INTEGER))
			if (optional)
				return TypeCodec.NULLABLE_INTEGER;
			else
				return TypeCodec.INTEGER;
		return super.getCodec(operator, optional);
	}
}
