/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.Global;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class DeltaIntegerOperatorCodec extends AlwaysPresentOperatorCodec {
	DeltaIntegerOperatorCodec(Operator operator, Type[] types) {
		super(operator, types);
	}

	public ScalarValue getValueToEncode(ScalarValue value,
	    ScalarValue priorValue, Scalar field) {
	    if (priorValue == null) {
	    	Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field " + field + " must have a priorValue defined.");
	    	return null;
	    }
	
	    if (value == null) {
	        if (field.isOptional()) {
	            return ScalarValue.NULL;
	        } else {
	            throw new IllegalArgumentException(
	                "Mandatory fields can't be null.");
	        }
	    }
	
	    if (priorValue.isUndefined()) {
	        priorValue = field.getInitialValue();
	    }
	
	    return ((NumericValue) value).subtract((NumericValue) priorValue);
	}

	public ScalarValue decodeValue(ScalarValue newValue,
	    ScalarValue previousValue, Scalar field) {
	    if (previousValue == null) {
	    	Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field " + field + " must have a priorValue defined.");
	    	return null;
	    }
	
	    if ((newValue == null) || newValue.isNull()) {
	        return null;
	    }
	
	    if (previousValue.isUndefined()) {
	        if (field.getDefaultValue().isUndefined()) {
	            previousValue = field.getInitialValue();
	        } else {
	            previousValue = field.getDefaultValue();
	        }
	    }
	
	    return ((NumericValue) newValue).add((NumericValue) previousValue);
	}

	public ScalarValue decodeEmptyValue(ScalarValue previousValue,
	    Scalar field) {
	    if (previousValue.isUndefined()) {
	        if (field.getDefaultValue().isUndefined()) {
	            if (field.isOptional()) {
	                return ScalarValue.UNDEFINED;
	            } else {
	                Global.handleError(FastConstants.D5_NO_DEFAULT_VALUE,
	                    "");
	            }
	        } else {
	            return field.getDefaultValue();
	        }
	    }
	
	    return previousValue;
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}