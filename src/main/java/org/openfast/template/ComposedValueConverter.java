package org.openfast.template;

import org.openfast.FieldValue;

public interface ComposedValueConverter {
	FieldValue[] split(FieldValue value);
	FieldValue compose(FieldValue[] values);
}
