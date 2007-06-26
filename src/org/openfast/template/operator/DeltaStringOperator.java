/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.util.Util;

final class DeltaStringOperator extends AlwaysPresentOperator {
	DeltaStringOperator(String name, Integer[] types) {
		super(name, types);
	}

	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
		if (value == null) return ScalarValue.NULL;
		if (priorValue == null) throw new IllegalStateException(Operator.ERR_D9);
		ScalarValue base = (priorValue.isUndefined()) ?  field.getInitialValue() : priorValue;
		return Util.getDifference((StringValue) value, (StringValue) base);
	}

	public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
		if (newValue == null || newValue.isNull())
			return null;
		TwinValue diffValue = (TwinValue) newValue;
		ScalarValue base = (previousValue.isUndefined()) ? field.getInitialValue() : previousValue;
		return Util.applyDifference((StringValue) base, diffValue);
	}

	public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
		throw new IllegalStateException("As of FAST v1.1 Delta values must be present in stream");
	}
}