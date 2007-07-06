/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class TailOperatorCodec extends OperatorCodec {
	TailOperatorCodec(Operator operator, Type[] types) {
		super(operator, types);
	}

	public ScalarValue getValueToEncode(ScalarValue value,
	    ScalarValue priorValue, Scalar field) {
	    if (value == null) {
	        return ScalarValue.NULL;
	    }
	
	    if (priorValue == null) {
	        return value;
	    }
	
	    if (priorValue.isUndefined()) {
	        if (value.equals(field.getDefaultValue())) {
	            return null;
	        } else {
	            return value;
	        }
	    }
	
	    int index = 0;
	
	    for (;
	            ((StringValue) value).value.charAt(index) == ((StringValue) priorValue).value.charAt(
	                index); index++)
	        ;
	
	    return new StringValue(((StringValue) value).value.substring(
	            index));
	}

	public ScalarValue decodeValue(ScalarValue newValue,
	    ScalarValue previousValue, Scalar field) {
	    StringValue base;
	
	    if ((previousValue == null) && !field.isOptional()) {
	        Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT,
	            "");
	
	        return null;
	    } else if ((previousValue == null) ||
	            previousValue.isUndefined()) {
	        base = (StringValue) field.getInitialValue();
	    } else {
	        base = (StringValue) previousValue;
	    }
	
	    if ((newValue == null) || newValue.isNull()) {
	        if (field.isOptional()) {
	            return null;
	        } else {
	            throw new IllegalArgumentException("");
	        }
	    }
	
	    String delta = ((StringValue) newValue).value;
	    int length = Math.max(base.value.length() - delta.length(), 0);
	    String root = base.value.substring(0, length);
	
	    return new StringValue(root + delta);
	}

	public ScalarValue decodeEmptyValue(ScalarValue previousValue,
	    Scalar field) {
	    if (previousValue.isUndefined()) {
	        return field.getInitialValue();
	    }
	
	    return previousValue;
	}
}