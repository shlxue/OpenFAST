/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.DecimalValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class DeltaDecimalOperator extends AlwaysPresentOperator {
	DeltaDecimalOperator() {
		super(DELTA, new Integer[] { Type.DECIMAL });
	}

	public ScalarValue getValueToEncode(ScalarValue val, ScalarValue priorVal, Scalar field) {
		if (priorVal == null) throw new IllegalStateException(Operator.ERR_D9);
		if (val == null)
			if (field.isOptional())
				return ScalarValue.NULL;
			else
				throw new IllegalArgumentException("Mandatory fields can't be null.");
		if (priorVal.isUndefined() && field.getDefaultValue().isUndefined()) return val;
		DecimalValue priorValue = priorVal.isUndefined() ? (DecimalValue) field.getDefaultValue() : (DecimalValue) priorVal;
		DecimalValue value = (DecimalValue) val;
		
		return new DecimalValue(value.mantissa - priorValue.mantissa, value.exponent - priorValue.exponent);
	}

	public ScalarValue decodeValue(ScalarValue val, ScalarValue priorVal, Scalar field) {
		if (priorVal == null)
			throw new IllegalStateException(Operator.ERR_D9);
		if (val == null) return null;
		DecimalValue priorValue = null;
		if (priorVal.isUndefined()) 
			if (field.getDefaultValue().isUndefined())
				priorValue = (DecimalValue) field.getInitialValue();
			else if (val == null) {
				if (field.isOptional())
					return ScalarValue.NULL;
				else
					throw new IllegalStateException("Field cannot be null.");
			}
			else
				priorValue = (DecimalValue) field.getDefaultValue();
		else
			priorValue = (DecimalValue) priorVal;
		DecimalValue value = (DecimalValue) val;
		return new DecimalValue(value.mantissa + priorValue.mantissa, value.exponent + priorValue.exponent);
	}

	public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
		if (field.getDefaultValue().isUndefined())
			if (field.isOptional())
				return ScalarValue.NULL;
			else if (previousValue.isUndefined())
				throw new IllegalStateException("Mandatory fields without a previous value or default value must be present.");
			else
				return previousValue;
		else
			return field.getDefaultValue();
	}
}