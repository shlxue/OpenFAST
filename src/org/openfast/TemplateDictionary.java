package org.openfast;

import java.util.HashMap;
import java.util.Map;

import org.openfast.template.Group;

public class TemplateDictionary implements Dictionary {
	
	protected Map table = new HashMap();

	public ScalarValue lookup(Group template, String key) {
		if (!table.containsKey(template))
			return ScalarValue.UNDEFINED;
		if (((Map)table.get(template)).containsKey(key))
			return (ScalarValue) ((Map)table.get(template)).get(key);
		return ScalarValue.UNDEFINED;
	}

	public void reset() {
		table.clear();
	}

	public void store(Group template, String key, ScalarValue valueToEncode) {
		if (!table.containsKey(template))
		{
			table.put(template, new HashMap());
		}
		((Map) table.get(template)).put(key, valueToEncode);
	}

}
