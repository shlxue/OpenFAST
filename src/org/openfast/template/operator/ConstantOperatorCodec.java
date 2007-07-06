/**
 * 
 */
package org.openfast.template.operator;

import org.openfast.BitVectorBuilder;
import org.openfast.FieldValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

final class ConstantOperatorCodec extends OperatorCodec {
	protected ConstantOperatorCodec(Operator operator, Type[] types) {
		super(operator, types);
	}

	/**
	 * 
	 */
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

	/**
	 * @param previousValue 
	 * @param field 
	 * @return
	 */
	public ScalarValue decodeEmptyValue(ScalarValue previousValue,
	    Scalar field) {
	    if (!field.isOptional()) {
	        return field.getDefaultValue();
	    }
	
	    return null;
	}

	/**
	 * @return Returns the passed optional boolean
	 */
	public boolean usesPresenceMapBit(boolean optional) {
	    return optional;
	}

	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
		throw new NotImplementedException();
	}
	
	public boolean canEncode(ScalarValue value, Scalar field) {
		if (field.isOptional() && value == null) return true;
		return field.getDefaultValue().equals(value);
	}
}