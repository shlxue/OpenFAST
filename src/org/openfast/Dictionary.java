package org.openfast;

import org.openfast.template.Group;

public interface Dictionary {
	public static final String TEMPLATE = "template";
	ScalarValue lookup (Group template, String key);
	void        store  (Group template, String key, ScalarValue valueToEncode);
	void reset();
}
