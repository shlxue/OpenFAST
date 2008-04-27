package org.openfast.template;

import java.io.Serializable;

import org.openfast.FieldValue;

public interface ComposedValueConverter extends Serializable {
	FieldValue[] split(FieldValue value);
	FieldValue compose(FieldValue[] values);
}
