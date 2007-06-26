package org.openfast.template.operator;

import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

public class CopyOperator extends OptionallyPresentOperator {
	protected CopyOperator() {
		super(COPY, Type.ALL_TYPES);
	}

	protected ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, ScalarValue defaultValue) {
		if (priorValue == ScalarValue.UNDEFINED && value.equals(defaultValue)) return null;
		return (value.equals(priorValue)) ? null : value;
	}

	protected ScalarValue getInitialValue(Scalar field) {
		if (!field.getDefaultValue().isUndefined()) return field.getDefaultValue();
		if (field.isOptional())	return null;
		FastConstants.handleError(FastConstants.NO_DEFAULT_VALUE, "");
		return null;
	}

	protected ScalarValue getEmptyValue(ScalarValue priorValue) {
		return priorValue;
	}

	public ScalarValue decodeValue(ScalarValue newValue, ScalarValue priorValue, Scalar field) {
		return newValue;
	}

}
