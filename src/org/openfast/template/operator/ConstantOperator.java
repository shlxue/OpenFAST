/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.BitVectorBuilder;
import org.openfast.FieldValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

final class ConstantOperator extends Operator {
	protected ConstantOperator(String name, String[] types) {
		super(name, types);
	}

	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field, BitVectorBuilder presenceMapBuilder) {
		if (field.isOptional())
			presenceMapBuilder.setOnValueSkipOnNull(value);
	    return null; // Never encode constant value.
	}

	public ScalarValue decodeValue(ScalarValue newValue,
	    ScalarValue previousValue, Scalar field) {
	    return field.getDefaultValue();
	}

	public boolean isPresenceMapBitSet(byte[] encoding,
	    FieldValue fieldValue) {
	    return fieldValue != null;
	}

	public ScalarValue decodeEmptyValue(ScalarValue previousValue,
	    Scalar field) {
	    if (!field.isOptional()) {
	        return field.getDefaultValue();
	    }
	
	    return null;
	}

	public boolean usesPresenceMapBit(boolean optional) {
	    return optional;
	}

	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
		throw new NotImplementedException();
	}
}