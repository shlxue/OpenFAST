/**
 * 
 */
package org.openfast.template.type;

import org.openfast.ScalarValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public abstract class SimpleType extends Type {
	private final TypeCodec codec;
	private final TypeCodec nullableCodec;
	
	public SimpleType (String typeName, TypeCodec codec, TypeCodec nullableCodec) {
		super(typeName);
		this.codec = codec;
		this.nullableCodec = nullableCodec;
	}
	
	/**
	 * Get the approprivate codec for the passed operator
	 * @param operator The operator object in which the codec is trying to get
	 * @param optional Determines if the Field is required or not for the data
	 * @return Returns the codec if the field is required
	 */
	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (optional)
			return nullableCodec;
		return codec;
	}
	
	/**
	 * @param value
	 * @return 
	 */
	public ScalarValue getValue(String value) {
		if (value == null) return null;
		return getVal(value);
	}

	protected abstract ScalarValue getVal(String value);
}