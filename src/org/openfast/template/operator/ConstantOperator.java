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

final class ConstantOperator extends Operator {
	protected ConstantOperator(String name, Type[] types) {
		super(name, types);
	}

	/**
	 * @param value
	 * @param priorValue
	 * @param field
	 * @param presenceMapBuilder
	 * @return 
	 */
	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field, BitVectorBuilder presenceMapBuilder) {
		if (field.isOptional())
			presenceMapBuilder.setOnValueSkipOnNull(value);
	    return null; // Never encode constant value.
	}

	/**
	 * 
	 * @param newValue The new value of the Field, used in the comparing with the 
	 * previousValue.  
	 * @param previousValue The previous value of the Field, used in 
	 * determining the corresponding field value for the current
	 * message being decoded.
	 * @return Returns the default ScalarValue of the passed Scalar object
	 */
	public ScalarValue decodeValue(ScalarValue newValue,
	    ScalarValue previousValue, Scalar field) {
	    return field.getDefaultValue();
	}

	/**
	 * @return Returns true if the passed fieldValue isn't null, false otherwise
	 */
	public boolean isPresenceMapBitSet(byte[] encoding,
	    FieldValue fieldValue) {
	    return fieldValue != null;
	}

	/**
	 * Get the default value of the Scalar object 
	 * @param previousValue The previous value of the Field, used in 
	 * determining the corresponding field value for the current
	 * message being decoded.
	 * @param field The Scalar object to get the default value from
	 * @return If the field is required, return the default ScalarValue, otherwise
	 * return null
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
}