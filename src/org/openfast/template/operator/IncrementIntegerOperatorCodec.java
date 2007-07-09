/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class IncrementIntegerOperatorCodec extends OperatorCodec {
	IncrementIntegerOperatorCodec(Operator operator, Type[] types) {
		super(operator, types);
	}

	public ScalarValue getValueToEncode(ScalarValue value,
	    ScalarValue priorValue, Scalar field) {
	    if (priorValue == null) {
	        return value;
	    }
	
	    if (value == null) {
	        if (field.isOptional()) {
	            if (priorValue == ScalarValue.UNDEFINED) {
	                return null;
	            }
	
	            return ScalarValue.NULL;
	        } else {
	            throw new IllegalArgumentException();
	        }
	    }
	
	    if (priorValue.isUndefined()) {
	        if (value.equals(field.getDefaultValue())) {
	            return null;
	        } else {
	            return value;
	        }
	    }
	
	    if (!value.equals(((NumericValue) priorValue).increment())) {
	        return value;
	    }
	
	    return null;
	}

	public ScalarValue decodeValue(ScalarValue newValue,
	    ScalarValue previousValue, Scalar field) {
	    return newValue;
	}

	public ScalarValue decodeEmptyValue(ScalarValue previousValue,
	    Scalar field) {
	    if ((previousValue == null) || previousValue.isUndefined()) {
	        if (field.getDefaultValue().isUndefined()) {
	            if (field.isOptional()) {
	                return null;
	            } else {
	                throw new IllegalStateException(
	                    "Field with operator increment must send a value if no previous value existed.");
	            }
	        } else {
	            return field.getDefaultValue();
	        }
	    }
	
	    return ((NumericValue) previousValue).increment();
	}
}