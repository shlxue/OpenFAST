package org.openfast;

import java.util.HashMap;
import java.util.Map;

import org.openfast.template.Group;

public class ApplicationTypeDictionary implements Dictionary {

	private Map dictionary = new HashMap();

	public ScalarValue lookup(Group template, QName key, String applicationType) {
		if (dictionary.containsKey(template.getTypeReference())) {
			Map applicationTypeMap = (Map) dictionary.get(template.getTypeReference());
			if (applicationTypeMap.containsKey(key))
				return (ScalarValue) applicationTypeMap.get(key);
		}
		return ScalarValue.UNDEFINED;
	}

	public void reset() {
		dictionary = new HashMap();
	}

	public void store(Group group, String applicationType, QName key, ScalarValue value) {
		if (!dictionary.containsKey(group.getTypeReference())) {
			dictionary.put(group.getTypeReference(), new HashMap());
		}
		Map applicationTypeDictionary = (Map) dictionary.get(group.getTypeReference());
		applicationTypeDictionary.put(key, value);
	}

}
