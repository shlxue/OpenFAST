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
	
	public ScalarValue getVal(String value) {
		return new StringValue(value);
	}
	
	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (operator.equals(Operator.DELTA_STRING))
			return (optional) ? TypeCodec.NULLABLE_STRING_DELTA : TypeCodec.STRING_DELTA;
		return super.getCodec(operator, optional);
	}

    /**
     * @return Returns a new StringValue object with empty string as the value
     */
    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }

	public boolean isValueOf(ScalarValue previousValue) {
		return previousValue instanceof StringValue;
	}
}