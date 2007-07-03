package org.openfast.template.type;

import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class UnsignedIntegerType extends IntegerType {

	public UnsignedIntegerType(int numberBits) {
		super("uInt" + numberBits, 0, getMax(numberBits), TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER);
	}

	/**
	 * Get the approprivate codec for the passed operator
	 * @param operator The operator object in which the codec is trying to get
	 * @param optional Determines if the Field is required or not for the data
	 * @return Returns the codec if the field is required
	 */
	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (operator.equals(Operator.DELTA_INTEGER))
			if (optional)
				return TypeCodec.NULLABLE_INTEGER;
			else
				return TypeCodec.INTEGER;
		return super.getCodec(operator, optional);
	}
}
