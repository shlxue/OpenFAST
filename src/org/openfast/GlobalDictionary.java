package org.openfast;

import java.util.HashMap;
import java.util.Map;

import org.openfast.template.Group;

public class GlobalDictionary implements Dictionary {
	protected Map table = new HashMap();
	public ScalarValue lookup(Group template, String key) {
		if (!table.containsKey(key))
			return ScalarValue.UNDEFINED;
		return (ScalarValue) table.get(key);
	}
	public void store(Group template, String key, ScalarValue value) {
		table.put(key, value);
	}
	public void reset() {
		table.clear();
	}
}
