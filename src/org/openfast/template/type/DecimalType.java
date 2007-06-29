/**
 * 
 */
package org.openfast.template.type;

import org.openfast.DecimalValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.operator.TwinOperator;
import org.openfast.template.type.codec.TypeCodec;

final class DecimalType extends SimpleType {
	DecimalType() {
		super("decimal", TypeCodec.SF_SCALED_NUMBER, TypeCodec.NULLABLE_SF_SCALED_NUMBER);
	}

	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (operator instanceof TwinOperator)
			return TypeCodec.TF_SCALED_NUMBER;
		return super.getCodec(operator, optional);
	}

	protected ScalarValue getVal(String value) {
		try {
			return new DecimalValue(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			Global.handleError(FastConstants.S3_INITIAL_VALUE_INCOMP, "The value \"" + value + "\" is not compatible with type " + this);
			return null;
		}
	}

	public ScalarValue getDefaultValue() {
		return new DecimalValue(0.0);
	}

	public boolean isValueOf(ScalarValue previousValue) {
		return previousValue instanceof DecimalValue;
	}
}