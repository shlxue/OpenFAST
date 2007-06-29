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
	
	public TypeCodec getCodec(Operator operator, boolean optional) {
		if (optional)
			return nullableCodec;
		return codec;
	}
	
	public ScalarValue getValue(String value) {
		if (value == null) return null;
		return getVal(value);
	}

	protected abstract ScalarValue getVal(String value);
}