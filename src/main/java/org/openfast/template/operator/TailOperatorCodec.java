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
	private static final long serialVersionUID = 1L;

	TailOperatorCodec(Operator operator, Type[] types) {
		super(operator, types);
	}

	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
	    if (value == null) {
	    	if (priorValue == null)
	    		return null;
	    	if (priorValue.isUndefined() && field.getDefaultValue().isUndefined())
	    		return null;
    		return ScalarValue.NULL;
	    }
	
	    if (priorValue == null) {
	        return value;
	    }
	
	    if (priorValue.isUndefined()) {
        	priorValue = field.getBaseValue();
	    }
	
	    int index = 0;
	
	    byte[] val = value.getBytes();
		byte[] prior = priorValue.getBytes();
		
		if (val.length > prior.length)
			return value;
		if (val.length < prior.length)
			Global.handleError(FastConstants.D3_CANT_ENCODE_VALUE, "The value " + val + " cannot be encoded by a tail operator with previous value " + priorValue);
		
		while (index < val.length && val[index] == prior[index])
			index++;
		if (val.length == index)
			return null;
	    return (ScalarValue) field.createValue(new String(val, index, val.length-index));
	}

	public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
	    StringValue base;
	
	    if ((previousValue == null) && !field.isOptional()) {
	        Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "");
	        return null;
	    } else if ((previousValue == null) || previousValue.isUndefined()) {
	        base = (StringValue) field.getBaseValue();
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

	public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
	    if (previousValue.isUndefined()) {
	    	if (field.getDefaultValue().isUndefined()) return null;
	        return field.getBaseValue();
	    }
	
	    return previousValue;
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}
}