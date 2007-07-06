/**
 * 
 */
package org.openfast.template.type;

import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class StringType extends SimpleType {
	public StringType(String typeName, TypeCodec codec, TypeCodec nullableCodec) {
		super(typeName, codec, nullableCodec);
	}
	
	/**
	 * @param value
	 * @return 
	 */
	public ScalarValue getVal(String value) {
		return new StringValue(value);
	}
	
	/**
	 * Get the approprivate codec for the passed operator
	 * @param operator The operator object in which the codec is trying to get
	 * @param optional Determines if the Field is required or not for the data
	 * @return Returns the codec if the field is required
	 */
	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (operator == Operator.DELTA)
			return (optional) ? TypeCodec.NULLABLE_STRING_DELTA : TypeCodec.STRING_DELTA;
		return super.getCodec(operator, optional);
	}

    /**
     * @return Returns a new StringValue object with empty string as the value
     */
    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }

    /**
     * @param previousValue The previous value of the Field, used in 
	 * determining the corresponding field value for the current
	 * message being decoded.
     * @return Returns true if the passed value is an instance of an integer or long
     */
	public boolean isValueOf(ScalarValue previousValue) {
		return previousValue instanceof StringValue;
	}
}